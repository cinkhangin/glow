package com.naulian.glow

interface Lexer {
    fun nextToken() : Token
}