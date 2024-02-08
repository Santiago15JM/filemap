package com.sjm.filemap.screens.filelist

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sjm.filemap.screens.LoadingScreen
import com.sjm.filemap.ui.theme.*
import com.sjm.filemap.utils.StorageM
import com.sjm.filemap.utils.getMimeType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun FileList(vm: FileListViewModel = viewModel(), selectionVM: SelectionViewModel = viewModel()) {
    val activity = LocalContext.current as Activity
    val listState = rememberLazyListState()
    val cs = rememberCoroutineScope()

    Scaffold(topBar = {
        //TODO Breadcrumb
        TopAppBar(backgroundColor = Background, contentColor = White) {
            Row {
                Column(Modifier.weight(4F)) {
                    Text(
                        text = vm.curDirectory.path,
                        textAlign = TextAlign.Start,
                    )
                }
                Column(Modifier.weight(1F)) {
                    Text(text = getAppropriateSize(StorageM.getSizeOf(vm.curDirectory)))
                }
            }
        }
    }, bottomBar = {
        ActionToolbar(selectionVM.isActive(), onBack = {
            exitDir(vm, selectionVM, { activity.finish() }, cs, listState)
        })
    }) { pv ->
        BackHandler {
            exitDir(vm, selectionVM, { activity.finish() }, cs, listState)
        }

        Box(
            Modifier
                .padding(pv)
                .fillMaxSize()
        ) {
            InfoPanel()

            LazyColumn(state = listState, modifier = Modifier.matchParentSize()) {
                item { Spacer(modifier = Modifier.padding(120.dp)) }
                items(items = vm.files, key = { f -> f.path }) { f ->
                    FileItem(file = f,
                        size = if (f.isDirectory) StorageM.getSizeOf(f) else f.length(),
                        onDirClick = { enterDir(vm, selectionVM, f, cs, listState) },
                        onFileClick = { vm.openFileInExtApp(f, activity) },
                        selectionActive = { selectionVM.isActive() },
                        onSelect = { selectionVM.addSelectedFile(f) },
                        onDeselect = { selectionVM.removeSelectedFile(f) })
                }
            }
        }
    }
}

//TODO: Maybe improve
private fun enterDir(
    vm: FileListViewModel,
    selection: SelectionViewModel,
    f: File,
    cs: CoroutineScope,
    ls: LazyListState
) {
    vm.enterDirectory(f)
    selection.clear()
    cs.launch { ls.scrollToItem(0) }
}

private fun exitDir(
    vm: FileListViewModel,
    selection: SelectionViewModel,
    onBackInRoot: () -> Unit,
    cs: CoroutineScope,
    ls: LazyListState
) {
    val index = vm.lastFolderIndex
    vm.exitDirectory { onBackInRoot() }
    selection.clear()
    cs.launch { ls.scrollToItem(index) }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileItem(
    file: File,
    size: Long,
    onDirClick: () -> Unit,
    onFileClick: () -> Unit,
    selectionActive: () -> Boolean,
    onSelect: () -> Unit,
    onDeselect: () -> Unit,
) {
    var selected by rememberSaveable { mutableStateOf(false) }
    if (!selectionActive()) selected = false
    val haptic = LocalHapticFeedback.current
    Surface(shape = RoundedCornerShape(30.dp),
        color = if (selected) SelectionColor else MaterialTheme.colors.surface,
        elevation = 2.dp,
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    if (!selectionActive()) {
                        if (file.isDirectory) {
                            onDirClick()
                        } else {
                            onFileClick()
                        }
                    } else {
                        if (selected) {
                            selected = false
                            onDeselect()
                        } else {
                            selected = true
                            onSelect()
                        }
                    }
                }, onLongPress = {
                    if (!selected and !selectionActive()) {
                        selected = true
                        onSelect()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                })
            }) {
        Row(
            Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = file.name, maxLines = 1, modifier = Modifier
                    .weight(1F)
                    .basicMarquee()
            )
            Spacer(Modifier.width(5.dp))
            Text(
                getAppropriateSize(size), fontSize = 14.sp, maxLines = 1, textAlign = TextAlign.End
            )
        }
    }
}

fun getAppropriateSize(size: Long): String {
    var count = 0
    var s = size.toDouble()
    while (s > 1024 && count < 4) {
        s /= 1024
        count++
    }
    val unit = when (count) {
        1 -> "KB"
        2 -> "MB"
        3 -> "GB"
        else -> "B"
    }
    return "${String.format("%.2f", s)} $unit"
}
