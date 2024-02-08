package com.sjm.filemap.utils

import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.io.File

object StorageM {
    var doneCalc by mutableStateOf(false)
        private set
    val rootFile: File = Environment.getExternalStorageDirectory()!!
    val sizeMap = mutableMapOf<String, Long>()
    private var totalSize: Long = 0

    fun init() {
        totalSize = calcAllFoldersSizes(rootFile)
        doneCalc = true
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
        if (size > 0) sizeMap[file.absolutePath] = size
        return size
    }

    fun getSizeOf(file: File): Long {
        return if (file.isDirectory) sizeMap[file.absolutePath] ?: 0
        else file.length()
    }

}