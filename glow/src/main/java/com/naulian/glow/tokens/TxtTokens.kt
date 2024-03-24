package com.naulian.glow.tokens

import com.naulian.glow_core.Token
import com.naulian.glow_core.Type

object TxtTokens {

    @Suppress("unused")
    private val TAG = TxtTokens::class.java.simpleName

    fun tokenize(input: String): List<Token> {
        return listOf(
            Token(Type.NONE, input)
        )
    }
}