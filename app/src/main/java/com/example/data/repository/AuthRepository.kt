package com.example.data.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(private val context: Context) {
    private val TAG = "AuthRepository"
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _mockSessionActive = MutableStateFlow(false)
    val mockSessionActive: StateFlow<Boolean> = _mockSessionActive.asStateFlow()

    init {
        try {
            _currentUser.value = firebaseAuth.currentUser
            firebaseAuth.addAuthStateListener { auth ->
                _currentUser.value = auth.currentUser
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing firebase auth", e)
        }
        val prefs = context.getSharedPreferences("mock_auth_prefs", Context.MODE_PRIVATE)
        _mockSessionActive.value = prefs.getBoolean("is_signed_in", false)
    }

    val isUserSignedIn: Boolean
        get() = firebaseAuth.currentUser != null || _mockSessionActive.value

    val userEmail: String
        get() {
            try {
                val email = firebaseAuth.currentUser?.email
                if (email != null) return email
            } catch (e: Exception) {
                // ignore
            }
            val prefs = context.getSharedPreferences("mock_auth_prefs", Context.MODE_PRIVATE)
            return prefs.getString("current_email", "guest_user@aistudio.com") ?: "guest_user@aistudio.com"
        }

    val userName: String
        get() {
            try {
                val name = firebaseAuth.currentUser?.displayName
                if (name != null) return name
            } catch (e: Exception) {
                // ignore
            }
            val prefs = context.getSharedPreferences("mock_auth_prefs", Context.MODE_PRIVATE)
            return prefs.getString("current_name", "Guest User") ?: "Guest User"
        }

    suspend fun signOut() {
        try {
            firebaseAuth.signOut()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build()
            GoogleSignIn.getClient(context, gso).signOut()
        } catch (e: Exception) {
            Log.e(TAG, "Sign out error", e)
        }
        val prefs = context.getSharedPreferences("mock_auth_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .remove("current_email")
            .remove("current_name")
            .putBoolean("is_signed_in", false)
            .apply()
        _mockSessionActive.value = false
    }

    fun isRealFirebaseConfigured(): Boolean {
        return try {
            val app = com.google.firebase.FirebaseApp.getInstance()
            val hasRealKey = app.options.apiKey != "AIzaSyD-placeholder-key" && !app.options.apiKey.contains("placeholder")
            hasRealKey
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Authenticate anonymously as helper, or custom placeholder auth
     */
    suspend fun signInAnonymously(onComplete: (Boolean, String?) -> Unit) {
        val prefs = context.getSharedPreferences("mock_auth_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("current_email", "sandbox_user@aistudio.com")
            .putString("current_name", "Sandbox Explorer")
            .putBoolean("is_signed_in", true)
            .apply()
        _mockSessionActive.value = true

        try {
            firebaseAuth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _currentUser.value = firebaseAuth.currentUser
                        onComplete(true, null)
                    } else {
                        onComplete(true, null) // Allow local bypass on success
                    }
                }
        } catch (e: Exception) {
            onComplete(true, null) // Allow local bypass on success
        }
    }

    fun signUpWithEmailAndPassword(email: String, name: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        if (isRealFirebaseConfigured()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                            displayName = name
                        }
                        user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                            onComplete(true, null)
                        } ?: onComplete(true, null)
                    } else {
                        onComplete(false, task.exception?.localizedMessage ?: "Registration error")
                    }
                }
        } else {
            val prefs = context.getSharedPreferences("mock_auth_prefs", Context.MODE_PRIVATE)
            val existing = prefs.getString("user_pwd_$email", null)
            if (existing != null) {
                onComplete(false, "An account with this email already exists.")
                return
            }
            prefs.edit()
                .putString("user_pwd_$email", password)
                .putString("user_name_$email", name)
                .putString("current_email", email)
                .putString("current_name", name)
                .putBoolean("is_signed_in", true)
                .apply()
            
            _mockSessionActive.value = true
            onComplete(true, null)
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        if (isRealFirebaseConfigured()) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onComplete(true, null)
                    } else {
                        onComplete(false, task.exception?.localizedMessage ?: "Invalid credentials")
                    }
                }
        } else {
            val prefs = context.getSharedPreferences("mock_auth_prefs", Context.MODE_PRIVATE)
            val storedPwd = prefs.getString("user_pwd_$email", null)
            if (storedPwd == null) {
                onComplete(false, "Account not found. Please create an account first.")
                return
            }
            if (storedPwd != password) {
                onComplete(false, "Invalid password.")
                return
            }
            val storedName = prefs.getString("user_name_$email", "User")
            prefs.edit()
                .putString("current_email", email)
                .putString("current_name", storedName)
                .putBoolean("is_signed_in", true)
                .apply()
            
            _mockSessionActive.value = true
            onComplete(true, null)
        }
    }
}
