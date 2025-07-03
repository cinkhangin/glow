package com.naulian.glow.tokens

import com.naulian.glow.Token
import com.naulian.glow.Type

internal class StrTokens(private val str: String) {
    @Suppress("unused")
    private val tag = StrTokens::class.java.simpleName

    private var position = 0
    private fun advance() = position++
    private val char get() = if (position >= str.length) Char.MAX_VALUE else str[position]

    private val tokens = arrayListOf<Token>()

    fun tokenize(): List<Token> {
        tokens.clear()
        position = 0
        while (char != Char.MAX_VALUE) {
            when (char) {
                '\\' -> readEscapes()
                '$' -> readInterpolation()
                '}' -> {
                    val right = Token(Type.STRING_BRACE, "}")
                    tokens.add(right)
                    advance()
                }
                else -> readNormal()
            }
        }
        return tokens
    }

    private fun readEscapes() {
        val start = position
        advance()
        advance()

        val identifier = if (char == Char.MAX_VALUE) str.substring(start)
        else str.substring(start, position)
        val newToken = Token(Type.B_SCAPE, identifier)
        tokens.add(newToken)
    }

    private fun readInterpolation() {
        val dollar = Token(Type.B_SCAPE, "$")
        tokens.add(dollar)
        advance()

        if (char == '{') {
            val left = Token(Type.STRING_BRACE, "{")
            tokens.add(left)
            advance()

            val start = position
            while (char != Char.MAX_VALUE && char != '}') {
                advance()
            }
            val identifier = str.substring(start, position)
            val newToken = Token(Type.INTERPOLATION, identifier)
            tokens.add(newToken)
            return
        }

        val start = position
        while (char != Char.MAX_VALUE && char != '\"' && char != ' ' && char != '}') {
            advance()
        }
        val identifier = str.substring(start, position)
        val newToken = Token(Type.INTERPOLATION, identifier)
        tokens.add(newToken)
    }

    private fun readNormal() {
        val start = position
        while (char != Char.MAX_VALUE && position < str.length &&
            char != '\\' && char != '$') {
            advance()
        }
        val identifier = str.substring(start, position)
        val newToken = Token(Type.STRING, identifier)
        tokens.add(newToken)
    }
}