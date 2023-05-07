package com.sjm.filemap.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.sjm.filemap.R
import com.sjm.filemap.utils.StorageM
import com.sjm.filemap.utils.getMimeType
import java.io.File

class FileExplorerViewModel : ViewModel() {
    var curDirectory: File by mutableStateOf(StorageM.rootFile)
        private set
    val files = mutableStateListOf<File>()
    private val fileStack: ArrayDeque<File> = ArrayDeque()
    private var totalSize: Long = 0
    var lastFolderIndex = 0

    init {
        totalSize = StorageM.calcAllFoldersSizes(curDirectory)
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
        val lastFolder = curDirectory
        curDirectory = fileStack.last()
        fileStack.removeLast()
        updateFiles()
        lastFolderIndex = files.indexOf(lastFolder)
    }

    //TODO: access Android/data and obb Folders
    private fun updateFiles() {
        files.clear()
        files.addAll(curDirectory.listFiles()!!)
        sortFilesBySize()
    }

    //TODO: Delete file

    private fun sortFilesBySize() {
        files.sortWith(compareByDescending<File> { StorageM.getSizeOf(it) }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.name })
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
