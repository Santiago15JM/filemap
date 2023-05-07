package com.sjm.filemap.utils

import android.webkit.MimeTypeMap
import java.io.File

fun File.getMimeType(): String? =
    MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
