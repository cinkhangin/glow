package com.naulian.glow

object Rex {
    val keywords = "\\b(${Keyword.kotlin.joinToString("|")})\\b".toRegex()
    val strings = "\"[^\"]*\"|'[^']*'".toRegex()
    val numbers = "\\b\\d+\\b".toRegex()
    val methods = "\\b[A-Za-z_]\\w*\\(\\)".toRegex()
    //val properties = "[^\\.]*\\.([^\\.]*)".toRegex()
    val comments = "//[^\n]*".toRegex()
    val documentations = "/\\*[\\s\\S]*?\\*/".toRegex()
}