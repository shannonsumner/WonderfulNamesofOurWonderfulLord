package com.shannonsumner.namesofjesus.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meta")
data class BibleMetadata(
    @PrimaryKey
    val field: String,
    val value: String?
)
