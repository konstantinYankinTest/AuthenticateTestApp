package com.lookout.authenticatetestapp.presentation.github

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.biometric.BiometricPrompt
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.fragment.app.FragmentActivity
import com.lookout.authenticatetestapp.R
import com.lookout.authenticatetestapp.presentation.github.views.GithubProfileView
import com.lookout.authenticatetestapp.presentation.github.views.GithubViewLoading
import com.lookout.authenticatetestapp.presentation.main.MainActivity
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
class GithubActivity : FragmentActivity() {

    private val viewModel: GithubViewModel by viewModels()

    private val logoutResponse = registerForActivityResult(StartActivityForResult()) {
        viewModel.webLogoutComplete()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Surface {
                    GithubProfileScreen()
                }
            }
        }
    }

    @Composable
    private fun GithubProfileScreen() {
        val viewState by remember { viewModel.state }
        when (val state = viewState) {
            GithubViewModel.UiState.Empty -> {

            }
            is GithubViewModel.UiState.Error -> {
                ErrorView(onReloadClick = { viewModel.checkEnableAccessToken() })
            }
            is GithubViewModel.UiState.Loaded -> {
                GithubProfileView(
                    user = state.user,
                    onLogoutClicked = { viewModel.logout() })
            }
            GithubViewModel.UiState.Loading -> {
                GithubViewLoading()
            }
            GithubViewModel.UiState.UpdateToken -> {
                showBiometricPrompt()
            }
            GithubViewModel.UiState.ReadyForRequest -> {
                viewModel.loadUserInfo()
            }
            GithubViewModel.UiState.CloseGithubActivity -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            is GithubViewModel.UiState.OpenLogout -> {
                logoutResponse.launch(state.intent)
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
            this@GithubActivity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    if (errorCode !in biometricsIgnoredErrors) {
                        Toast.makeText(
                            this@GithubActivity,
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
                        this@GithubActivity,
                        "Authentication failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
        biometricPrompt.authenticate(promptInfo)
    }
}
