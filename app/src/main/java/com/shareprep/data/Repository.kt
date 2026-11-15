package com.shareprep.data

import android.graphics.Bitmap
import com.shareprep.data.db.AppDatabase
import com.shareprep.data.db.ImageTagCrossRef
import com.shareprep.data.db.ProcessedImage
import com.shareprep.data.db.Tag

class Repository(private val db: AppDatabase, private val storage: ImageStorage) {
    private val images = db.imageDao()
    private val tags = db.tagDao()

    val tagsFlow = tags.observeAll()
    val tagsWithCounts = tags.observeWithCounts()
    val trashed = images.observeTrashed()

    fun shareReady(filterTagIds: Set<Long>) =
        if (filterTagIds.isEmpty()) images.observeActive()
        else images.observeActiveByAnyTag(filterTagIds.toList())

    /** Saves the processed bitmap and returns the new image id (tags are assigned next). */
    suspend fun approve(bitmap: Bitmap, wasEdited: Boolean): Long {
        val file = storage.save(bitmap)
        return images.insert(
            ProcessedImage(
                filePath = file.absolutePath,
                createdAt = System.currentTimeMillis(),
                width = bitmap.width,
                height = bitmap.height,
                wasEdited = wasEdited
            )
        )
    }

    suspend fun setTags(imageId: Long, tagIds: Set<Long>) {
        images.clearTags(imageId)
        images.insertRefs(tagIds.map { ImageTagCrossRef(imageId, it) })
    }

    suspend fun tagIdsFor(imageId: Long) = images.tagIdsFor(imageId).toSet()

    /** Returns the tag id, reusing an existing tag with the same name (case-insensitive). */
    suspend fun createTag(name: String): Long? {
        val clean = name.trim()
        if (clean.isEmpty()) return null
        val id = tags.insert(Tag(name = clean))
        return if (id != -1L) id else tags.findByName(clean)?.id
    }

    suspend fun deleteTag(id: Long) = tags.delete(id)

    suspend fun moveToTrash(id: Long) = images.setTrashed(id, System.currentTimeMillis())
    suspend fun restore(id: Long) = images.setTrashed(id, null)

    suspend fun deleteForever(id: Long) {
        images.get(id)?.let { storage.delete(it.filePath) }
        images.delete(id)
    }

    fun shareUri(path: String) = storage.shareUri(path)
}
