// Student No: 2501529
package com.example.a2501529_lecturereactiontracker

import android.content.Context

object PremiumManager {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_IS_PREMIUM = "is_premium"

    fun isPremium(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_PREMIUM, false)
    }

    fun setPremium(context: Context, isPremium: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_PREMIUM, isPremium).apply()
    }
}