package com.sjm.filemap.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun FileList(vm: FileListViewModel = viewModel()) {
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
        BottomAppBar {
            Button({ vm.exitDirectory { activity.finish() } }) {
                Text("Back")
            }
//            Button({
//                vm.openFileInExtApp(
//                    File("/storage/emulated/0/Download/pl.mp3"),
//                    activity
//                )
//            }) {
//                Text("Open")
//            }
//            Button({
//                val f = File("/storage/emulated/0/Download/pl.mp3")
//                Log.d("Type",f.getMimeType()?:"bug")
//            }) {
//                Text("getType")
//            }
        }
    }) {
        val listState = rememberLazyListState()
        val cs = rememberCoroutineScope()
        LazyColumn(contentPadding = it, state = listState) {
            items(vm.files) { f ->
                SimpleFile(f, if (f.isDirectory) vm.getSizeOf(f) else f.length(), {
                    vm.enterDirectory(f)
                    cs.launch { listState.scrollToItem(0) } //TODO: Scroll to last folder
                }, { vm.openFileInExtApp(f, activity) })
            }
        }
    }
    //TODO Request permission
}

@Composable
fun SimpleFile(file: File, size: Long, onDirClick: () -> Unit, onFileClick: () -> Unit) {
    Surface(shape = RoundedCornerShape(10.dp),
        elevation = 2.dp,
        modifier = if (file.isDirectory) Modifier.padding(4.dp).fillMaxWidth().clickable { onDirClick() }
        else Modifier.padding(4.dp).fillMaxWidth().clickable { onFileClick() }) {
        Row(Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(file.name)
            Spacer(Modifier.width(30.dp))
            Text(getAppropriateSize(size))
        }
    }
}

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


//@Composable
//fun Bubble(size: Dp) {
//    Surface(shape = CircleShape, modifier = Modifier.size(size)) {
//
//    }
//}