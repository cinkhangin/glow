package com.naulian.glow_compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal fun Preview(content: @Composable () -> Unit) {
    MaterialTheme {
        Surface(
            color = Color.LightGray,
            content = content
        )
    }
}
