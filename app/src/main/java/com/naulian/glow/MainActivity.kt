package com.naulian.glow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.naulian.anhance.readStringAsset
import com.naulian.glow.tokens.PTokens

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textInput = findViewById<EditText>(R.id.textInput)
        val textSource = findViewById<TextView>(R.id.textSource)
        val textOutput = findViewById<TextView>(R.id.textOutput)

        readStringAsset("python") { result ->
            result.onSuccess {
                textInput.setText(it)
                val highlighted = glowSyntax(it,"py", CodeTheme.kotlinLight)
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
            PTokens.logTokens(text)
            val highlighted = glowSyntax(text,"py", CodeTheme.kotlinLight)
            textSource.text = highlighted.raw
            textOutput.setCodeTheme(CodeTheme.kotlinLight.normal)
            textOutput.text = highlighted.spanned
        }
    }
}