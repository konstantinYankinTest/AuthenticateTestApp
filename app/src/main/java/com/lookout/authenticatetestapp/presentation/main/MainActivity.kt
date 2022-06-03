package com.lookout.authenticatetestapp.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lookout.authenticatetestapp.extention.launchWhenStarted
import com.lookout.authenticatetestapp.presentation.github.GithubActivity
import com.lookout.authenticatetestapp.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import net.openid.appauth.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val getAuthResponse = registerForActivityResult(StartActivityForResult()) {
        val dataIntent = it.data ?: return@registerForActivityResult
        handleAuthResponseIntent(dataIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.sideEffects
            .onEach(::handleSideEffects)
            .launchWhenStarted(this)

        viewModel.state
            .onEach(::handleViewState)
            .launchWhenStarted(this)

        setContent {
            AppTheme {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.start() }
                        ) {
                            Text(text = "Start")
                        }
                    }
                }
            }
        }
    }

    private fun handleViewState(state: MainViewModel.State) {
        when (state) {
            MainViewModel.State.Empty -> {

            }
            is MainViewModel.State.Error -> {

            }
            MainViewModel.State.Loaded -> {

            }
            MainViewModel.State.Loading -> {

            }
        }
    }

    private fun handleSideEffects(sideEffect: MainViewModel.SideEffect) {
        when (sideEffect) {
            MainViewModel.SideEffect.NavigateToDetails -> {
                val intent = Intent(this, GithubActivity::class.java)
                startActivity(intent)
                finish()
            }
            is MainViewModel.SideEffect.OpenLoginPage -> {
                getAuthResponse.launch(sideEffect.intent)
            }
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