package com.example.travelpass

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "MyPrefs",
        Context.MODE_PRIVATE
    )

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun setLoggedIn(loggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("isLoggedIn", loggedIn).apply()
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }

}