package com.naulian.glow_core.atx

fun String.tokenizeAtx(): List<AtxToken> {
    val lexer = AtxLexer(this)
    val tokens = mutableListOf<AtxToken>()
    var current = lexer.next()
    while (current.type != AtxType.END) {
        tokens.add(current)
        current = lexer.next()
    }
    tokens.forEach(::println)
    return tokens
}