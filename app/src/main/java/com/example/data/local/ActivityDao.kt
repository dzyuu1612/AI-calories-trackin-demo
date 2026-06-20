package com.example.data.local

import androidx.room.*
import com.example.data.model.ActivityLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
    fun getAllActivities(): Flow<List<ActivityLog>>

    @Query("SELECT * FROM activity_logs WHERE timestamp >= :startOfDay ORDER BY timestamp DESC")
    fun getActivitiesToday(startOfDay: Long): Flow<List<ActivityLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityLog)

    @Delete
    suspend fun deleteActivity(activity: ActivityLog)
}
