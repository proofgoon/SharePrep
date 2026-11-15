package com.shareprep.data

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/** Saves processed bitmaps into app-private storage. Originals are never touched. */
class ImageStorage(private val context: Context) {
    private val dir = File(context.filesDir, "processed").apply { mkdirs() }

    suspend fun save(bitmap: Bitmap): File = withContext(Dispatchers.IO) {
        val file = File(dir, "${UUID.randomUUID()}.jpg")
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it) }
        file
    }

    suspend fun delete(path: String) = withContext(Dispatchers.IO) { File(path).delete(); Unit }

    fun shareUri(path: String) = FileProvider.getUriForFile(
        context, "${context.packageName}.fileprovider", File(path)
    )
}
