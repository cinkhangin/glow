package com.naulian.glow_core.atx

class AtxParser(source: String) {
    private val atxLexer = AtxLexer(source)
    private val atxTokens = atxLexer.tokenize()

    private var cursor = 0
    private fun token() = atxTokens.getOrElse(cursor) { AtxToken.EOF }
    private fun advance(amount: Int = 1) {
        cursor += amount
    }

    private fun next(): AtxToken {
        advance()
        return token()
    }

    fun parse(): List<AtxGroup> {
        val atxGroups = mutableListOf<AtxGroup>()
        var atxTokens = mutableListOf<AtxToken>()

        var element = token()
        while (element.type != AtxType.EOF) {
            val lastType = atxTokens.lastOrNull()?.getAtxBaseType() ?: AtxContentType.OTHER
            val type = element.getAtxBaseType()

            if (atxTokens.isEmpty() || lastType == type) {
                atxTokens.add(element)
            } else {
                val atxGroup = AtxGroup(lastType, atxTokens)
                atxGroups.add(atxGroup)
                atxTokens = mutableListOf(element)
            }
            element = next()
        }

        if (atxTokens.isNotEmpty()) {
            val type = atxTokens.last().getAtxBaseType()
            val atxNode = AtxGroup(type, atxTokens)
            atxGroups.add(atxNode)
        }

        return atxGroups
    }

    private fun AtxToken.getAtxBaseType(): AtxContentType {
        return when (type) {
            AtxType.TEXT,
            AtxType.BOLD,
            AtxType.ITALIC,
            AtxType.UNDERLINE,
            AtxType.STRIKE,
            AtxType.COLORED,
            AtxType.ELEMENT,
            AtxType.NEWLINE -> AtxContentType.TEXT

            AtxType.TABLE -> AtxContentType.TABLE
            AtxType.LINK -> AtxContentType.LINK

            else -> AtxContentType.OTHER
        }
    }

}