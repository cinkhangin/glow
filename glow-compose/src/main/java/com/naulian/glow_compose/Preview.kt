package com.naulian.glow_compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal fun Preview(
    background: Color = Color(0xFFF2F3F4),
    content: @Composable () -> Unit
) {
    MaterialTheme {
        Surface(
            color = background,
            content = content
        )
    }
}
