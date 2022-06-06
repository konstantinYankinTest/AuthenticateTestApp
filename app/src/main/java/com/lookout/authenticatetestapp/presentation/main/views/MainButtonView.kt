package com.lookout.authenticatetestapp.presentation.main.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lookout.authenticatetestapp.ui.theme.AppTheme

@Composable
fun MainButtonView(onButtonClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onButtonClick
        ) {
            Text(text = "Start")
        }
    }
}

@Preview
@Composable
fun DailyViewLoading_Preview() {
    AppTheme(darkTheme = true) {
        MainButtonView(onButtonClick = {})
    }
}