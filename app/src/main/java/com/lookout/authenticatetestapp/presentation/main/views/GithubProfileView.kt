package com.lookout.authenticatetestapp.presentation.github.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.lookout.authenticatetestapp.ui.theme.AppTheme
import com.lookout.domain.models.GithubUser

@Composable
fun GithubProfileView(
    user: GithubUser,
    onLogoutClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = user.image,
            contentDescription = "Profile image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Name: ${user.name}")

        Row {
            Text(text = "Repos: ${user.repos}")
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Gists: ${user.gists}")
        }

        Row {
            Text(text = "Followers: ${user.followers}")
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Following: ${user.following}")
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { onLogoutClicked() }
            ) {
                Text(text = "Sing out Github")
            }
        }
    }
}

@Preview
@Composable
fun GithubProfileView_Preview() {
    AppTheme(darkTheme = true) {
        GithubProfileView(user = GithubUser.test(), onLogoutClicked = {})
    }
}