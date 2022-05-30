package com.lookout.domain.usecases

import com.lookout.domain.repositories.AuthRepository
import javax.inject.Inject

class GetEndSessionRequestUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke() = authRepository.getEndSessionRequest()
}