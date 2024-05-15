package com.naulian.glow_compose.kotlin

import com.naulian.glow.tokens.StrTokens
import com.naulian.glow_core.Token
import com.naulian.glow_core.Type


fun tokenizeKt(input: String): List<Token> {
    val lexer = KtLexer(input)
    val tokens = mutableListOf<Token>()
    var token = lexer.nextToken()
    var prevToken = Token(Type.NONE)
    var prevIndex = 0

    while (token.type != Type.EOF && token.type != Type.ILLEGAL) {
        //logDebug(TAG, token)

        if (token.type == Type.SPACE) {
            tokens.add(token)
            token = lexer.nextToken()
            continue
        }

        //based on previous
        val modified = when (prevToken.type) {
            Type.ASSIGNMENT -> numberToken(token)
            Type.LPAREN -> argumentToken(token)
            Type.FUNCTION -> token.copy(type = Type.FUNC_NAME)
            Type.CLASS -> token.copy(type = Type.CLASS_NAME)
            Type.COLON -> token.copy(type = Type.DATA_TYPE)
            Type.VARIABLE -> token.copy(type = Type.VAR_NAME)
            Type.DOT -> token.copy(type = Type.PROPERTY)
            else -> token
        }

        //based on next
        when (modified.type) {
            Type.COLON -> {
                if (prevToken.type == Type.ARGUMENT) {
                    tokens[prevIndex] = prevToken.copy(type = Type.PARAM)
                }
            }

            Type.LPAREN -> {
                tokens.getOrNull(prevIndex - 1)?.let {
                    if (it.type == Type.DOT) {
                        tokens[prevIndex] = prevToken.copy(type = Type.FUNC_CALL)
                    }
                }
            }

            else -> Unit
        }

        //tracking
        prevIndex = tokens.size
        prevToken = modified

        if (modified.type == Type.STRING) {
            val strTokens = StrTokens(modified.value).tokenize()
            tokens.addAll(strTokens)
        } else tokens.add(modified)
        token = lexer.nextToken()
    }

    return tokens
}

private fun argumentToken(token: Token): Token {
    return if (token.type != Type.IDENTIFIER) token
    else token.copy(type = Type.ARGUMENT)
}

private fun numberToken(token: Token): Token {
    if (token.type != Type.NUMBER) return token
    return if (token.value.contains("L")) token.copy(type = Type.VALUE_LONG)
    else if (token.value.contains("f")) token.copy(type = Type.VALUE_FLOAT)
    else token.copy(type = Type.VALUE_INT)
}
