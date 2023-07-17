package com.naulian.glow.klexer

data class Token(
    val type: TokenType,
    val value: String = ""
)