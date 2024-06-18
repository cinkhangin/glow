package com.naulian.glow_core.mdx

object MdxParser {

    fun parse(source: String): List<MdxComponentGroup> {
        val generatedTokens = MdxTokenizer.tokenize(source)
        val tokenGroups = mutableListOf<MdxComponentGroup>()
        var currentGroup = mutableListOf<MdxToken>()

        for (token in generatedTokens) {
            val lastType = currentGroup.lastOrNull()?.getComponentType() ?: MdxComponentType.OTHER
            val type = token.getComponentType()

            if (currentGroup.isEmpty() || lastType == type) {
                currentGroup.add(token)
            } else {
                if (currentGroup.last().isBlackText()) {
                    currentGroup.removeLast()
                }

                val handled = handleTokens(currentGroup)
                if (handled.isNotEmpty()) {
                    val atxGroup = MdxComponentGroup(lastType, handled)
                    tokenGroups.add(atxGroup)
                }

                currentGroup = mutableListOf(token)
            }
        }

        //add the last group
        val type = currentGroup.getComponentType()
        val handled = handleTokens(currentGroup)
        if (handled.isNotEmpty()) {
            val atxGroup = MdxComponentGroup(type, handled)
            tokenGroups.add(atxGroup)
        }
        return tokenGroups
    }

    private fun handleTokens(group: List<MdxToken>): List<MdxToken> {
        if (group.isEmpty()) return emptyList()

        val currentGroup = group.toMutableList()
        if (currentGroup.size == 1) {
            val last = currentGroup.removeLast()
            val updatedLast = last.copy(text = last.text.trim())
            currentGroup.add(updatedLast)
        } else {
            val last = currentGroup.removeLast()
            val updatedLast = last.copy(text = last.text.trimEnd())
            currentGroup.add(updatedLast)
        }

        if (currentGroup.first().isBlackText()) {
            currentGroup.removeFirst()
        }

        return currentGroup
    }

    private fun List<MdxToken>.getComponentType(): MdxComponentType {
        return if (isEmpty()) MdxComponentType.OTHER else last().getComponentType()
    }

    private fun MdxToken.getComponentType(): MdxComponentType {
        return when (type) {
            MdxType.TEXT,
            MdxType.BOLD,
            MdxType.ITALIC,
            MdxType.UNDERLINE,
            MdxType.WHITESPACE,
            MdxType.STRIKE,
            MdxType.HYPER_LINK,
            MdxType.LINK,
            MdxType.ESCAPE,
            MdxType.ELEMENT,
            MdxType.DATETIME,
            MdxType.COLORED -> MdxComponentType.TEXT

            else -> MdxComponentType.OTHER
        }
    }

}