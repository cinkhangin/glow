package com.naulian.glow_core

data class Token(
    val type: Type,
    val value: String = ""
) {
    companion object {
        val NONE = Token(Type.NONE, "")
        val END = Token(Type.EOF, "")
    }
}
