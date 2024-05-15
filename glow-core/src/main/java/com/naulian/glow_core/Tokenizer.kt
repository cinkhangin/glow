package com.naulian.glow_core

class Tokenizer(private val input: String) {
    fun tokenize(): List<Token> {
        val lexer = Lexer(input)
        val tokens = mutableListOf<Token>()
        var token = lexer.nextToken()

        while (token.type != Type.EOF && token.type != Type.ILLEGAL) {
            tokens.add(token)
            token = lexer.nextToken()
        }

        return tokens
    }
}