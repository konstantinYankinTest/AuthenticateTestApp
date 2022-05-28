package com.lookout.authenticatetestapp.presentation.github

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.lookout.authenticatetestapp.AuthConfig
import com.lookout.authenticatetestapp.presentation.main.MainActivity
import com.lookout.authenticatetestapp.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.EndSessionRequest

@AndroidEntryPoint
class GithubActivity : ComponentActivity() {

    private val viewModel: GithubViewModel by viewModels()
    private lateinit var service: AuthorizationService

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            viewModel.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        service = AuthorizationService(this)

        setContent {
            AppTheme {
                Surface {
                    ShowGithubProfile()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        service.dispose()
    }

    @Composable
    private fun ShowGithubProfile() {
        val profile by remember { viewModel.user }
        profile?.let {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = it.image,
                    contentDescription = "Profile image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Name: ${it.name}")

                Row {
                    Text(text = "Repos: ${it.repos}")
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Gists: ${it.gists}")
                }

                Row {
                    Text(text = "Followers: ${it.followers}")
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Following: ${it.following}")
                }
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            githubSingOut()
                        }
                    ) {
                        Text(text = "Sing out Github")
                    }
                }


            }
        }
    }

    private fun githubSingOut() {
        viewModel.signOut()

        val serviceConfiguration = AuthorizationServiceConfiguration(
            Uri.parse(AuthConfig.AUTH_URI),
            Uri.parse(AuthConfig.TOKEN_URI),
            null,
            Uri.parse(AuthConfig.END_SESSION_URI)
        )
        val endSessionRequest = EndSessionRequest
            .Builder(serviceConfiguration)
            .setPostLogoutRedirectUri(AuthConfig.LOGOUT_CALLBACK_URL.toUri())
            .build()

        val endSessionIntent = service.getEndSessionRequestIntent(endSessionRequest)
        launcher.launch(endSessionIntent)
    }
}