package com.example.glow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.example.glow.databinding.ActivityMainBinding
import com.naulian.anhance.readStringAsset
import com.naulian.glow.CodeTheme
import com.naulian.glow.glowSyntax
import com.naulian.glow.setCodeTheme
import com.naulian.glow.tokens.StrTokens
import com.naulian.glow.compose.Glow
import com.naulian.glow.compose.hexToColor


class MainActivity : AppCompatActivity() {
    @Suppress("unused")
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filename = "sample.java"
        val language = "java"
        val lightTheme = CodeTheme.defaultLight
        val darkTheme = CodeTheme.defaultDark

        binding.apply {
            textOutput.setCodeTheme(lightTheme.normal)
            readStringAsset(filename) { result ->
                result.onSuccess { source ->
                    textInput.setText(source)
                    val highlighted = glowSyntax(source, language, lightTheme)

                    textSource.text = highlighted.raw
                    textOutput.text = highlighted.spanned

                    textSource.isVisible = true
                    textInput.isVisible = true
                    textOutput.isVisible = true

                    val highLightedCompose = Glow.highlight(source, language, darkTheme)

                    composeView.setContent {

                    }
                }
                result.onFailure {
                    textOutput.text = it.message
                }
            }

            textInput.doAfterTextChanged {

                val text = it?.toString() ?: ""

                val strTokens = StrTokens(text).tokenize()
                textSource.text = strTokens.toString()

                val highlighted = glowSyntax(text, language, lightTheme)
                textSource.text = highlighted.raw
                textOutput.text = highlighted.spanned
            }
        }
    }
}