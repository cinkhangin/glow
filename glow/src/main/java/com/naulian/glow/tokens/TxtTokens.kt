package com.naulian.glow.tokens

object TxtTokens {

    @Suppress("unused")
    private val TAG = TxtTokens::class.java.simpleName

    fun tokenize(input: String): List<Token> {
        return listOf(
            Token(Type.NONE, input)
        )
    }
}