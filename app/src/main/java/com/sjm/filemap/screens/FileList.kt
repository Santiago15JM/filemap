package com.sjm.filemap.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.sjm.filemap.ui.theme.*
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun FileList(vm: FileExplorerViewModel = viewModel()) {
    val activity = LocalContext.current as Activity
    BackHandler { vm.exitDirectory { activity.finish() } }
    Scaffold(topBar = {
        TopAppBar(backgroundColor = Background, contentColor = White) {
            Row {
                Column(Modifier.weight(4F)) {
                    Text(
                        text = vm.curDirectory.path,
                        textAlign = TextAlign.Start,
                    )
                }
                Column(Modifier.weight(1F)) {
                    Text(text = getAppropriateSize(vm.getSizeOf(vm.curDirectory)))
                }
            }
        }
    }, bottomBar = {
        ActionToolbar(onBack = { vm.exitDirectory { activity.finish() } })
    }) {
        val listState = rememberLazyListState()
        val cs = rememberCoroutineScope()

        Box(Modifier.padding(it).fillMaxSize()) {
            ActionPanel(vm.selection, vm.sizeMap)

            LazyColumn(state = listState, modifier = Modifier.matchParentSize()) {
                item { Spacer(modifier = Modifier.padding(120.dp)) }
                items(items = vm.files, key = { f -> f.path }) { f ->
                    SimpleFile(file = f,
                        size = if (f.isDirectory) vm.getSizeOf(f) else f.length(),
                        onDirClick = { vm.enterDirectory(f); cs.launch { listState.scrollToItem(0) } },
                        onFileClick = { vm.openFileInExtApp(f, activity) },
                        selectionIsEmpty = { vm.selection.isEmpty() },
                        onSelect = { vm.addSelectedFile(f) },
                        onDeselect = { vm.removeSelectedFile(f) })
                    //TODO: Scroll to last folder
                }
            }
        }
    }
}

@Composable
fun SimpleFile(
    file: File,
    size: Long,
    onDirClick: () -> Unit,
    onFileClick: () -> Unit,
    selectionIsEmpty: () -> Boolean,
    onSelect: () -> Unit,
    onDeselect: () -> Unit,
) {
    var selected by rememberSaveable { mutableStateOf(false) }
    if (selectionIsEmpty()) selected = false

    val haptic = LocalHapticFeedback.current

    Surface(shape = RoundedCornerShape(30.dp),
        color = if (selected) SelectionColor else MaterialTheme.colors.surface,
        elevation = 2.dp,
        modifier = Modifier.padding(5.dp).fillMaxWidth().pointerInput(Unit) {
            detectTapGestures(onTap = {
                if (selectionIsEmpty()) {
                    if (file.isDirectory) {
                        onDirClick()
                    } else {
                        onFileClick()
                    }
                } else {
                    if (selected) {
                        selected = false
                        onDeselect() //OnDeselect
                    } else {
                        selected = true
                        onSelect() //OnSelect
                    }
                }
            }, onLongPress = {
                if (!selected and selectionIsEmpty()) {
                    selected = true
                    onSelect()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            })
        }) {
        Row(Modifier.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(file.name)
            Text(getAppropriateSize(size))
        }
    }
}

@Composable
fun ActionToolbar(onBack: () -> Unit) {
    Surface(
        color = ActionToolbarBg,
        modifier = Modifier.fillMaxWidth().padding(5.dp, 10.dp), /*5.dp, 0.dp, 5.dp, 10.dp*/
        shape = RoundedCornerShape(30.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.padding(0.dp, 10.dp),
        ) {
            ToolbarButton({ onBack() }) {
                Text("Atrás")
            }
            ToolbarButton({}) {
                Text("Pegar")
            }
            ToolbarButton({}) {
                Text("Cortar")
            }
            ToolbarButton({}) {
                Text("Copiar")
            }
        }
    }
}

//TODO: Path InfoBar
@Composable
fun ActionPanel(selection: MutableList<File>, sizeMap: MutableMap<String, Long>) {
    val vm = ActionPanelViewModel(selection)
    AnimatedVisibility(
        visible = selection.isNotEmpty(),
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.zIndex(1F),
    ) {
        if (selection.isNotEmpty()) Surface(
            color = ActionPanelBg,
            shape = RoundedCornerShape(30.dp),
            elevation = 5.dp,
            modifier = Modifier.padding(5.dp),
        ) {
            Column {
                SelectionInfo(selection, vm.getSelectionSize(sizeMap))
                //TODO: Optimize viewModels
                Row(modifier = Modifier.fillMaxWidth().padding(15.dp), horizontalArrangement = Arrangement.End) {
                    ActionPanelButton(Icons.Outlined.Delete, "Delete", {})
                    ActionPanelButton(Icons.Outlined.Edit, "Rename", {})
                }
            }
        }
    }
}

@Composable
fun ActionPanelButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    Surface(shape = RoundedCornerShape(12.dp), modifier = Modifier.padding(5.dp)) {
        IconButton({ onClick() }, Modifier.size(40.dp)) { Icon(icon, description, Modifier.size(30.dp)) }
    }
}

@Composable
fun SelectionInfo(selection: MutableList<File>, selectionSize: Long) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxWidth().padding(20.dp).height(90.dp),
    ) {
        if (selection.size > 1) {
            Text(
                text = "Varios archivos",
                color = White,
                fontSize = 26.sp,
            )
            Text("Tamaño: ${getAppropriateSize(selectionSize)}", color = White)
        } else {
            val file = selection.first()
            Text(
                text = file.name,
                color = White,
                fontSize = 26.sp,
            )
            Text("Tamaño: ${getAppropriateSize(selectionSize)}", color = White)
            if (file.isDirectory) Text("Numero de archivos: ${file.listFiles()?.size ?: 0}", color = White)
            else Text("Tipo: ${file.getMimeType()}", color = White)
        }
    }
}

@Composable
fun ToolbarButton(
    onClick: () -> Unit, content: @Composable RowScope.() -> Unit,
) = Button(
    onClick,
    shape = RoundedCornerShape(20.dp),
    content = content,
    colors = ButtonDefaults.buttonColors(backgroundColor = ToolbarButtonColor, contentColor = White),
)

//@Composable
//fun Bubble(size: Dp) {
//    Surface(shape = CircleShape, modifier = Modifier.size(size)) {
//
//    }
//}

private fun getAppropriateSize(size: Long): String {
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
