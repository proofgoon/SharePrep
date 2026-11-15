package com.shareprep.data.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "images")
data class ProcessedImage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filePath: String,
    val createdAt: Long,
    val width: Int,
    val height: Int,
    val wasEdited: Boolean,
    /** null = in Share Ready; non-null = in Trash since this timestamp. */
    val trashedAt: Long? = null
)

@Entity(tableName = "tags", indices = [Index(value = ["name"], unique = true)])
data class Tag(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(collate = ColumnInfo.NOCASE) val name: String
)

@Entity(
    tableName = "image_tags",
    primaryKeys = ["imageId", "tagId"],
    foreignKeys = [
        ForeignKey(ProcessedImage::class, parentColumns = ["id"], childColumns = ["imageId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(Tag::class, parentColumns = ["id"], childColumns = ["tagId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("tagId")]
)
data class ImageTagCrossRef(val imageId: Long, val tagId: Long)

data class ImageWithTags(
    @Embedded val image: ProcessedImage,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(ImageTagCrossRef::class, parentColumn = "imageId", entityColumn = "tagId")
    )
    val tags: List<Tag>
)

data class TagWithCount(val id: Long, val name: String, val count: Int)
