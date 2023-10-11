package com.naulian.glow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.naulian.anhance.readStringAsset
import com.naulian.glow.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    @Suppress("unused")
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filename = "sample.java"
        val language = "java"

        binding.apply {
            readStringAsset(filename) { result ->
                result.onSuccess {
                    val source = "ArrayList<String> fruits = new ArrayList<String>();\nint x = a <= b;"
                    textInput.setText(source)
                    val highlighted = glowSyntax(source, language, CodeTheme.kotlinLight)
                    textSource.text = highlighted.raw
                    textOutput.setCodeTheme(CodeTheme.kotlinLight.normal)
                    textOutput.text = highlighted.spanned
                }
                result.onFailure {
                    textOutput.text = it.message
                }
            }

            textInput.doAfterTextChanged {
                val text = it?.toString() ?: ""
                val highlighted = glowSyntax(text, language, CodeTheme.kotlinLight)
                textSource.text = highlighted.raw
                textOutput.setCodeTheme(CodeTheme.kotlinLight.normal)
                textOutput.text = highlighted.spanned
            }
        }
    }
}