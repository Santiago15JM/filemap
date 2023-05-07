package com.sjm.filemap.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.io.File

class SelectionViewModel : ViewModel() {
    private val selection = mutableStateListOf<File>()

    fun getSelectionSize(sizeMap: MutableMap<String, Long>): Long {
        var size = 0L
        selection.forEach {
            size += if (it.isFile) it.length() else sizeMap[it.absolutePath] ?: 0
        }
        return size
    }

    fun addSelectedFile(file: File) {
        selection.add(file)
    }

    fun removeSelectedFile(file: File) {
        selection.remove(file)
    }

    fun clear() = selection.clear()

    fun isActive() = selection.isNotEmpty()

    fun size() = selection.size

    fun getFirst() = selection.first()
}