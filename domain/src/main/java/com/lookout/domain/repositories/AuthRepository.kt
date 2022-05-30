package com.lookout.domain.repositories

import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.TokenRequest

interface AuthRepository {

    fun logout()

    fun getAuthRequest(): AuthorizationRequest

    fun getEndSessionRequest(): EndSessionRequest

    suspend fun performTokenRequest(authService: AuthorizationService, tokenRequest: TokenRequest)
}