package com.naulian.glow

object KotlinRegex {
    val punctuations = "[+,=\\-|*&:;{}]".toRegex()
    val lists = "\\b(${Keyword.lists.joinToString("|")})\\b".toRegex()
    val keywords = "\\b(${Keyword.kotlin.joinToString("|")})\\b".toRegex()
    val variables = "(val|var)\\s+(\\w+)".toRegex()
    val strings = "\"[^\"]*\"|'[^']*'".toRegex()
    val numbers = "\\b(\\d+(\\.\\d+)?([fF])?|0x[\\da-fA-F]+|\\d+([lL]))\\b".toRegex()
    //val methods = "\\b[a-z]\\w*\\(\\)".toRegex()
    //val properties = "[^\\.]*\\.([^\\.]*)".toRegex()
    val comments = "//[^\n]*".toRegex()
    val documentations = "/\\*[\\s\\S]*?\\*/".toRegex()

    val instanceProperty = Regex("(?<=\\.)[a-z][a-zA-Z\\d]*?(?=[\\s,)\\].])")
}
