package com.example.glow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.glow.ui.theme.GlowTheme
import com.naulian.glow.glowSyntax
import com.naulian.glow.theme.GlowTheme
import com.naulian.glow.theme.hexToColor
import com.naulian.modify.Fonts

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GlowTheme {
                val darkTheme = GlowTheme.RosePineMoon
                val sourceCode = Sample.Kotlin
                val highLighted = glowSyntax(sourceCode, language = "kt").annotatedString

                LazyColumn {
                    item {
                        LazyRow(
                            modifier = Modifier
                                .background(darkTheme.background.hexToColor())
                                .padding(16.dp)
                        ) {
                            item {
                                Text(
                                    text = highLighted,
                                    color = Color.White,
                                    fontFamily = Fonts.JetBrainsMono
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
