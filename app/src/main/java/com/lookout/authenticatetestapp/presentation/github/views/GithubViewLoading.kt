package com.lookout.authenticatetestapp.presentation.github.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lookout.authenticatetestapp.ui.theme.AppTheme

@Composable
fun GithubViewLoading() {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
fun DailyViewLoading_Preview() {
    AppTheme(darkTheme = true) {
        GithubViewLoading()
    }
}