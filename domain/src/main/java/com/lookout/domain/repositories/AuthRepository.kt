package com.lookout.domain.repositories

interface AuthRepository {

    fun logout()

    fun getAuthUrl(): String

    fun getEndSessionUrl(): String

    suspend fun performTokenRequest(code: String)

    suspend fun refreshTokens()
}