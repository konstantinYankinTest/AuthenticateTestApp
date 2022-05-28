package com.lookout.authenticatetestapp.presentation.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import androidx.core.net.toUri
import com.lookout.authenticatetestapp.AuthConfig
import com.lookout.authenticatetestapp.presentation.github.GithubActivity
import com.lookout.authenticatetestapp.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var service: AuthorizationService

    private val launcher = registerForActivityResult(StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val ex = AuthorizationException.fromIntent(it.data)
            val result = AuthorizationResponse.fromIntent(it.data ?: Intent())

            if (ex != null) {
                Log.e("Github Auth", "launcher: $ex")
            } else {
                val secret = ClientSecretBasic(AuthConfig.CLIENT_SECRET)
                val tokenRequest = result?.createTokenExchangeRequest()

                tokenRequest?.let {
                    service.performTokenRequest(tokenRequest, secret) { res, exception ->
                        if (exception != null) {
                            Log.e("Github Auth", "launcher: ${exception.error}")
                        } else {
                            val token = res?.accessToken
                            viewModel.setToken(token ?: "")

                            val intent = Intent(this, GithubActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        service = AuthorizationService(this)

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
                            onClick = { githubAuth() }
                        ) {
                            Text(text = "Login with Github")
                        }
                    }
                }
            }
        }
    }

    private fun githubAuth() {
        val serviceConfiguration = AuthorizationServiceConfiguration(
            Uri.parse(AuthConfig.AUTH_URI),
            Uri.parse(AuthConfig.TOKEN_URI),
            null,
            Uri.parse(AuthConfig.END_SESSION_URI)
        )
        val request =
            AuthorizationRequest
                .Builder(
                    serviceConfiguration,
                    AuthConfig.CLIENT_ID,
                    AuthConfig.RESPONSE_TYPE,
                    AuthConfig.CALLBACK_URL.toUri()
                )
                .setScope(AuthConfig.SCOPE)
                .build()
        val intent = service.getAuthorizationRequestIntent(request)
        launcher.launch(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        service.dispose()
    }
}