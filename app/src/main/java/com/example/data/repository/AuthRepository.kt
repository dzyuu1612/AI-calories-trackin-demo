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

    init {
        try {
            _currentUser.value = firebaseAuth.currentUser
            firebaseAuth.addAuthStateListener { auth ->
                _currentUser.value = auth.currentUser
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing firebase auth", e)
        }
    }

    val isUserSignedIn: Boolean
        get() = firebaseAuth.currentUser != null

    val userEmail: String
        get() = firebaseAuth.currentUser?.email ?: "guest_user@aistudio.com"

    val userName: String
        get() = firebaseAuth.currentUser?.displayName ?: "Guest User"

    suspend fun signOut() {
        try {
            firebaseAuth.signOut()
            // Sign out google client too
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build()
            GoogleSignIn.getClient(context, gso).signOut()
        } catch (e: Exception) {
            Log.e(TAG, "Sign out error", e)
        }
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
        try {
            firebaseAuth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _currentUser.value = firebaseAuth.currentUser
                        onComplete(true, null)
                    } else {
                        onComplete(false, task.exception?.localizedMessage ?: "Unknown auth error")
                    }
                }
        } catch (e: Exception) {
            onComplete(false, e.localizedMessage ?: "Firebase app is not configured correctly.")
        }
    }
}
