package com.sjm.filemap.screens.filelist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sjm.filemap.utils.StorageM
import java.io.File

class SelectionViewModel : ViewModel() {
    private val selection = mutableStateListOf<File>()

    var showEditDialog by mutableStateOf(false)

    fun getSelectionSize(): Long {
        var size = 0L
        selection.forEach {
            size += if (it.isFile) it.length() else StorageM.sizeMap[it.absolutePath] ?: 0
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

    fun rename(name: String) {
        if (selection.size != 1) return
        val file = selection.first()
        if (file.renameTo(File(file.parent, name))) {
            clear()
        }
    }
}
