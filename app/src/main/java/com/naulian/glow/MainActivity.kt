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
        val source = this.readStringAsset("code")
        val highlighted = Glow().highlight(source)
        textView.text = highlighted
    }
}