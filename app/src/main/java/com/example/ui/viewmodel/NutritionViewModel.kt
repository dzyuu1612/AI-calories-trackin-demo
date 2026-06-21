package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.local.AppDatabase
import com.example.data.model.ActivityLog
import com.example.data.model.MealLog
import com.example.data.model.UserProfile
import com.example.data.repository.AuthRepository
import com.example.data.repository.NutritionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class NutritionViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val authRepo = AuthRepository(application)
    private val repository = NutritionRepository(
        database.userDao(),
        database.mealDao(),
        database.activityDao()
    )

    // Auth status exposing
    val currentUser = authRepo.currentUser
    val isUserSignedIn = combine(authRepo.currentUser, authRepo.mockSessionActive) { user, mockActive ->
        user != null || mockActive
    }
    val isRealFirebaseConfigured = authRepo.isRealFirebaseConfigured()

    val userEmail: String get() = authRepo.userEmail
    val userName: String get() = authRepo.userName

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _authSuccess = MutableStateFlow<Boolean>(false)
    val authSuccess: StateFlow<Boolean> = _authSuccess.asStateFlow()

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    // Dashboard daily metrics calculation
    private val startOfDay: Long
        get() = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    // Reactive streams from Repository
    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .map { it ?: createDefaultProfile() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = createDefaultProfile()
        )

    val todayMeals: StateFlow<List<MealLog>> = repository.getMealsToday(startOfDay)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val todayActivities: StateFlow<List<ActivityLog>> = repository.getActivitiesToday(startOfDay)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Water tracking helper (holds in persistent state flow for live session)
    private val _waterLoggedTodayMl = MutableStateFlow(1250) // starting pre-populated to avoid blank
    val waterLoggedTodayMl: StateFlow<Int> = _waterLoggedTodayMl.asStateFlow()

    // Wearable fitness simulation sync states
    private val _isWearableSyncing = MutableStateFlow(false)
    val isWearableSyncing: StateFlow<Boolean> = _isWearableSyncing.asStateFlow()

    private val _wearableSyncStatus = MutableStateFlow("Tap Sync to connect sensor")
    val wearableSyncStatus: StateFlow<String> = _wearableSyncStatus.asStateFlow()

    // AI states
    private val _aiRecommendation = MutableStateFlow("")
    val aiRecommendation: StateFlow<String> = _aiRecommendation.asStateFlow()

    private val _isAiPlanningLoading = MutableStateFlow(false)
    val isAiPlanningLoading: StateFlow<Boolean> = _isAiPlanningLoading.asStateFlow()

    private val _aiAdvice = MutableStateFlow("")
    val aiAdvice: StateFlow<String> = _aiAdvice.asStateFlow()

    private val _isAiAdviceLoading = MutableStateFlow(false)
    val isAiAdviceLoading: StateFlow<Boolean> = _isAiAdviceLoading.asStateFlow()

    // AI Food Scanner States
    data class ScannedFood(
        val dishName: String,
        val calories: Int,
        val protein: Float,
        val carbs: Float,
        val fat: Float,
        val analysis: String
    )

    // Biometric Weight Entry Timeline (Stored in SharedPreferences)
    data class WeightEntry(
        val weight: Float,
        val date: String,
        val timestamp: Long
    )

    private val sharedPrefs = application.getSharedPreferences("calories_tracker_prefs", android.content.Context.MODE_PRIVATE)

    private val _weightHistory = MutableStateFlow<List<WeightEntry>>(emptyList())
    val weightHistory: StateFlow<List<WeightEntry>> = _weightHistory.asStateFlow()

    // Custom prompt configuration fields matching the PWA Custom Prompt feature
    private val _customUserPrompt = MutableStateFlow<String>("")
    val customUserPrompt: StateFlow<String> = _customUserPrompt.asStateFlow()

    private val _customSystemInstruction = MutableStateFlow<String>("")
    val customSystemInstruction: StateFlow<String> = _customSystemInstruction.asStateFlow()

    // High-level safe AI settings (prevents prompt injection & hides raw prompts from UI)
    private val _cuisineFocusMode = MutableStateFlow("Vietnamese Focus")
    val cuisineFocusMode: StateFlow<String> = _cuisineFocusMode.asStateFlow()

    private val _verbosityLevel = MutableStateFlow("Detailed Analysis")
    val verbosityLevel: StateFlow<String> = _verbosityLevel.asStateFlow()

    private val _caloricAggressiveness = MutableStateFlow("Balanced Standard")
    val caloricAggressiveness: StateFlow<String> = _caloricAggressiveness.asStateFlow()

    // Debug Mode visual payload state
    private val _lastRawResponse = MutableStateFlow<String>("")
    val lastRawResponse: StateFlow<String> = _lastRawResponse.asStateFlow()

    private val _isScanningImage = MutableStateFlow(false)
    val isScanningImage: StateFlow<Boolean> = _isScanningImage.asStateFlow()

    private val _scannedFoodResult = MutableStateFlow<ScannedFood?>(null)
    val scannedFoodResult: StateFlow<ScannedFood?> = _scannedFoodResult.asStateFlow()

    private val _scanError = MutableStateFlow<String?>(null)
    val scanError: StateFlow<String?> = _scanError.asStateFlow()

    private fun loadWeightHistory() {
        val jsonStr = sharedPrefs.getString("logged_weight_history_json", null)
        if (jsonStr != null) {
            try {
                val list = mutableListOf<WeightEntry>()
                val array = org.json.JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        WeightEntry(
                            weight = obj.getDouble("weight").toFloat(),
                            date = obj.getString("date"),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
                _weightHistory.value = list
            } catch (e: Exception) {
                _weightHistory.value = getDefaultWeightEntries()
            }
        } else {
            val initial = getDefaultWeightEntries()
            _weightHistory.value = initial
            saveWeightHistoryList(initial)
        }
    }

    private fun getDefaultWeightEntries() = listOf(
        WeightEntry(74.5f, "Jun 15", System.currentTimeMillis() - 4 * 24 * 3600 * 1000L),
        WeightEntry(74.1f, "Jun 16", System.currentTimeMillis() - 3 * 24 * 3600 * 1000L),
        WeightEntry(73.8f, "Jun 17", System.currentTimeMillis() - 2 * 24 * 3600 * 1000L),
        WeightEntry(73.5f, "Jun 18", System.currentTimeMillis() - 1 * 24 * 3600 * 1000L)
    )

    private fun saveWeightHistoryList(list: List<WeightEntry>) {
        try {
            val array = org.json.JSONArray()
            for (entry in list) {
                val obj = org.json.JSONObject()
                obj.put("weight", entry.weight.toDouble())
                obj.put("date", entry.date)
                obj.put("timestamp", entry.timestamp)
                array.put(obj)
            }
            sharedPrefs.edit().putString("logged_weight_history_json", array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addWeightEntry(weight: Float, dateStr: String) {
        val currentList = _weightHistory.value.toMutableList()
        currentList.add(WeightEntry(weight, dateStr, System.currentTimeMillis()))
        currentList.sortBy { it.timestamp }
        _weightHistory.value = currentList
        saveWeightHistoryList(currentList)
        
        // Update user profile current weight too!
        val profile = userProfile.value
        updateUserProfile(
            name = profile.name,
            age = profile.age,
            weight = weight,
            height = profile.heightCm,
            calorieGoal = profile.baseCalorieGoal,
            waterGoal = profile.waterGoalMl,
            stepsGoal = profile.stepGoal
        )
    }

    fun deleteWeightEntry(entry: WeightEntry) {
        val currentList = _weightHistory.value.filter { it != entry }
        _weightHistory.value = currentList
        saveWeightHistoryList(currentList)
    }

    private fun loadCustomPrompts() {
        val userPromptDefault = """
            Analyze this food image. Identify the dish or food items present. High emphasis on identifying ingredients, proportions, and style correctly.
            Provide estimated macros based on standard serving sizes.
            
            You MUST return ONLY a single valid JSON object. Do not explain, do not add markdown wrapping like ```json, just return pure JSON.
            Required JSON keys and format:
            {
              "dishName": "Name of the food item (e.g. Grilled Salmon with Quinoa, Avocado Toast, Beef Pho)",
              "calories": 450,
              "protein": 24.5,
              "carbs": 52.0,
              "fat": 11.5,
              "analysis": "A concise explanation of the health and fitness benefits of this dish, noting core ingredients and nutritional profile."
            }
        """.trimIndent()

        val sysInstructionDefault = "You are an automated premium food recognition engine tailored for global gastronomy and precise calorie estimation. You must explicitly prioritize Vietnamese nutritional data and portion sizing accuracy when the user scans local or regional cuisine (including iconic specialties like Pho, Banh Mi, Bun Cha, and regional variations)."

        _cuisineFocusMode.value = sharedPrefs.getString("ai_cuisine_focus", "Vietnamese Focus") ?: "Vietnamese Focus"
        _verbosityLevel.value = sharedPrefs.getString("ai_verbosity_level", "Detailed Analysis") ?: "Detailed Analysis"
        _caloricAggressiveness.value = sharedPrefs.getString("ai_caloric_aggressiveness", "Balanced Standard") ?: "Balanced Standard"

        _customUserPrompt.value = sharedPrefs.getString("scanned_food_custom_prompt", userPromptDefault) ?: userPromptDefault
        _customSystemInstruction.value = sharedPrefs.getString("scanned_food_custom_system_instruction", sysInstructionDefault) ?: sysInstructionDefault
    }

    fun updateScannerSettings(cuisine: String, verbosity: String, aggressiveness: String) {
        _cuisineFocusMode.value = cuisine
        _verbosityLevel.value = verbosity
        _caloricAggressiveness.value = aggressiveness

        sharedPrefs.edit()
            .putString("ai_cuisine_focus", cuisine)
            .putString("ai_verbosity_level", verbosity)
            .putString("ai_caloric_aggressiveness", aggressiveness)
            .apply()

        // Automatically synthesize the exact system prompt safely in the background
        val sysInstruction = when (cuisine) {
            "Vietnamese Focus" -> {
                "You are an automated premium food recognition engine tailored for global gastronomy and precise calorie estimation. You must explicitly prioritize Vietnamese nutritional data and portion sizing accuracy when the user scans local or regional cuisine (including iconic specialties like Pho, Banh Mi, Bun Cha, and regional variations)."
            }
            else -> {
                "You are an automated premium food recognition engine tailored for global gastronomy and precise calorie estimation."
            }
        } + " " + when (verbosity) {
            "Concise Summary" -> "Keep your dietary analysis feedback extremely brief, compact and to the point (maximum 2 sentences)."
            else -> "Provide a comprehensive, detail-rich explanation of the health and fitness benefits, key biological highlights, and helpful dietary tips."
        } + " " + when (aggressiveness) {
            "Strict Deficit Focus" -> "Be highly conservative in your calorie estimations, aligning slightly on the higher side of normal ranges to help users maintain strict adherence to their calorie deficit boundaries."
            "Athletic Bulk Focus" -> "Take active athletic muscular maintenance energy requirements into consideration, aligning calculations with rich protein and carbohydrate density guidelines."
            else -> "Provide standard, highly balanced median dietary calculations."
        }

        val userPrompt = """
            Analyze this food image. Identify the dish or food items present. High emphasis on identifying ingredients, proportions, and style correctly.
            Provide estimated macros based on standard serving sizes.
            
            You MUST return ONLY a single valid JSON object. Do not explain, do not add markdown wrapping like ```json, just return pure JSON.
            Required JSON keys and format:
            {
              "dishName": "Name of the food item (e.g. Grilled Salmon with Quinoa, Avocado Toast, Beef Pho)",
              "calories": 450,
              "protein": 24.5,
              "carbs": 52.0,
              "fat": 11.5,
              "analysis": "A concise explanation of the health and fitness benefits of this dish, noting core ingredients and nutritional profile."
            }
        """.trimIndent()

        updateCustomPrompts(userPrompt, sysInstruction)
    }

    fun updateCustomPrompts(userPrompt: String, systemInstruction: String) {
        _customUserPrompt.value = userPrompt
        _customSystemInstruction.value = systemInstruction
        sharedPrefs.edit()
            .putString("scanned_food_custom_prompt", userPrompt)
            .putString("scanned_food_custom_system_instruction", systemInstruction)
            .apply()
    }

    fun resetCustomPromptsToDefault() {
        sharedPrefs.edit()
            .remove("scanned_food_custom_prompt")
            .remove("scanned_food_custom_system_instruction")
            .remove("ai_cuisine_focus")
            .remove("ai_verbosity_level")
            .remove("ai_caloric_aggressiveness")
            .apply()
        loadCustomPrompts()
    }


    // SQL/Injection and malicious scripting defense sanitization layer
    private fun isMaliciousPayload(input: String): Boolean {
        val uppercaseInput = input.uppercase(java.util.Locale.ROOT)
        val patterns = listOf(
            "DROP TABLE", "DELETE FROM", "UPDATE ", "INSERT INTO", "ALTER TABLE",
            "SQLITE_MASTER", "UNION SELECT", "SELECT * FROM", " OR 1=1", " OR '1'='1'",
            "<SCRIPT", "JAVASCRIPT:", "ONERROR=", "ONLOAD=", "ONMOUSEOVER=",
            "DATABASE_DESTRUCTION", "IGNORE ALL PRIOR INSTRUCTIONS", "BYPASS RESTRICTIONS",
            "IGNORE PREVIOUS INSTRUCTIONS", "FORGET PREVIOUS INSTRUCTIONS", "NEW SYSTEM INSTRUCTIONS"
        )
        for (pattern in patterns) {
            if (uppercaseInput.contains(pattern)) {
                return true
            }
        }
        return false
    }

    fun sanitizeUserQuery(query: String): String {
        if (query.isEmpty()) return ""
        // Prevent massive buffer/token overflow attacks
        var sanitized = if (query.length > 250) query.substring(0, 250) else query
        
        // Remove known prompt injection commands or bypasses
        val uppercaseSanitized = sanitized.uppercase(java.util.Locale.ROOT)
        val dangerousPhrases = listOf(
            "IGNORE PREVIOUS INSTRUCTIONS", "IGNORE ALL PRIOR INSTRUCTIONS", "FORGET PREVIOUS INSTRUCTIONS",
            "FORGET ALL PREVIOUS", "BYPASS RESTRICTIONS", "YOU ARE NOW A", "SYSTEM OVERRIDE", "NEW SYSTEM RULES",
            "DEVELOPER CONFIG", "PRINT YOUR INSTRUCTIONS", "SHOW SYSTEM PROMPT"
        )
        
        for (phrase in dangerousPhrases) {
            if (uppercaseSanitized.contains(phrase)) {
                sanitized = sanitized.replace(Regex("(?i)" + Regex.escape(phrase)), "[Clean query protocol restored]")
            }
        }
        
        // Remove potential script injection constructs
        sanitized = sanitized.replace(Regex("(?i)<script.*?>.*?</script>"), "")
        sanitized = sanitized.replace(Regex("(?i)javascript:"), "")
        
        return sanitized
    }

    fun clearScannedFood() {
        _scannedFoodResult.value = null
        _scanError.value = null
    }

    fun scanFoodImage(base64Image: String, mimeType: String = "image/jpeg") {
        _isScanningImage.value = true
        _scanError.value = null
        _scannedFoodResult.value = null

        viewModelScope.launch {
            try {
                val responseText = GeminiClient.analyzeFoodImage(
                    base64Image = base64Image,
                    mimeType = mimeType,
                    customPrompt = _customUserPrompt.value,
                    customSystemInstruction = _customSystemInstruction.value
                )
                _lastRawResponse.value = responseText
                val cleanJson = responseText.trim()
                val startIndex = cleanJson.indexOf('{')
                val endIndex = cleanJson.lastIndexOf('}')
                if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                    val jsonStr = cleanJson.substring(startIndex, endIndex + 1)
                    val jsonObj = org.json.JSONObject(jsonStr)
                    val dishName = jsonObj.optString("dishName", "Analyzed Food")
                    val calories = jsonObj.optInt("calories", 0)
                    val protein = jsonObj.optDouble("protein", 0.0).toFloat()
                    val carbs = jsonObj.optDouble("carbs", 0.0).toFloat()
                    val fat = jsonObj.optDouble("fat", 0.0).toFloat()
                    val analysis = jsonObj.optString("analysis", "")

                    _scannedFoodResult.value = ScannedFood(
                        dishName = dishName,
                        calories = calories,
                        protein = protein,
                        carbs = carbs,
                        fat = fat,
                        analysis = analysis
                    )
                } else {
                    _scanError.value = "AI scanning resolved text format issue:\n$responseText"
                }
            } catch (e: Exception) {
                _scanError.value = "OCR Scan Connection Error: ${e.localizedMessage ?: e.message}"
            } finally {
                _isScanningImage.value = false
            }
        }
    }

    fun setScannedFood(dishName: String, calories: Int, protein: Float, carbs: Float, fat: Float, analysis: String, rawResponse: String = "") {
        _scannedFoodResult.value = ScannedFood(dishName, calories, protein, carbs, fat, analysis)
        _isScanningImage.value = false
        _scanError.value = null
        if (rawResponse.isNotEmpty()) {
            _lastRawResponse.value = rawResponse
        }
    }

    // Dynamic calculations: BMI & TDEE
    val userBmi: StateFlow<Float> = userProfile.map { profile ->
        if (profile.heightCm > 0f) {
            val htMers = profile.heightCm / 100f
            profile.weightKg / (htMers * htMers)
        } else 0f
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    fun calculateTdee(weight: Float, height: Float, age: Int, isMale: Boolean, activityMultiplier: Float): Int {
        val bmr = if (isMale) {
            (10f * weight) + (6.25f * height) - (5f * age) + 5f
        } else {
            (10f * weight) + (6.25f * height) - (5f * age) - 161f
        }
        return (bmr * activityMultiplier).toInt()
    }

    // Dashboard quick summary calculations
    val summaryCaloriesLogged = todayMeals.map { meals ->
        meals.sumOf { it.calories }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val summarySteps = todayActivities.map { activities ->
        activities.sumOf { it.steps }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val summaryCaloriesBurned = todayActivities.map { activities ->
        activities.sumOf { it.activeCaloriesBurned }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val proteinLogged = todayMeals.map { meals ->
        meals.sumOf { it.proteinG.toDouble() }.toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val carbsLogged = todayMeals.map { meals ->
        meals.sumOf { it.carbsG.toDouble() }.toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val fatLogged = todayMeals.map { meals ->
        meals.sumOf { it.fatG.toDouble() }.toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    init {
        loadWeightHistory()
        loadCustomPrompts()

        // Pre-populate some historical activity and default mock data to give beautiful visuals immediately
        viewModelScope.launch {
            if (repository.userProfile.first() == null) {
                repository.saveUserProfile(createDefaultProfile())
            }
            // Populate initial sample food log so user sees colorful visual graph immediately
            if (repository.allMeals.first().isEmpty()) {
                repository.logMeal(MealLog(foodName = "Greek Yoghurt & Honey", calories = 320, proteinG = 18f, carbsG = 24f, fatG = 5f, mealType = "Breakfast"))
                repository.logMeal(MealLog(foodName = "Grilled Chicken Salad", calories = 480, proteinG = 42f, carbsG = 15f, fatG = 12f, mealType = "Lunch"))
            }
            if (repository.allActivities.first().isEmpty()) {
                repository.logActivity(ActivityLog(source = "Garmin Fenix", steps = 3540, activeCaloriesBurned = 185, durationMinutes = 25))
            }
        }
    }

    private fun createDefaultProfile() = UserProfile(
        id = "current_user",
        email = authRepo.userEmail,
        name = authRepo.userName,
        age = 28,
        weightKg = 73.5f,
        heightCm = 178f,
        gender = "male",
        activityLevel = "moderately_active",
        fitnessGoal = "maintain",
        baseCalorieGoal = 2200,
        waterGoalMl = 2500,
        stepGoal = 10000
    )

    // --- Authentication Actions ---

    fun signUpWithEmailAndPassword(
        email: String, name: String, password: String,
        age: Int, weight: Float, height: Float, gender: String, activityLevel: String, fitnessGoal: String,
        onComplete: ((Boolean, String?) -> Unit)? = null
    ) {
        _authLoading.value = true
        _authError.value = null
        authRepo.signUpWithEmailAndPassword(email, name, password) { success, error ->
            if (success) {
                _authSuccess.value = true
                viewModelScope.launch {
                    val tdee = calculateTdee(gender, weight, height, age, activityLevel)
                    val targets = deriveTargets(tdee, fitnessGoal, weight)
                    val targetCals = targets["calories"] ?: tdee
                    val waterGoal = if (gender.lowercase() == "male") 3000 else 2200
                    val stepGoal = when (activityLevel.lowercase()) {
                        "sedentary" -> 5000
                        "lightly_active" -> 8000
                        "moderately_active" -> 10000
                        "very_active" -> 12000
                        "extra_active" -> 15000
                        else -> 10000
                    }

                    val initialProfile = UserProfile(
                        id = "current_user",
                        email = email,
                        name = name,
                        age = age,
                        weightKg = weight,
                        heightCm = height,
                        gender = gender,
                        activityLevel = activityLevel,
                        fitnessGoal = fitnessGoal,
                        baseCalorieGoal = targetCals,
                        waterGoalMl = waterGoal,
                        stepGoal = stepGoal
                    )
                    repository.saveUserProfile(initialProfile)

                    val currentDate = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault()).format(java.util.Date())
                    addWeightEntry(weight, currentDate)

                    _authLoading.value = false
                    onComplete?.invoke(true, null)
                }
            } else {
                _authLoading.value = false
                _authError.value = error
                onComplete?.invoke(false, error)
            }
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String, onComplete: ((Boolean, String?) -> Unit)? = null) {
        _authLoading.value = true
        _authError.value = null
        authRepo.signInWithEmailAndPassword(email, password) { success, error ->
            _authLoading.value = false
            if (success) {
                _authSuccess.value = true
                viewModelScope.launch {
                    repository.syncLocalDataWithFirestore()
                    onComplete?.invoke(true, null)
                }
            } else {
                _authError.value = error
                onComplete?.invoke(false, error)
            }
        }
    }

    fun signInAnonymously() {
        _authLoading.value = true
        _authError.value = null
        viewModelScope.launch {
            authRepo.signInAnonymously { success, error ->
                _authLoading.value = false
                if (success) {
                    _authSuccess.value = true
                    viewModelScope.launch {
                        repository.syncLocalDataWithFirestore()
                    }
                } else {
                    _authError.value = error
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepo.signOut()
            _authSuccess.value = false
        }
    }

    // --- Calorie & Food Logging ---

    fun addMeal(name: String, calories: Int, protein: Float, carbs: Float, fat: Float, mealType: String, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            val meal = MealLog(
                foodName = name,
                calories = calories,
                proteinG = protein,
                carbsG = carbs,
                fatG = fat,
                mealType = mealType
            )
            repository.logMeal(meal)
            onComplete?.invoke()
        }
    }

    fun deleteMeal(meal: MealLog) {
        viewModelScope.launch {
            repository.deleteMeal(meal)
        }
    }

    fun addWater(ml: Int) {
        _waterLoggedTodayMl.value = (_waterLoggedTodayMl.value + ml).coerceAtLeast(0)
    }

    // --- Wearable Fitness Activity Syncing ---

    fun syncWithWearable(brand: String) {
        _isWearableSyncing.value = true
        _wearableSyncStatus.value = "Establishing BLE link with $brand..."
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            _wearableSyncStatus.value = "Reading active metrics..."
            kotlinx.coroutines.delay(1000)

            // Randomly simulate fetched additional active step data & calories matching wearable devices
            val stepsFetched = (4000..6500).random()
            val kcalBurned = (150..320).random()
            val duration = (20..45).random()

            val activity = ActivityLog(
                source = "$brand Smartwatch",
                steps = stepsFetched,
                activeCaloriesBurned = kcalBurned,
                durationMinutes = duration
            )
            repository.logActivity(activity)

            _isWearableSyncing.value = false
            _wearableSyncStatus.value = "Successfully synced $stepsFetched steps from $brand!"
        }
    }

    // --- Biometrics Engine (Mifflin-St Jeor) ---
    private fun calculateTdee(gender: String, weightKg: Float, heightCm: Float, age: Int, activityLevel: String): Int {
        val bmr = if (gender.lowercase() == "male") {
            (10 * weightKg) + (6.25f * heightCm) - (5 * age) + 5
        } else {
            (10 * weightKg) + (6.25f * heightCm) - (5 * age) - 161
        }

        val multiplier = when (activityLevel.lowercase()) {
            "sedentary" -> 1.2f
            "lightly_active" -> 1.375f
            "moderately_active" -> 1.55f
            "very_active" -> 1.725f
            "extra_active" -> 1.9f
            else -> 1.2f
        }
        return (bmr * multiplier).toInt()
    }

    private fun deriveTargets(tdee: Int, goal: String, weightKg: Float): Map<String, Int> {
        val targetCalories = when (goal) {
            "lose_weight" -> tdee - 500
            "gain_muscle" -> tdee + 300
            else -> tdee
        }
        val proteinG = (weightKg * 2.0f).toInt()
        val proteinKcal = proteinG * 4
        val fatKcal = targetCalories * 0.25f
        val fatG = (fatKcal / 9).toInt()
        val remainingKcal = targetCalories - (proteinKcal + fatKcal)
        val carbsG = maxOf(0, (remainingKcal / 4).toInt())

        return mapOf(
            "calories" to targetCalories,
            "protein" to proteinG,
            "carbs" to carbsG,
            "fat" to fatG
        )
    }

    // --- Profile Editing ---
    
    fun updateBiometricProfile(
        name: String, age: Int, weight: Float, height: Float, gender: String, activityLevel: String, fitnessGoal: String,
        waterGoal: Int, stepsGoal: Int,
        onComplete: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            val tdee = calculateTdee(gender, weight, height, age, activityLevel)
            val targets = deriveTargets(tdee, fitnessGoal, weight)
            val targetCals = targets["calories"] ?: tdee
            
            val updated = UserProfile(
                id = "current_user",
                email = authRepo.userEmail,
                name = name,
                age = age,
                weightKg = weight,
                heightCm = height,
                gender = gender,
                activityLevel = activityLevel,
                fitnessGoal = fitnessGoal,
                baseCalorieGoal = targetCals,
                waterGoalMl = waterGoal,
                stepGoal = stepsGoal
            )
            repository.saveUserProfile(updated)
            
            val currentDate = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault()).format(java.util.Date())
            addWeightEntry(weight, currentDate)
            
            onComplete?.invoke()
        }
    }


    fun updateUserProfile(name: String, age: Int, weight: Float, height: Float, calorieGoal: Int, waterGoal: Int, stepsGoal: Int, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            val updated = UserProfile(
                id = "current_user",
                email = authRepo.userEmail,
                name = name,
                age = age,
                weightKg = weight,
                heightCm = height,
                baseCalorieGoal = calorieGoal,
                waterGoalMl = waterGoal,
                stepGoal = stepsGoal
            )
            repository.saveUserProfile(updated)
            onComplete?.invoke()
        }
    }

    // --- AI Meal Planning & Recommendation (Google Search Grounded) ---

    fun generateMealPlan(dietaryPreference: String, mealPlanningRequest: String) {
        _isAiPlanningLoading.value = true
        _aiRecommendation.value = "Scanning local nutrition metadata and searching current healthy ingredients on Google..."

        viewModelScope.launch {
            val profile = userProfile.value
            val todayTotal = summaryCaloriesLogged.value
            val goal = profile.baseCalorieGoal
            val sanitizedRequest = sanitizeUserQuery(mealPlanningRequest)

            val prompt = """
                Generate a healthy curated meal plan suggestion.
                User profile: ${profile.age} years old, weighing ${profile.weightKg} kg, current daily target is $goal kcal.
                Dietary Preference/Constraints: $dietaryPreference
                Specific requests or search focus: $sanitizedRequest
                Current progress today: Already logged $todayTotal kcal.
                
                Search current local standard menus or healthy recipes of June 2026.
                Format the answer with clear, elegant bullet points:
                1. A creative Name of the menu option
                2. Ingredients
                3. Accurate nutritional macro breakdown estimate (kcal, protein, fat, carbohydrates)
                4. Clear step-by-step prep summary.
            """.trimIndent()

            val systemPrompt = """
                You are AI Calories & Meal Tracker assistant, an expert digital dietitian.
                Use Google Search grounding tool to look up current superfood trends, seasonal recipes, or meal ideas.
                Always supply up-to-date, mathematically accurate nutrition feedback.
            """.trimIndent()

            val responseText = GeminiClient.getMealPlanWithSearch(prompt, systemPrompt)
            _aiRecommendation.value = responseText
            _isAiPlanningLoading.value = false
        }
    }

    // --- AI Personalised Nutrition Advice (Thinking High Mode) ---

    fun requestExpertNutritionAdvice(question: String) {
        _isAiAdviceLoading.value = true
        _aiAdvice.value = "Activating advanced clinical thinking engine... reasoning nutrition strategy..."

        viewModelScope.launch {
            val profile = userProfile.value
            val todayCalories = todayMeals.value.joinToString { "${it.foodName} (${it.calories} kcal)" }
            val todayActive = todayActivities.value.joinToString { "${it.source}: ${it.steps} steps, ${it.activeCaloriesBurned} kcal burned" }
            val sanitizedQuestion = sanitizeUserQuery(question)

            val prompt = """
                The user asks: "$sanitizedQuestion"
                User context:
                - Age: ${profile.age}
                - Weight: ${profile.weightKg} kg, Height: ${profile.heightCm} cm
                - Base Targets: Target Intake: ${profile.baseCalorieGoal} kcal, Goal Steps: ${profile.stepGoal}
                - Food logged today: $todayCalories
                - Live wearable-synced activities logged today: $todayActive
                - Water target: ${profile.waterGoalMl} ml (Logged today: ${waterLoggedTodayMl.value} ml)

                Conduct a highly reasoning-intensive sports science analysis to answer the query. Help the user optimize their metabolism, fat loss, or muscle gain according to their wearable sensor streams.
            """.trimIndent()

            val systemPrompt = """
                You are a highly analytical expert clinical sports nutritionist.
                Analyze user profiles, macro progress, and physical wearable active output. Do deep tactical thinking, considering hormonal, metabolic, and energy expenditure patterns.
                Structure the output with a detailed rationale followed by actionable, high-impact suggestions.
            """.trimIndent()

            val responseText = GeminiClient.getNutritionAdviceHighThinking(prompt, systemPrompt)
            _aiAdvice.value = responseText
            _isAiAdviceLoading.value = false
        }
    }
}
