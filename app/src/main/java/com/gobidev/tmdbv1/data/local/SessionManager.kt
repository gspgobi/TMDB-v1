package com.gobidev.tmdbv1.data.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("tmdb_session", Context.MODE_PRIVATE)

    var sessionId: String?
        get() = prefs.getString("session_id", null)
        set(value) = prefs.edit { putString("session_id", value) }

    var accountId: Int
        get() = prefs.getInt("account_id", -1)
        set(value) = prefs.edit { putInt("account_id", value) }

    val isLoggedIn: Boolean
        get() = sessionId != null

    fun clearSession() {
        prefs.edit {
            remove("session_id")
            remove("account_id")
        }
    }
}
