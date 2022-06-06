package com.lookout.authenticatetestapp.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.lookout.authenticatetestapp.presentation.github.GithubActivity
import com.lookout.authenticatetestapp.presentation.main.views.MainButtonView
import com.lookout.authenticatetestapp.presentation.main.views.MainViewLoading
import com.lookout.authenticatetestapp.presentation.views.ErrorView
import com.lookout.authenticatetestapp.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val getAuthResponse = registerForActivityResult(StartActivityForResult()) {
        val dataIntent = it.data ?: return@registerForActivityResult
        handleAuthResponseIntent(dataIntent)
    }

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

            MainViewModel.UiState.Empty -> MainButtonView(onButtonClick = { viewModel.start() })

            is MainViewModel.UiState.Error -> ErrorView(onReloadClick = { viewModel.start() })

            MainViewModel.UiState.Loaded -> {
                val intent = Intent(this, GithubActivity::class.java)
                startActivity(intent)
                finish()
            }
            MainViewModel.UiState.Loading -> MainViewLoading()

            is MainViewModel.UiState.OpenLoginPage -> getAuthResponse.launch(state.intent)

        }
    }

    private fun handleAuthResponseIntent(intent: Intent) {
        val exception = AuthorizationException.fromIntent(intent)
        val tokenExchangeRequest = AuthorizationResponse.fromIntent(intent)
            ?.createTokenExchangeRequest()
        when {
            exception != null -> viewModel.onAuthCodeFailed(exception)
            tokenExchangeRequest != null ->
                viewModel.onAuthCodeReceived(tokenExchangeRequest)
        }
    }
}