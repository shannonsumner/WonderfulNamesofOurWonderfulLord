package com.shannonsumner.namesofjesus.data

import androidx.room.Entity

@Entity(tableName = "verses", primaryKeys = ["book", "chapter", "verse"])
data class BibleVerse(
    val book: Int,
    val chapter: Int,
    val verse: Int,
    val text: String
)
