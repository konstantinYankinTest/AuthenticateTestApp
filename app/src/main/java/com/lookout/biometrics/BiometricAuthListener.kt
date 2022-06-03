package com.lookout.biometrics

import androidx.biometric.BiometricPrompt


interface BiometricAuthListener {

    fun onBiometricAuthSuccess(result: BiometricPrompt.AuthenticationResult)

    fun onBiometricAuthError(errorCode: Int, errorMessage: String)
}