package com.sjm.filemap.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun FileList(vm: FileExplorerViewModel = viewModel()) {
    val activity = LocalContext.current as Activity
    BackHandler { vm.exitDirectory { activity.finish() } }
    Scaffold(topBar = {
        TopAppBar {
            Row {
                Column(Modifier.weight(3F)) {
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
            ActionPanel(vm.selection.isNotEmpty())

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
    Surface(shape = RoundedCornerShape(10.dp),
        color = if (selected) Color(0xFF00DDFF) else MaterialTheme.colors.background,
        elevation = 2.dp,
        modifier = Modifier.padding(4.dp).fillMaxWidth().pointerInput(Unit) {
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
                }
            })
        }) {
        Row(Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
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
fun ActionPanel(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.zIndex(1F)
    ) {
        Surface(
            color = ActionPanelBg,
            shape = RoundedCornerShape(30.dp),
            elevation = 5.dp,
            modifier = Modifier.padding(5.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(20.dp),
            ) {
                Text("Actions Panel", color = White)
                Text("Actions Panel", color = White)
                Text("Actions Panel", color = White)
                Text("Actions Panel", color = White)
            }
        }
    }
}

@Composable
fun ToolbarButton(
    onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit,
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
