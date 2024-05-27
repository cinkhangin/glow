package com.naulian.glow_core.atx

data class BaseToken(
    val type: BaseType,
    val text: String
) {
    companion object {
        val EOF = BaseToken(BaseType.EOF, "")
    }
}

data class AtxToken(
    val type: AtxType,
    val value: String = "",
    val argument: String = ""
) {
    companion object {
        val EOF = AtxToken(AtxType.EOF)
        val NEWLINE = AtxToken(AtxType.NEWLINE, "\n")
        val OTHER = AtxToken(AtxType.OTHER, "")
    }
}