package com.lookout.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Preferences @Inject constructor(
    @ApplicationContext context: Context
) {

    private val preferences =
        context.getSharedPreferences("AUTHENTICATE_TEST_PREFS", Context.MODE_PRIVATE)

    var accessToken: String?
        get() = preferences.getString(ACCESS_TOKEN_KEY, null)
        set(value) {
            preferences.edit().putString(ACCESS_TOKEN_KEY, value).apply()
        }

    var refreshToken: String?
        get() = preferences.getString(REFRESH_TOKEN_KEY, null)
        set(value) {
            preferences.edit().putString(REFRESH_TOKEN_KEY, value).apply()
        }

    companion object {

        private const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY"
        private const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN_KEY"
    }
}