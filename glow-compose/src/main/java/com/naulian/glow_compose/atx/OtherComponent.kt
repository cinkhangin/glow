package com.naulian.glow_compose.atx

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.naulian.glow_core.atx.AtxToken
import com.naulian.glow_core.atx.AtxType

val a2z = ('a'..'z').toList()
val A2Z = ('A'..'Z').toList()

@Composable
fun OtherComponent(tokens: List<AtxToken>) {
    tokens.forEach { token ->
        when (token.type) {
            AtxType.HEADER -> {
                val sizePair = when (token.argument) {
                    "1" -> 32.sp to 42.sp
                    "2" -> 28.sp to 36.sp
                    "3" -> 24.sp to 32.sp
                    "4" -> 20.sp to 28.sp
                    "5" -> 18.sp to 24.sp
                    "6" -> 16.sp to 22.sp
                    else -> 24.sp to 32.sp
                }
                Text(
                    text = token.value,
                    fontSize = sizePair.first,
                    fontWeight = FontWeight.Bold,
                    lineHeight = sizePair.second
                )
            }

            AtxType.QUOTE -> {
                val content = """
                    |${token.value}
                    |- ${token.argument.ifEmpty { "unknown" }}
                """.trimMargin()
                QuoteBlock(quote = content)
            }

            AtxType.FUN -> AtxCodeBlock(token = token)

            AtxType.PICTURE -> AtxImageBlock(token = token)

            AtxType.VIDEO -> {}

            AtxType.DIVIDER -> {
                if (token.value == "") {
                    HorizontalDivider()
                } else Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = token.value,
                    textAlign = TextAlign.Center
                )
            }

            else -> {}
        }
    }
}