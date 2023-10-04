package com.naulian.glow.tokens

object JTokens {

    @Suppress("unused")
    private val TAG = JTokens::class.java.simpleName

    fun tokenize(input: String): List<Token> {
        val lexer = JLexer(input)
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
        return if(token.type != Type.IDENTIFIER)  token
        else token.copy(type = Type.ARGUMENT)
    }

    private fun numberToken(token: Token): Token {
        if (token.type != Type.NUMBER) return token
        return if (token.value.contains("L")) token.copy(type = Type.VALUE_LONG)
        else if (token.value.contains("f")) token.copy(type = Type.VALUE_FLOAT)
        else token.copy(type = Type.VALUE_INT)
    }
}

private class JLexer(private val input: String) {
    private var position: Int = 0
    private val keywords = listOf(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
        "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally",
        "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface",
        "long", "native", "new", "package", "private", "protected", "public", "return", "short",
        "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "void", "volatile", "while"
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
            '#' -> createToken(Type.HASH, char.toString())
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
            '>' -> createToken(Type.GT, char.toString())
            '<' -> createToken(Type.LT, char.toString())
            ';' -> createToken(Type.SEMICOLON, char.toString())
            '+' -> createToken(Type.PLUS, char.toString())
            '=' -> createToken(Type.ASSIGNMENT, char.toString())
            '[' -> createToken(Type.LEFT_BRACKET, char.toString())
            ']' -> createToken(Type.RIGHT_BRACKET, char.toString())
            '/' -> {
                when (input[position + 1]) {
                    '/' -> lexSingleLineComment()
                    '*' -> lexMultiLineComment()
                    else -> createToken(Type.SLASH_FORWARD, char.toString())
                }
            }
            '\'' -> readChar()
            '\"' -> readString()
            in 'a'..'z', in 'A'..'Z', '_' -> readIdentifier()
            in '0'..'9' -> readNumber()
            Char.MIN_VALUE -> createToken(Type.EOF, char.toString())
            else -> createToken(Type.ILLEGAL, char.toString())
        }
    }

    private fun lexSingleLineComment(): Token {
        val start = position
        do {
            position++
        } while (currentChar() != '\n' && currentChar() != Char.MIN_VALUE)

        val identifier = input.substring(start, position)
        return Token(Type.COMMENT_SINGLE, identifier)
    }

    private fun lexMultiLineComment(): Token {
        val start = position
        do {
            position++
        } while (currentChar() != '/' && currentChar() != Char.MIN_VALUE)
        position++

        val identifier = input.substring(start, position)
        return Token(Type.COMMENT_MULTI, identifier)
    }


    private fun createToken(type: Type, value: String): Token {
        position++
        return Token(type, value)
    }

    private fun whitespaceToken(): Token {
        val start = position
        while (currentChar().isWhitespace()) {
            position++
        }

        val indentifier = input.substring(start, position)
        return Token(Type.WHITE_SPACE, indentifier)
    }

    private fun readIdentifier(): Token {
        val start = position
        while (currentChar().isLetter() || currentChar() == '_' || currentChar().isDigit()) {
            position++
        }

        return when (val identifier = input.substring(start, position)) {
            "int" -> Token(Type.VARIABLE, identifier)
            "string" -> Token(Type.VARIABLE, identifier)
            "long" -> Token(Type.VARIABLE, identifier)
            "boolean" -> Token(Type.VARIABLE, identifier)
            "char" -> Token(Type.VARIABLE, identifier)
            "byte" -> Token(Type.VARIABLE, identifier)
            "float" -> Token(Type.VARIABLE, identifier)
            "short" -> Token(Type.VARIABLE, identifier)
            "class" -> Token(Type.CLASS, identifier)
            in keywords -> Token(Type.KEYWORD, identifier)
            else -> Token(Type.IDENTIFIER, identifier)
        }
    }

    private fun readString(): Token {
        val start = position
        position++
        while (currentChar() != '\"' && currentChar() != Char.MIN_VALUE) {
            position++
        }
        position++
        val identifier = input.substring(start, position)
        return Token(Type.STRING, identifier)
    }

    private fun readChar(): Token {
        val start = position
        position++
        while (currentChar() != '\'') {
            position++
        }
        position++
        val identifier = input.substring(start, position)
        return Token(Type.CHAR, identifier)
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