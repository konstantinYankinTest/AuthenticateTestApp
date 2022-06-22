package com.lookout.authenticatetestapp.presentation.views

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String? = null,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit = {}
) {
    Button(
        modifier = modifier.height(48.dp),
        onClick = onClick,
        enabled = enabled
    ) {
        text?.let {
            Text(
                text = it,
                color = Color.White
            )
        } ?: content.invoke(this)
    }
}