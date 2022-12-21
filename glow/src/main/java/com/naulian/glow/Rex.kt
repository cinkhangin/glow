package com.naulian.glow

object Rex {
    val punctuations = "[+,=\\-|*&:;]".toRegex()
    val lists = "\\b(${Keyword.lists.joinToString("|")})\\b".toRegex()
    val keywords = "\\b(${Keyword.kotlin.joinToString("|")})\\b".toRegex()
    val variables = "(val|var)\\s+(\\w+)".toRegex()
    val strings = "\"[^\"]*\"|'[^']*'".toRegex()
    val numbers = "\\b\\d+\\b".toRegex()
    val methods = "\\b[a-z]\\w*\\(\\)".toRegex()
    //val properties = "[^\\.]*\\.([^\\.]*)".toRegex()
    val comments = "//[^\n]*".toRegex()
    val documentations = "/\\*[\\s\\S]*?\\*/".toRegex()
}