package com.sjm.filemap.screens

import android.os.Environment
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.io.File

class FileListViewModel : ViewModel() {
    var curDirectory: File by mutableStateOf(Environment.getExternalStorageDirectory()!!)
        private set
    val files = mutableStateListOf<File>()
    private val fileStack: ArrayDeque<File> = ArrayDeque()
    private val sizeMap = mutableMapOf<String, Long>()
    private var totalSize: Long = 0

    init {
        updateFiles()
        totalSize = calcAllFoldersSizes(curDirectory)
    }

    fun enterDirectory(file: File) {
        fileStack.addLast(curDirectory)
        curDirectory = file
        updateFiles()
    }

    fun exitDirectory(onBackInRootDir: () -> Unit) {
        if (fileStack.isEmpty()) {
            onBackInRootDir()
            return
        }
        curDirectory = fileStack.last()
        fileStack.removeLast()
        updateFiles()
    }

    private fun updateFiles() {
        files.clear()
        files.addAll(curDirectory.listFiles()!!)
        sortFilesByName()
    }

    private fun sortFilesByName() {
        //TODO: sort by size
        files.sortWith(compareBy<File> { it.isFile }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.name })
    }

    private fun calcAllFoldersSizes(file: File): Long {
        val files = file.listFiles()
        if (files.isNullOrEmpty()) return 0
        var size: Long = 0

        for (f in files) {
            size += if (f.isDirectory) {
                calcAllFoldersSizes(f)
            } else {
                f.length()
            }
        }
        sizeMap[file.absolutePath] = size
        return size
    }

    fun getSizeOf(file: File) = sizeMap[file.absolutePath] ?: 0

    fun logMap() {
        Log.d("SizeMap", sizeMap.toString())
        Log.d("map len", "${sizeMap.size}")
    }
}
