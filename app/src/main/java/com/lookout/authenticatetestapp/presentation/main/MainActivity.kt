package com.lookout.authenticatetestapp.presentation.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.biometric.BiometricPrompt
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.fragment.app.FragmentActivity
import com.lookout.authenticatetestapp.R
import com.lookout.authenticatetestapp.presentation.github.views.GithubProfileView
import com.lookout.authenticatetestapp.presentation.main.MainViewModel.UiState.*
import com.lookout.authenticatetestapp.presentation.main.views.AndroidWebView
import com.lookout.authenticatetestapp.presentation.main.views.MainButtonView
import com.lookout.authenticatetestapp.presentation.main.views.MainViewLoading
import com.lookout.authenticatetestapp.presentation.views.ErrorView
import com.lookout.authenticatetestapp.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint


private val biometricsIgnoredErrors = listOf(
    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
    BiometricPrompt.ERROR_CANCELED,
    BiometricPrompt.ERROR_USER_CANCELED,
    BiometricPrompt.ERROR_NO_BIOMETRICS
)

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    @Composable
    fun MainScreen() {
        val viewState by remember { viewModel.state }

        when (val state = viewState) {

            Empty -> MainButtonView(onButtonClick = { viewModel.openLoginPage() })

            Loading -> MainViewLoading()

            ShowBiometricDialog -> showBiometricPrompt()

            is Error -> ErrorView(onReloadClick = { })

            is OpenLoginPage -> {
                AndroidWebView(
                    url = state.url,
                    onAuthCodeReceived = { url -> viewModel.onAuthCodeReceived(url) })
            }

            is OpenLogout -> {
                AndroidWebView(url = state.url,
                    onWebLogoutCompleted = { viewModel.webLogoutComplete() })
            }

            is ShowProfile -> {
                GithubProfileView(
                    user = state.user,
                    onLogoutClicked = { viewModel.openLogoutPage() })
            }
        }
    }

    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText(getString(android.R.string.cancel))
            .build()

        val biometricPrompt = BiometricPrompt(
            this@MainActivity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    if (errorCode !in biometricsIgnoredErrors) {
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.pin_biometric_error, errString),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    viewModel.updateTokens()
                }

                override fun onAuthenticationFailed() {
                    Toast.makeText(
                        this@MainActivity,
                        "Authentication failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
        biometricPrompt.authenticate(promptInfo)
    }
}