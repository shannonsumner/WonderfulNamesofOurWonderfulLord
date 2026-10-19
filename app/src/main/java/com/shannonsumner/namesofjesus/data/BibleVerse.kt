package com.shannonsumner.namesofjesus.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "verses",
    indices = [
        Index(value = ["book"], name = "ixb"),
        Index(value = ["chapter"], name = "ixc"),
        Index(value = ["verse"], name = "ixv"),
        Index(value = ["book", "chapter", "verse"], name = "ixbcv")
    ]
)
data class BibleVerse(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val book: Int,
    val chapter: Int,
    val verse: Int,
    val text: String
)
