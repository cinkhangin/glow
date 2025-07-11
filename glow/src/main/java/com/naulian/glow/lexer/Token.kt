@file:Suppress("unused")

package com.naulian.glow.lexer

data class Token(
    val type: Type,
    val value: String = ""
) {
    companion object {
        val NONE = Token(Type.NONE, "")
        val END = Token(Type.EOF, "")
    }
}
