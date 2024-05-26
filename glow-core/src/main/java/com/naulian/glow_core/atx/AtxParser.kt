package com.naulian.glow_core.atx

class AtxParser(source: String) {
    private val baseTokens = source.tokenizeAtx()

    private var cursor = 0
    private val token get() = baseTokens.getOrElse(cursor) { AtxToken.END }
    private fun advance(amount: Int = 1) {
        cursor += amount
    }

    private fun next(): AtxToken {
        advance()
        return token
    }

    fun parse(): List<AtxNode> {
        val result = mutableListOf<AtxNode>()
        var currentGroup = mutableListOf<AtxToken>()

        var element = token
        while (element.type != AtxType.END) {
            val lastKind = currentGroup.lastOrNull()?.type?.kind ?: AtxKind.OTHER
            if (currentGroup.isEmpty() || lastKind == element.type.kind) {
                currentGroup.add(element)
            } else {
                val atxNode = AtxNode(lastKind, currentGroup)
                result.add(atxNode)
                currentGroup = mutableListOf(element)
            }
            element = next()
        }

        if (currentGroup.isNotEmpty()) {
            val groupKind = currentGroup.last().type.kind
            val atxNode = AtxNode(groupKind, currentGroup)
            result.add(atxNode)
        }

        return result
    }

}