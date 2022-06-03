package com.lookout.authenticatetestapp.presentation.github

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import coil.compose.AsyncImage
import com.lookout.authenticatetestapp.R
import com.lookout.authenticatetestapp.extention.launchWhenStarted
import com.lookout.authenticatetestapp.presentation.main.MainActivity
import com.lookout.authenticatetestapp.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

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

        viewModel.sideEffects
            .onEach(::handleSideEffects)
            .launchWhenStarted(this)

        setContent {
            AppTheme {
                Surface {
                    ShowGithubProfile()
                }
            }
        }
    }

    @Composable
    private fun ShowGithubProfile() {
        val state by remember { viewModel.state }
        HandleViewState(state)
    }

    @Composable
    private fun HandleViewState(state: GithubViewModel.UiState) {
        when (state) {
            GithubViewModel.UiState.Empty -> {

            }
            is GithubViewModel.UiState.Error -> {

            }
            is GithubViewModel.UiState.Loaded -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = state.user.image,
                        contentDescription = "Profile image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Name: ${state.user.name}")

                    Row {
                        Text(text = "Repos: ${state.user.repos}")
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = "Gists: ${state.user.gists}")
                    }

                    Row {
                        Text(text = "Followers: ${state.user.followers}")
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = "Following: ${state.user.following}")
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                viewModel.logout()
                            }
                        ) {
                            Text(text = "Sing out Github")
                        }
                    }
                }
            }
            GithubViewModel.UiState.Loading -> {

            }
            GithubViewModel.UiState.UpdateToken -> {
                showBiometricPrompt()
            }
            GithubViewModel.UiState.ReadyForRequest -> {
                viewModel.loadUserInfo()
            }
        }
    }

    private fun handleSideEffects(sideEffect: GithubViewModel.SideEffect) {
        when (sideEffect) {
            GithubViewModel.SideEffect.CloseGithubActivity -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            is GithubViewModel.SideEffect.OpenLogout -> {
                logoutResponse.launch(sideEffect.intent)
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
