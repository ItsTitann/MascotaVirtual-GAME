package com.example.animation.data.local

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("pet_prefs", Context.MODE_PRIVATE)

    fun savePetId(id: String) {
        prefs.edit().putString("pet_id", id).apply()
    }

    fun getPetId(): String? {
        return prefs.getString("pet_id", null)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
