package com.shannonsumner.namesofjesus.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Daily")
data class DailyDevotional(
    @PrimaryKey
    @ColumnInfo(name = "day_of_year")
    val dayOfYear: Int,
    val topic: String,
    val scripture: String,
    val reference: String,
    val book: Int?,
    val chapter: Int?,
    val verse: String,
    val devo: String?,
    val activity: String?
)
