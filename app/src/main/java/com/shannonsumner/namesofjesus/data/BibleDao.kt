package com.shannonsumner.namesofjesus.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface BibleDao {
    @Query("SELECT * FROM verses WHERE book = :book AND chapter = :chapter AND verse IN (:verses)")
    suspend fun getVerses(book: Int, chapter: Int, verses: List<Int>): List<BibleVerse>

    @Query("SELECT COUNT(*) FROM verses")
    suspend fun getVerseCount(): Int
}
