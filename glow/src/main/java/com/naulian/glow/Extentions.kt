package com.naulian.glow

import android.graphics.Color
import android.widget.TextView

fun TextView.setCodeTheme(hexColor: String) = setTextColor(Color.parseColor(hexColor))
