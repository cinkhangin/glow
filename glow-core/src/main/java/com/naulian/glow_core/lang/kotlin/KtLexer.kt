package com.naulian.glow_core.lang.kotlin

import com.naulian.glow_core.Lexer
import com.naulian.glow_core.Token
import com.naulian.glow_core.Type

private val kotlinKeywords = listOf(
    "abstract", "annotation", "as", "break", "by", "catch", "class", "companion", "const",
    "constructor", "continue", "crossinline", "data", "do", "else", "enum", "external", "false",
    "final", "finally", "for", "fun", "if", "in", "infix", "init", "inline", "inner",
    "interface", "internal", "is", "it", "lateinit", "noinline", "null", "object", "open",
    "operator", "out", "import", "override", "package", "private", "protected", "public",
    "reified", "return", "sealed", "super", "suspend", "this", "throw", "to", "true", "try",
    "typealias", "typeof", "val", "var", "when", "where", "while"
)

class KtLexer(input: String) {
    private var basicTokens: List<Token> = Lexer(input).tokenize()
    private var cursor = 0

    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()

        while (cursor < basicTokens.size) {
            val t = basicTokens[cursor]
            if (t.type == Type.SPACE) {
                tokens.add(t)
                cursor++
                continue
            }

            val modified = when (t.type) {
                Type.S_QUOTE -> createCharToken()
                Type.D_QUOTE -> createStringToken()
                Type.F_SLASH -> {
                    val next = peek()
                    when (next.type) {
                        Type.F_SLASH -> createSingleCommentToken()
                        Type.STAR -> createMultilineCommentToken()
                        else -> t
                    }
                }

                Type.WORD -> {
                    if (t.value in kotlinKeywords) t.copy(Type.KEYWORD)
                    else t
                }

                else -> t
            }

            tokens.add(modified)
        }

        return tokens
    }

    private fun peek(offset: Int = 1): Token {
        val position = cursor + offset
        return if (position > basicTokens.size) Token.END else basicTokens[position]
    }

    private fun createSingleCommentToken(): Token {
        val from = cursor
        cursor++
        while (cursor < basicTokens.size && basicTokens[cursor].type != Type.NEWLINE) {
            cursor++
        }
        val subList = basicTokens.subList(from, cursor)
        return Token(Type.S_COMMENT, subList.joinToString(""))
    }

    private fun createMultilineCommentToken(): Token {
        val from = cursor
        cursor++
        while (cursor < basicTokens.size && basicTokens[cursor].type != Type.F_SLASH) {
            cursor++
        }
        val subList = basicTokens.subList(from, cursor)
        return Token(Type.S_COMMENT, subList.joinToString(""))
    }

    private fun createCharToken(): Token {
        val from = cursor
        cursor++
        while (cursor < basicTokens.size && basicTokens[cursor].type != Type.S_QUOTE) {
            cursor++
        }
        val subList = basicTokens.subList(from, cursor)
        return Token(Type.CHAR, subList.joinToString(""))
    }

    private fun createStringToken(): Token {
        val from = cursor
        cursor++
        while (cursor < basicTokens.size && basicTokens[cursor].type != Type.D_QUOTE) {
            cursor++
        }
        val subList = basicTokens.subList(from, cursor)
        return Token(Type.STRING, subList.joinToString(""))
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
}