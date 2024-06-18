package com.naulian.glow_core.mdx

object MdxParser {

    fun parse(source: String): List<MdxComponentGroup> {
        val generatedTokens = MdxLexer(source).tokenize()
        val tokenGroups = mutableListOf<MdxComponentGroup>()
        var currentGroup = mutableListOf<MdxToken>()

        for (token in generatedTokens) {
            val lastType = currentGroup.lastOrNull()?.getComponentType() ?: MdxComponentType.OTHER
            val type = token.getComponentType()

            if (currentGroup.isEmpty() || lastType == type) {
                currentGroup.add(token)
            } else {
                if (currentGroup.last().isBlankText()) {
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
        return tokenGroups.finalize()
    }

    private fun List<MdxComponentGroup>.finalize(): List<MdxComponentGroup> {
        val groups = mutableListOf<MdxComponentGroup>()
        for (g in this) {
            if (g.children.size == 1) {
                val child = g.children.first()
                if (child.type != MdxType.WHITESPACE) {
                    groups.add(g)
                }
            } else {
                var modifiedChildren = g.children

                //println("before: $modifiedChildren")
                while (modifiedChildren.first().type == MdxType.WHITESPACE) {
                    // println("removing first")
                    modifiedChildren = modifiedChildren.drop(1)
                    if (modifiedChildren.isEmpty()) break
                }

                if (modifiedChildren.isEmpty()) {
                    continue
                }

                while (modifiedChildren.last().type == MdxType.WHITESPACE) {
                    // println("removing first")
                    modifiedChildren = modifiedChildren.dropLast(1)
                    if (modifiedChildren.isEmpty()) break
                }

                if (modifiedChildren.isNotEmpty()) {
                    //println("after:$modifiedChildren")
                    val modifiedComponent = g.copy(children = modifiedChildren)
                    groups.add(modifiedComponent)
                }
            }
        }

        return groups.toList()
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

        if (currentGroup.first().isBlankText()) {
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