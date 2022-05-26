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

    fun getGitHubToken() = preferences.getString(gitHubTokenKey, null)

    fun setGitHubToken(token: String?) = preferences.edit().putString(gitHubTokenKey, token).apply()
}