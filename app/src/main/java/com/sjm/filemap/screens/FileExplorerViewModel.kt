package com.sjm.filemap.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.sjm.filemap.R
import java.io.File

class FileExplorerViewModel : ViewModel() {
    var curDirectory: File by mutableStateOf(Environment.getExternalStorageDirectory()!!)
        private set
    val files = mutableStateListOf<File>()
    private val fileStack: ArrayDeque<File> = ArrayDeque()
    val sizeMap = mutableMapOf<String, Long>()
    private var totalSize: Long = 0
    val selection = mutableStateListOf<File>()

    init {
        totalSize = calcAllFoldersSizes(curDirectory)
        updateFiles()
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
        selection.clear()
    }

    //TODO: access Android/data and obb Folders
    private fun updateFiles() {
        files.clear()
        selection.clear()
        files.addAll(curDirectory.listFiles()!!)
        sortFilesBySize()
    }

    fun addSelectedFile(file: File) {
        selection.add(file)
    }

    fun removeSelectedFile(file: File) {
        selection.remove(file)
    }

    //TODO: Delete file

//    private fun sortFilesByName() {
//        files.sortWith(compareBy<File> { it.isFile }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.name })
//    }

    private fun sortFilesBySize() {
        files.sortWith(compareByDescending<File> { getSizeOf(it) }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.name })
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

    fun openFileInExtApp(file: File, c: Context) {
        if (file.isDirectory) return

        val fileUri = FileProvider.getUriForFile(c, "com.sjm.filemap.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(fileUri, file.getMimeType())
        try {
            c.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(c, c.getString(R.string.no_app_installed), Toast.LENGTH_SHORT).show()
        }
    }

}

fun File.getMimeType(): String? =
    MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
