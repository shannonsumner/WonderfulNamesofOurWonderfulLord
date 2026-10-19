package com.shannonsumner.namesofjesus.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface DailyDevotionalDao {
    @Query("SELECT * FROM Daily WHERE day_of_year = :dayOfYear")
    suspend fun getByDayOfYear(dayOfYear: Int): DailyDevotional?

    @Query("SELECT * FROM Daily ORDER BY day_of_year")
    suspend fun getAll(): List<DailyDevotional>
}
