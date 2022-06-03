package com.lookout.domain.usecases

import com.lookout.domain.repositories.AuthRepository
import net.openid.appauth.AuthorizationService
import javax.inject.Inject

class UpdateAccessTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        authorizationService: AuthorizationService
    ) = authRepository.refreshTokens(authorizationService)
}