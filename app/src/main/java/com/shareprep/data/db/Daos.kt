package com.shareprep.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Insert suspend fun insert(image: ProcessedImage): Long

    @Transaction
    @Query("SELECT * FROM images WHERE trashedAt IS NULL ORDER BY createdAt DESC")
    fun observeActive(): Flow<List<ImageWithTags>>

    @Transaction
    @Query(
        """SELECT DISTINCT i.* FROM images i
           JOIN image_tags it ON it.imageId = i.id
           WHERE i.trashedAt IS NULL AND it.tagId IN (:tagIds)
           ORDER BY i.createdAt DESC"""
    )
    fun observeActiveByAnyTag(tagIds: List<Long>): Flow<List<ImageWithTags>>

    @Transaction
    @Query("SELECT * FROM images WHERE trashedAt IS NOT NULL ORDER BY trashedAt DESC")
    fun observeTrashed(): Flow<List<ImageWithTags>>

    @Query("SELECT * FROM images WHERE id = :id")
    suspend fun get(id: Long): ProcessedImage?

    @Query("UPDATE images SET trashedAt = :ts WHERE id = :id")
    suspend fun setTrashed(id: Long, ts: Long?)

    @Query("DELETE FROM images WHERE id = :id")
    suspend fun delete(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRefs(refs: List<ImageTagCrossRef>)

    @Query("DELETE FROM image_tags WHERE imageId = :imageId")
    suspend fun clearTags(imageId: Long)

    @Query("SELECT tagId FROM image_tags WHERE imageId = :imageId")
    suspend fun tagIdsFor(imageId: Long): List<Long>
}

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: Tag): Long

    @Query("SELECT * FROM tags WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): Tag?

    @Query("SELECT * FROM tags ORDER BY name")
    fun observeAll(): Flow<List<Tag>>

    @Query(
        """SELECT t.id AS id, t.name AS name,
                  COUNT(i.id) AS count
           FROM tags t
           LEFT JOIN image_tags it ON it.tagId = t.id
           LEFT JOIN images i ON i.id = it.imageId AND i.trashedAt IS NULL
           GROUP BY t.id ORDER BY t.name"""
    )
    fun observeWithCounts(): Flow<List<TagWithCount>>

    @Query("DELETE FROM tags WHERE id = :id")
    suspend fun delete(id: Long)
}
