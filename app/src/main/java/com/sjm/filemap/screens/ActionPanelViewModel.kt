package com.sjm.filemap.screens

import androidx.lifecycle.ViewModel
import java.io.File

class ActionPanelViewModel(private val selection: MutableList<File>) : ViewModel() {

    fun getSelectionSize(sizeMap: MutableMap<String, Long>): Long {
        var size = 0L
        selection.forEach {
            size += if (it.isFile) it.length() else sizeMap[it.absolutePath] ?: 0
        }
        return size
    }

}