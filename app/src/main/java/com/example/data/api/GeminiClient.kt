package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Models for Gemini API ---

@JsonClass(generateAdapter = true)
data class InlineData(
    @Json(name = "mimeType") val mimeType: String,
    @Json(name = "data") val data: String
)

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null,
    @Json(name = "inlineData") val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class ThinkingConfig(
    @Json(name = "thinkingLevel") val thinkingLevel: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @Json(name = "thinkingConfig") val thinkingConfig: ThinkingConfig? = null,
    @Json(name = "temperature") val temperature: Float? = null
)

@JsonClass(generateAdapter = true)
data class GoogleSearch(
    @Json(name = "googleSearch") val googleSearch: Map<String, String>? = emptyMap()
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @Json(name = "tools") val tools: List<GoogleSearch>? = null,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>? = null
)

// --- Retrofit Interface ---

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContentFlash(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse

    @POST("v1beta/models/gemini-3.1-pro-preview:generateContent")
    suspend fun generateContentPro(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

// --- API Service Client ---

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val apiService: GeminiApiService by lazy {
        retrofit.create(GeminiApiService::class.java)
    }

    /**
     * Executes queries using gemini-3.5-flash with Google Search Grounding for meal insights.
     */
    suspend fun getMealPlanWithSearch(prompt: String, systemPrompt: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "API Key is empty or placeholder!")
            return "Please configure your GEMINI_API_KEY in the Secrets panel."
        }

        // Configure Google Search tools: {"googleSearch": {}}
        val tools = listOf(GoogleSearch())

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            tools = tools,
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
        )

        return try {
            val response = apiService.generateContentFlash(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No recommendation text generated."
        } catch (e: Exception) {
            Log.e(TAG, "API call failed", e)
            "Error generating response: ${e.localizedMessage ?: e.message}"
        }
    }

    /**
     * Executes complex queries using gemini-3.1-pro-preview with Thinking HIGH for personalized nutrition advice.
     */
    suspend fun getNutritionAdviceHighThinking(prompt: String, systemPrompt: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "API Key is empty or placeholder!")
            return "Please configure your GEMINI_API_KEY in the Secrets panel."
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(
                thinkingConfig = ThinkingConfig(thinkingLevel = "HIGH"),
                temperature = 0.7f
            ),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
        )

        return try {
            val response = apiService.generateContentPro(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No clinical nutrition advice generated."
        } catch (e: Exception) {
            Log.e(TAG, "API call failed", e)
            "Error reasoning advice: ${e.localizedMessage ?: e.message}"
        }
    }

    /**
     * Analyzes image of a dish (especially recognizing Vietnamese food) using gemini-3.5-flash
     */
    suspend fun analyzeFoodImage(
        base64Image: String,
        mimeType: String,
        customPrompt: String? = null,
        customSystemInstruction: String? = null
    ): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "API Key is empty or placeholder!")
            return """{"error": "Please configure your GEMINI_API_KEY in the Secrets panel."}"""
        }

        val prompt = if (!customPrompt.isNullOrBlank()) {
            customPrompt
        } else {
            """
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
        }

        val baseInstruction = "You are an automated premium food recognition engine tailored for global gastronomy and precise calorie estimation. You must explicitly prioritize Vietnamese nutritional data and portion sizing accuracy when the user scans local or regional cuisine (including iconic specialties like Pho, Banh Mi, Bun Cha, and regional variations)."
        val sysInstructionText = if (!customSystemInstruction.isNullOrBlank()) {
            "$baseInstruction\n\nUser custom instruction settings: $customSystemInstruction"
        } else {
            baseInstruction
        }

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = prompt),
                        Part(inlineData = InlineData(mimeType = mimeType, data = base64Image))
                    )
                )
            ),
            generationConfig = GenerationConfig(temperature = 0.2f),
            systemInstruction = Content(parts = listOf(Part(text = sysInstructionText)))
        )

        return try {
            val response = apiService.generateContentFlash(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: """{"error": "No content generated."}"""
        } catch (e: Exception) {
            Log.e(TAG, "Image analysis call failed", e)
            """{"error": "${e.localizedMessage ?: e.message}"}"""
        }
    }
}
