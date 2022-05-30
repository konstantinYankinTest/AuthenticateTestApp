package com.lookout.data.repositories

import com.lookout.data.auth.AppAuth
import com.lookout.data.local.Preferences
import com.lookout.domain.repositories.AuthRepository
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.TokenRequest
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val preferences: Preferences
) : AuthRepository {

    override fun logout() {
        preferences.accessToken = null
        preferences.refreshToken = null
        preferences.idToken = null
    }

    override fun getAuthRequest(): AuthorizationRequest {
        return AppAuth.getAuthRequest()
    }

    override fun getEndSessionRequest(): EndSessionRequest {
        return AppAuth.getEndSessionRequest()
    }

    override suspend fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest
    ) {
        val tokens = AppAuth.performTokenRequestSuspend(authService, tokenRequest)
        preferences.accessToken = tokens.accessToken
        preferences.refreshToken = tokens.refreshToken
        preferences.idToken = tokens.idToken
    }
}