package com.naulian.glow.lexer

interface Lexer {
    fun nextToken() : Token
}