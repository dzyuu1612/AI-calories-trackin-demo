package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val source: String, // Fitbit Sync, Garmin Sync, Apple Health Sync, Manual, Samsung Health
    val steps: Int,
    val activeCaloriesBurned: Int,
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis()
)
