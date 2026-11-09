package com.shannonsumner.namesofjesus.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface DevotionalDao {
    @Query("SELECT * FROM Daily WHERE day_of_year = :dayOfYear")
    suspend fun getByDayOfYear(dayOfYear: Int): DevotionalDailyReading?

    @Query("SELECT * FROM Daily ORDER BY day_of_year")
    suspend fun getAll(): List<DevotionalDailyReading>
}
