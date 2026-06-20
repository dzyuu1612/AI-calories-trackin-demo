package com.example.data.local

import androidx.room.*
import com.example.data.model.MealLog
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meal_logs ORDER BY timestamp DESC")
    fun getAllMeals(): Flow<List<MealLog>>

    @Query("SELECT * FROM meal_logs WHERE timestamp >= :startOfDay ORDER BY timestamp DESC")
    fun getMealsToday(startOfDay: Long): Flow<List<MealLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealLog)

    @Delete
    suspend fun deleteMeal(meal: MealLog)

    @Query("DELETE FROM meal_logs WHERE id = :id")
    suspend fun deleteMealById(id: Int)
}
