package com.sjm.filemap.screens

import androidx.compose.runtime.Composable
import com.sjm.filemap.screens.filelist.FileList
import com.sjm.filemap.utils.StorageM

@Composable
fun FileExplorer() {
    if (StorageM.doneCalc) {
        FileList()
    } else {
        LoadingScreen()
    }
}
