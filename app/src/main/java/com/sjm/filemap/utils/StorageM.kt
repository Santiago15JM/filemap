package com.sjm.filemap.utils

import android.os.Environment
import java.io.File

object StorageM {
    val rootFile: File = Environment.getExternalStorageDirectory()!!
    val sizeMap = mutableMapOf<String, Long>()

    init {
        calcAllFoldersSizes(rootFile)
    }

    fun calcAllFoldersSizes(file: File): Long {
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