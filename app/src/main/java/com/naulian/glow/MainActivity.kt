package com.naulian.glow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.naulian.anhance.readStringAsset

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.textMain)
        readStringAsset("python") { result ->
            result.onSuccess {
                val highlighted = Glow.hlPython(it, CodeTheme.kotlinLight)
                textView.setCodeTheme(CodeTheme.kotlinLight.normal)
                textView.text = highlighted.spanned
            }
            result.onFailure {
                textView.text = it.message
            }
        }
    }
}