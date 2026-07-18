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

    var cachedUsername: String?
        get() = prefs.getString("cached_username", null)
        set(value) = prefs.edit { putString("cached_username", value) }

    var cachedName: String?
        get() = prefs.getString("cached_name", null)
        set(value) = prefs.edit { putString("cached_name", value) }

    var cachedAvatarUrl: String?
        get() = prefs.getString("cached_avatar_url", null)
        set(value) = prefs.edit { putString("cached_avatar_url", value) }

    val isLoggedIn: Boolean
        get() = sessionId != null

    fun clearSession() {
        prefs.edit {
            remove("session_id")
            remove("account_id")
            remove("cached_username")
            remove("cached_name")
            remove("cached_avatar_url")
        }
    }
}
