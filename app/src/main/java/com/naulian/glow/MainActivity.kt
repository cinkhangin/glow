package com.naulian.glow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.widget.doAfterTextChanged
import com.naulian.anhance.readStringAsset
import com.naulian.glow.databinding.ActivityMainBinding
import com.naulian.glow_compose.Glow
import com.naulian.glow_compose.toComposeColor

class MainActivity : AppCompatActivity() {
    @Suppress("unused")
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filename = "sample.kt"
        val language = "kt"
        val theme = CodeTheme.defaultDark

        binding.apply {
            textOutput.setCodeTheme(theme.normal)
            readStringAsset(filename) { result ->
                result.onSuccess {
                    val source = it
                    textInput.setText(source)
                    val highlighted = glowSyntax(source, language, theme)
                    textSource.text = highlighted.raw
                    textOutput.text = highlighted.spanned

                    val highLightedCompose = Glow.highlight(source, language, theme)
                    composeView.setContent {
                        LazyRow(modifier = Modifier.background(theme.background.toComposeColor()).padding(16.dp)) {
                            item { Text(text = highLightedCompose.value) }
                        }
                    }
                }
                result.onFailure {
                    textOutput.text = it.message
                }
            }

            textInput.doAfterTextChanged {

                val text = it?.toString() ?: ""

                /*val strTokens = StrTokens(text).tokenize()
                textSource.text = strTokens.toString()*/

                val highlighted = glowSyntax(text, language, theme)
                textSource.text = highlighted.raw
                textOutput.text = highlighted.spanned
            }
        }
    }
}