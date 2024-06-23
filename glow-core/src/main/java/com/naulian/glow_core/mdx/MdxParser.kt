package com.naulian.glow_core.mdx

object MdxParser {

    fun parse(source: String): List<MdxComponentGroup> {
        val tokens = MdxLexer(source).tokenize()
        val firstIteration = parseIteration(tokens).flatten()
        val secondIteration = parseIteration(firstIteration)
        return secondIteration
    }

    private fun List<MdxComponentGroup>.flatten(): List<MdxNode> {
        val tokens = mutableListOf<MdxNode>()
        for (group in this) {
            for (token in group.children) {
                tokens.add(token)
            }
        }
        return tokens.toList()
    }

    private fun parseIteration(source: List<MdxNode>): List<MdxComponentGroup> {
        val tokenGroups = mutableListOf<MdxComponentGroup>()
        var currentGroup = mutableListOf<MdxNode>()

        for (token in source) {
            val lastType = currentGroup.lastOrNull()?.getComponentType() ?: MdxComponentType.OTHER
            val type = token.getComponentType()

            if (currentGroup.isEmpty() || lastType == type) {
                currentGroup.add(token)
            } else {
                val children = currentGroup.trimWhiteSpaces()
                if (children.isNotEmpty()) {
                    val atxGroup = MdxComponentGroup(lastType, children)
                    tokenGroups.add(atxGroup)
                }
                currentGroup = mutableListOf(token)
            }
        }

        //add the last group
        val type = currentGroup.getComponentType()
        val children = currentGroup.trimWhiteSpaces()
        if (children.isNotEmpty()) {
            val atxGroup = MdxComponentGroup(type, children)
            tokenGroups.add(atxGroup)
        }
        return tokenGroups
    }

    private fun List<MdxNode>.trimWhiteSpaces(): List<MdxNode> {
        if (isEmpty()) {
            return emptyList()
        }

        if (size == 1) {
            val child = first()
            return if (child.type != MdxType.WHITESPACE) this
            else emptyList()
        } else {
            var modifiedList = this
            while (modifiedList.first().type == MdxType.WHITESPACE) {
                modifiedList = modifiedList.drop(1)
                if (modifiedList.isEmpty()) break
            }

            if (modifiedList.isEmpty()) {
                return emptyList()
            }

            while (modifiedList.last().type == MdxType.WHITESPACE) {
                modifiedList = modifiedList.dropLast(1)
                if (modifiedList.isEmpty()) break
            }

            return if (modifiedList.isEmpty()) emptyList()
            else modifiedList
        }
    }

    private fun List<MdxNode>.getComponentType(): MdxComponentType {
        return if (isEmpty()) MdxComponentType.OTHER else last().getComponentType()
    }

    private fun MdxNode.getComponentType(): MdxComponentType {
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
            MdxType.DATETIME,
            MdxType.COLORED -> MdxComponentType.TEXT
            MdxType.ELEMENT -> MdxComponentType.ELEMENT

            else -> MdxComponentType.OTHER
        }
    }

}