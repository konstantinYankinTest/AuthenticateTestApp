package com.lookout.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Preferences @Inject constructor(
    @ApplicationContext context: Context
) {

    private val gitHubTokenKey = "GITHUB_TOKEN"
    private val preferences =
        context.getSharedPreferences("AUTHENTICATE_TEST_PREFS", Context.MODE_PRIVATE)

    var accessToken: String?
    get() = preferences.getString(gitHubTokenKey, null)
    set(value) {
        preferences.edit().putString(gitHubTokenKey, value).apply()
    }
}