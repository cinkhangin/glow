package com.naulian.glow

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.naulian.anhance.readStringAsset

class MainActivity : AppCompatActivity() {
    @Suppress("unused")
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textInput = findViewById<EditText>(R.id.textInput)
        val textSource = findViewById<TextView>(R.id.textSource)
        val textOutput = findViewById<TextView>(R.id.textOutput)

        val textPaint = textOutput.paint
        textPaint.fontFeatureSettings = "liga=0;" // Disable ligatures

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
            val highlighted = glowSyntax(text,"py", CodeTheme.kotlinLight)
            textSource.text = highlighted.raw
            textOutput.setCodeTheme(CodeTheme.kotlinLight.normal)
            textOutput.text = highlighted.spanned
        }
    }
}