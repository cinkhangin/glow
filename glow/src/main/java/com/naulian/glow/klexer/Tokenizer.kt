package com.naulian.glow.klexer

import com.naulian.anhance.logDebug

class Tokenizer {
    private val TAG = Tokenizer::class.java.simpleName

    fun tokenize(input: String): List<Token> {
        val lexer = Lexer(input)
        val tokens = mutableListOf<Token>()
        var token = lexer.nextToken()
        while (token.type != TokenType.EOF && token.type != TokenType.ILLEGAL) {
            logDebug(TAG, token)
            tokens.add(token)
            token = lexer.nextToken()
        }

        return audit1(tokens)
    }

    private fun audit1(list: List<Token>): List<Token> {
        if (list.size < 2) return list

        val tokens = mutableListOf(
            list[0],
            list[1]
        )

        for (index in 2 until list.size) {
            val prev = list[index - 2]
            val token = list[index]

            val modified = when (prev.type) {
                TokenType.ASSIGNMENT -> numberToken(token)
                TokenType.LEFT_PARENTHESES -> argumentToken(token)
                TokenType.FUNCTION -> token.copy(type = TokenType.FUNC_NAME)
                TokenType.CLASS -> token.copy(type = TokenType.CLASS_NAME)
                TokenType.COLON -> token.copy(type = TokenType.DATA_TYPE)
                TokenType.VAL, TokenType.VAR -> token.copy(type = TokenType.VAR_NAME)
                else -> token
            }

            tokens.add(modified)
        }
        return audit2(tokens)
    }

    private fun argumentToken(token: Token): Token{
        return if(token.type != TokenType.IDENTIFIER)  token
        else token.copy(type = TokenType.ARGUMENT)
    }

    private fun numberToken(token: Token): Token {
        if (token.type != TokenType.NUMBER) return token
        return if (token.value.contains("L")) token.copy(type = TokenType.VALUE_LONG)
        else if (token.value.contains("f")) token.copy(type = TokenType.VALUE_FLOAT)
        else token.copy(type = TokenType.VALUE_INT)
    }

    private fun audit2(listRaw: List<Token>): List<Token> {
        val list = listRaw.reversed()
        if (list.size < 2) return list

        val tokens = mutableListOf(
            list[0],
            list[1]
        )

        for (index in 2 until list.size) {
            val prev = list[index - 2]
            val token = list[index]

            val modified = when (prev.type) {
                TokenType.COLON -> if (token.type == TokenType.ARGUMENT) token.copy(
                    type = TokenType.PARAM
                )
                else token

                else -> token
            }
            tokens.add(modified)
        }
        return tokens.reversed()
    }
}