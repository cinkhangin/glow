package com.naulian.glow_core

abstract class LineLexer(
    private val input: String
) {
    private var position: Int = 0
    private fun currentChar() = if (position < input.length) input[position] else Char.MIN_VALUE

    abstract fun onCreateToken(tokens: List<Token>): List<Token>

    private fun createSourceFile(): SourceFile {
        var lineNumber = 0
        var lineLevel = 0
        var lineValue = StringBuilder()
        val sourceFile = mutableListOf<SourceLine>()

        while (position < input.length) {
            when (val char = currentChar()) {
                '\n' -> {
                    val sourceLine = SourceLine(
                        number = lineNumber,
                        level = lineLevel,
                        value = lineValue.toString()
                    )
                    lineValue.clear()
                    sourceFile.add(sourceLine)
                    lineNumber++
                }

                '{' -> {
                    lineLevel++
                }

                '}' -> {
                    lineLevel--
                }

                '(' -> {}
                ')' -> {}
                Char.MIN_VALUE -> createToken(Type.EOF, char)
                else -> lineValue.append(char)
            }
            position++
        }

        return SourceFile(emptyList())
    }

    private fun createToken(type: Type, value: String): Token {
        position++
        return Token(type, value)
    }

    private fun createToken(type: Type, char: Char): Token {
        return createToken(type, char.toString())
    }
}