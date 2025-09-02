package com.naulian.glow

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.naulian.glow.theme.GlowTheme
import com.naulian.glow.theme.Theme
import com.naulian.modify.Fonts

@Composable
fun Code(
    modifier: Modifier = Modifier,
    source: String,
    language: String = "txt",
    theme: Theme = GlowTheme.defaultLight,
    textStyle: TextStyle = TextStyle(fontFamily = Fonts.JetBrainsMono),
    onLongClick: (() -> Unit)? = null
) {
    val glowCode = remember(source) {
        Glow.highlight(source, language, theme).annotatedString
    }

    LazyRow(
        modifier = modifier
            .combinedClickable(
                enabled = onLongClick != null,
                onLongClick = onLongClick,
                onClick = {}
            )
    ) {
        item {
            Text(
                text = glowCode,
                style = textStyle,
                modifier = Modifier.padding(12.dp),
            )
        }
    }
}