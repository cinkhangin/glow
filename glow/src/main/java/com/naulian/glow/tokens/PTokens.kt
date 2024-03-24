package com.naulian.glow.tokens

import com.naulian.anhance.logInfo
import com.naulian.glow_core.Token
import com.naulian.glow_core.Type
import kotlin.math.min

object PTokens {
    @Suppress("unused")
    private val TAG = PTokens::class.java.simpleName

    @Suppress("unused")
    fun logTokens(input: String) {
        val lexer = PLexer(input)
        var token = lexer.nextToken()
        while (token.type != Type.EOF && token.type != Type.ILLEGAL) {
            logInfo(TAG, token)
            token = lexer.nextToken()
        }
    }


    fun tokenize(input: String): List<Token> {
        val lexer = PLexer(input)
        val tokens = mutableListOf<Token>()
        var token = lexer.nextToken()
        var prevToken = Token(Type.NONE)
        var prevIndex = 0

        while (token.type != Type.EOF && token.type != Type.ILLEGAL) {
            //logDebug(TAG, token)

            if (token.type == Type.WHITE_SPACE) {
                tokens.add(token)
                token = lexer.nextToken()
                continue
            }

            //based on previous
            val modified = when (prevToken.type) {
                Type.ASSIGNMENT -> numberToken(token)
                Type.LEFT_PARENTHESES -> argumentToken(token)
                Type.FUNCTION -> token.copy(type = Type.FUNC_NAME)
                Type.CLASS -> token.copy(type = Type.CLASS_NAME)
                Type.COLON -> token.copy(type = Type.DATA_TYPE)
                Type.VARIABLE -> token.copy(type = Type.VAR_NAME)
                Type.DOT -> token.copy(type = Type.PROPERTY)
                else -> token
            }

            //based on next
            when(modified.type){
                Type.COLON ->{
                    if (prevToken.type == Type.ARGUMENT) {
                        tokens[prevIndex] = prevToken.copy(type = Type.PARAM)
                    }
                }
                Type.LEFT_PARENTHESES -> {
                    tokens.getOrNull(prevIndex - 1)?.let {
                        if(it.type == Type.DOT){
                            tokens[prevIndex] = prevToken.copy(type = Type.FUNC_CALL)
                        }
                    }
                }
                else -> Unit
            }

            //tracking
            prevIndex = tokens.size
            prevToken = modified

            tokens.add(modified)
            token = lexer.nextToken()
        }

        return tokens
    }

    private fun argumentToken(token: Token): Token {
        return if (token.type != Type.IDENTIFIER) token
        else token.copy(type = Type.ARGUMENT)
    }

    private fun numberToken(token: Token): Token {
        return when {
            token.type != Type.NUMBER -> token
            token.value.contains("L") -> token.copy(type = Type.VALUE_LONG)
            token.value.contains("f") -> token.copy(type = Type.VALUE_FLOAT)
            else -> token.copy(type = Type.VALUE_INT)
        }
    }
}

private class PLexer(private val input: String) {
    private var position: Int = 0
    private val keywords = listOf(
        "and", "as", "assert", "async", "await", "break", "class", "continue", "def", "del", "elif",
        "else", "except", "False", "finally", "for", "from", "global", "if", "import", "in", "is",
        "lambda", "None", "nonlocal", "not", "or", "pass", "raise", "return", "True", "try",
        "while", "with", "yield"
    )

    private fun currentChar() = if (position < input.length) input[position] else Char.MIN_VALUE

    fun nextToken(): Token {
        if (currentChar().isWhitespace()) {
            return whitespaceToken()
        }

        return when (val char = currentChar()) {
            '*' -> createToken(Type.ASTERISK, char.toString())
            '.' -> createToken(Type.DOT, char.toString())
            '-' -> createToken(Type.DASH, char.toString())
            '@' -> createToken(Type.AT, char.toString())
            '$' -> createToken(Type.DOLLAR, char.toString())
            '%' -> createToken(Type.MODULO, char.toString())
            '^' -> createToken(Type.POW, char.toString())
            '&' -> createToken(Type.AND, char.toString())
            '?' -> createToken(Type.QUESTION_MARK, char.toString())
            '|' -> createToken(Type.OR, char.toString())
            '\\' -> createToken(Type.ESCAPE, char.toString())
            '!' -> createToken(Type.BANG, char.toString())
            '{' -> createToken(Type.LEFT_BRACE, char.toString())
            '}' -> createToken(Type.RIGHT_BRACE, char.toString())
            '(' -> createToken(Type.LEFT_PARENTHESES, char.toString())
            ')' -> createToken(Type.RIGHT_PARENTHESES, char.toString())
            ',' -> createToken(Type.COMMA, char.toString())
            ':' -> createToken(Type.COLON, char.toString())
            '>' -> createToken(Type.GT, "&gt")
            '<' -> createToken(Type.LT, "&lt")
            ';' -> createToken(Type.SEMICOLON, char.toString())
            '+' -> createToken(Type.PLUS, char.toString())
            '=' -> createToken(Type.ASSIGNMENT, char.toString())
            '/' -> createToken(Type.FORWARD_SLASH, char.toString())
            '[' -> createToken(Type.LEFT_BRACKET, char.toString())
            ']' -> createToken(Type.RIGHT_BRACKET, char.toString())
            '#' -> readComments()
            '\'' -> readSingleQString()
            '\"' -> readDoubleQString()
            in 'a'..'z', in 'A'..'Z', '_' -> readIdentifier()
            in '0'..'9' -> readNumber()
            Char.MIN_VALUE -> createToken(Type.EOF, char.toString())
            else -> createToken(Type.ILLEGAL, char.toString())
        }
    }

    private fun readComments(): Token {
        val start = position
        do {
            position++
        } while (currentChar() != '\n' && currentChar() != Char.MIN_VALUE)

        val identifier = input.substring(start, position)
        return Token(Type.COMMENT_SINGLE, identifier)
    }

    private fun createToken(type: Type, value: String): Token {
        position++
        return Token(type, value)
    }

    private fun whitespaceToken(): Token {
        if(currentChar() == '\n'){
           return createToken(Type.EOL, currentChar().toString())
        }

        val start = position
        while (currentChar().isWhitespace()) {
            position++
        }

        val identifier = input.substring(start, position)
        return Token(Type.WHITE_SPACE, identifier)
    }

    private fun readIdentifier(): Token {
        val start = position
        while (currentChar().isLetter() || currentChar() == '_' || currentChar().isDigit()) {
            position++
        }

        return when (val identifier = input.substring(start, position)) {
            "def" -> Token(Type.FUNCTION, identifier)
            "class" -> Token(Type.CLASS, identifier)
            in keywords -> Token(Type.KEYWORD, identifier)
            else -> Token(Type.IDENTIFIER, identifier)
        }
    }

    private fun readDoubleQString(): Token {
        val start = position
        position++
        while (currentChar() != '\"' && currentChar() != Char.MIN_VALUE) {
            position++
        }
        position++

        position = min(position, input.length)
        val identifier = input.substring(start, position)
        return Token(Type.STRING, identifier)
    }

    private fun readSingleQString(): Token {
        val start = position
        position++
        while (currentChar() != '\'' && currentChar() != Char.MIN_VALUE) {
            position++
        }
        position++

        position = min(position, input.length)
        val identifier = input.substring(start, position)
        return Token(Type.STRING, identifier)
    }

    private fun readNumber(): Token {
        val start = position

        while (
            currentChar().isDigit() || currentChar() == '_' ||
            currentChar() == 'L' || currentChar() == 'f' ||
            currentChar() == '.'
        ) {
            position++
        }
        return Token(Type.NUMBER, input.substring(start, position))
    }
}