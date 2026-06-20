package com.example.data.repository

import android.util.Log
import com.example.data.local.ActivityDao
import com.example.data.local.MealDao
import com.example.data.local.UserDao
import com.example.data.model.ActivityLog
import com.example.data.model.MealLog
import com.example.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class NutritionRepository(
    private val userDao: UserDao,
    private val mealDao: MealDao,
    private val activityDao: ActivityDao
) {
    private val TAG = "NutritionRepository"
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    // State flows from Local Room DB
    val userProfile: Flow<UserProfile?> = userDao.getUserProfileFlow()
    val allMeals: Flow<List<MealLog>> = mealDao.getAllMeals()
    val allActivities: Flow<List<ActivityLog>> = activityDao.getAllActivities()

    fun getMealsToday(startOfDay: Long): Flow<List<MealLog>> = mealDao.getMealsToday(startOfDay)
    fun getActivitiesToday(startOfDay: Long): Flow<List<ActivityLog>> = activityDao.getActivitiesToday(startOfDay)

    /**
     * Inserts/updates User Profile in local DB and syncs to Firestore
     */
    suspend fun saveUserProfile(profile: UserProfile) {
        userDao.insertUserProfile(profile)
        syncProfileToFirestore(profile)
    }

    /**
     * Inserts food meal logging local DB and syncs to Firestore
     */
    suspend fun logMeal(meal: MealLog) {
        mealDao.insertMeal(meal)
        syncMealToFirestore(meal)
    }

    /**
     * Deletes a logged food item
     */
    suspend fun deleteMeal(meal: MealLog) {
        mealDao.deleteMeal(meal)
        deleteMealFromFirestore(meal)
    }

    /**
     * Inserts active step syncing / burned calorie activity into local DB and syncs to Firestore
     */
    suspend fun logActivity(activity: ActivityLog) {
        activityDao.insertActivity(activity)
        syncActivityToFirestore(activity)
    }

    /**
     * Sync local Room data list directly to/from Firestore on successful login
     */
    suspend fun syncLocalDataWithFirestore() {
        val uid = auth.currentUser?.uid ?: return
        try {
            // Update profile from Firestore if it exists
            val profileDoc = firestore.collection("users").document(uid).get().await()
            if (profileDoc.exists()) {
                val age = (profileDoc.getLong("age") ?: 25).toInt()
                val weight = (profileDoc.getDouble("weightKg") ?: 70.0).toFloat()
                val height = (profileDoc.getDouble("heightCm") ?: 175.0).toFloat()
                val baseGoal = (profileDoc.getLong("baseCalorieGoal") ?: 2000).toInt()
                val waterGoal = (profileDoc.getLong("waterGoalMl") ?: 2500).toInt()
                val stepsGoal = (profileDoc.getLong("stepGoal") ?: 10000).toInt()
                val name = profileDoc.getString("name") ?: "Super User"
                val email = profileDoc.getString("email") ?: "firebase@aistudio.com"

                val profile = UserProfile(
                    id = "current_user",
                    email = email,
                    name = name,
                    age = age,
                    weightKg = weight,
                    heightCm = height,
                    baseCalorieGoal = baseGoal,
                    waterGoalMl = waterGoal,
                    stepGoal = stepsGoal
                )
                userDao.insertUserProfile(profile)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sync error", e)
        }
    }

    private fun syncProfileToFirestore(profile: UserProfile) {
        val uid = auth.currentUser?.uid ?: return
        val data = hashMapOf(
            "name" to profile.name,
            "email" to profile.email,
            "age" to profile.age,
            "weightKg" to profile.weightKg,
            "heightCm" to profile.heightCm,
            "baseCalorieGoal" to profile.baseCalorieGoal,
            "waterGoalMl" to profile.waterGoalMl,
            "stepGoal" to profile.stepGoal
        )
        firestore.collection("users").document(uid)
            .set(data)
            .addOnFailureListener { Log.e(TAG, "Profile Firestore sync failed", it) }
    }

    private fun syncMealToFirestore(meal: MealLog) {
        val uid = auth.currentUser?.uid ?: return
        val data = hashMapOf(
            "id" to meal.id,
            "foodName" to meal.foodName,
            "calories" to meal.calories,
            "proteinG" to meal.proteinG,
            "carbsG" to meal.carbsG,
            "fatG" to meal.fatG,
            "mealType" to meal.mealType,
            "timestamp" to meal.timestamp
        )
        firestore.collection("users").document(uid)
            .collection("meals").document(meal.id.toString())
            .set(data)
            .addOnFailureListener { Log.e(TAG, "Meal Firestore sync failed", it) }
    }

    private fun deleteMealFromFirestore(meal: MealLog) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .collection("meals").document(meal.id.toString())
            .delete()
            .addOnFailureListener { Log.e(TAG, "Meal Firestore deletion failed", it) }
    }

    private fun syncActivityToFirestore(activity: ActivityLog) {
        val uid = auth.currentUser?.uid ?: return
        val data = hashMapOf(
            "id" to activity.id,
            "source" to activity.source,
            "steps" to activity.steps,
            "activeCaloriesBurned" to activity.activeCaloriesBurned,
            "durationMinutes" to activity.durationMinutes,
            "timestamp" to activity.timestamp
        )
        firestore.collection("users").document(uid)
            .collection("activities").document(activity.id.toString())
            .set(data)
            .addOnFailureListener { Log.e(TAG, "Activity Firestore sync failed", it) }
    }
}
