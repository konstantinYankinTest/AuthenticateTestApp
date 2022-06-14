package com.lookout.data.repositories

import com.lookout.data.AccessToken
import com.lookout.data.auth.AppAuth
import com.lookout.data.local.Preferences
import com.lookout.domain.repositories.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val preferences: Preferences
) : AuthRepository {

    override fun logout() {
        AccessToken.accessToken = null
        preferences.refreshToken = null
    }

    override fun getAuthUrl(): String {
        return AppAuth.getAuthUrl()
    }

    override fun getEndSessionUrl(): String {
        return AppAuth.getEndSessionUrl()
    }

    override suspend fun performTokenRequest(code: String) {
        withContext(Dispatchers.IO) {
            val tokens = AppAuth.getTokens(code)
            preferences.refreshToken = tokens.refreshToken
            AccessToken.accessToken = tokens.accessToken
        }
    }

    override suspend fun refreshTokens() {
        withContext(Dispatchers.IO){
            val tokens = AppAuth.updateTokens(preferences.refreshToken.orEmpty())
            preferences.refreshToken = tokens.refreshToken
            AccessToken.accessToken = tokens.accessToken
        }
    }
}