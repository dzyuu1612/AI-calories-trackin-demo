package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: String = "current_user",
    val email: String,
    val name: String,
    val age: Int,
    val weightKg: Float,
    val heightCm: Float,
    val gender: String = "male",
    val activityLevel: String = "moderately_active",
    val fitnessGoal: String = "maintain",
    val baseCalorieGoal: Int,
    val waterGoalMl: Int,
    val stepGoal: Int
)
