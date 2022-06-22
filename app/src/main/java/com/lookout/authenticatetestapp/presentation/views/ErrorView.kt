package com.lookout.authenticatetestapp.presentation.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lookout.authenticatetestapp.R
import com.lookout.authenticatetestapp.ui.theme.AppTheme

@Composable
fun ErrorView(
    onReloadClick: () -> Unit,
    message: String? = null
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(96.dp),
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Error loading items"
                )

                Text(
                    modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
                    text = message ?: stringResource(id = R.string.error_general),
                    textAlign = TextAlign.Center
                )

                CustomButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.action_refresh),
                    onClick = onReloadClick
                )
            }
        }
    }
}

@Composable
@Preview
fun ErrorView_Preview() {
    AppTheme(darkTheme = true) {
        ErrorView(onReloadClick = {})
    }
}