package com.shannonsumner.namesofjesus.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface BibleDao {
    @Query("SELECT * FROM verses WHERE book = :book AND chapter = :chapter AND verse = :verse")
    suspend fun getVerse(book: Int, chapter: Int, verse: Int): BibleVerse?

    @Query("SELECT * FROM verses WHERE book = :book AND chapter = :chapter ORDER BY verse")
    suspend fun getChapter(book: Int, chapter: Int): List<BibleVerse>

    @Query("""
        SELECT * FROM verses
        WHERE book = :book AND chapter = :chapter AND verse BETWEEN :firstVerse AND :lastVerse
        ORDER BY verse
    """)
    suspend fun getPassage(
        book: Int,
        chapter: Int,
        firstVerse: Int,
        lastVerse: Int
    ): List<BibleVerse>

    @Query("SELECT COUNT(*) FROM verses")
    suspend fun getVerseCount(): Int

    @Query("SELECT * FROM meta ORDER BY field")
    suspend fun getMetadata(): List<BibleMetadata>
}
