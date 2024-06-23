package com.naulian.glow_core.mdx

class MdxParser2(private val source: String) {

    fun parse(): MdxNode2 {
        val nodes = MdxLexer2(source).tokenize()
        val astBuilder = ASTBuilder(nodes)
        val treeNode = astBuilder.build()
        val typedNode = astBuilder.buildTyped(treeNode)
        return typedNode
    }
}

private val mdxTextTypes = listOf(
    MdxType2.BLOCK_SYMBOL,
    MdxType2.COLOR_START,
    MdxType2.COLOR_END,
    MdxType2.BOLD,
    MdxType2.ITALIC,
    MdxType2.UNDERLINE,
    MdxType2.STRIKE,
    MdxType2.TEXT,
    MdxType2.PARAGRAPH,
    MdxType2.LINK,
    MdxType2.HYPER_LINK,
    MdxType2.WHITESPACE,
    MdxType2.NEWLINE,
    MdxType2.DATETIME,
    MdxType2.ESCAPE,
    MdxType2.IGNORE,
    MdxType2.COLORED
)

private val mdxElementTypes = listOf(
    MdxType2.ELEMENT,
    MdxType2.ELEMENT_BULLET,
    MdxType2.ELEMENT_UNCHECKED,
    MdxType2.ELEMENT_CHECKED
)

class ASTBuilder(private val nodes: List<MdxNode2>) {

    fun buildTyped(node: MdxNode2 = MdxNode2(children = nodes)): MdxNode2 {
        if (node.children.isEmpty()) {
            return node
        }

        var index = 0
        val children = node.children
        val childNodes = mutableListOf<MdxNode2>()
        while (children.getOrNull(index) != null && children[index].type != MdxType2.EOF) {
            val current = children[index]
            when (current.type) {
                in mdxTextTypes -> {
                    val start = index
                    while (children.getOrNull(index) != null && children[index].type in mdxTextTypes && children[index] != MdxNode2.EOF) {
                        index++
                    }

                    val subChild = children.subList(start, index)
                    val paragraphNode = MdxNode2(
                        type = MdxType2.PARAGRAPH,
                        children = subChild.trimWhiteSpaces()
                    )

                    if (paragraphNode.children.isNotEmpty()) {
                        childNodes.add(paragraphNode)
                    }
                }

                in mdxElementTypes -> {
                    val start = index
                    while (
                        children.getOrNull(index) != null
                        && (children[index].type in mdxElementTypes
                                || children[index].type == MdxType2.NEWLINE)
                        && children[index] != MdxNode2.EOF
                    ) {
                        index++
                    }
                    val subChild = children.subList(start, index)
                        .filter { it.type != MdxType2.NEWLINE }
                        .map { buildTyped(it) }

                    val elementNode = MdxNode2(
                        type = MdxType2.ELEMENT,
                        children = subChild.trimWhiteSpaces()
                    )

                    if (elementNode.children.isNotEmpty()) {
                        childNodes.add(elementNode)
                    }
                }

                else -> {
                    if (current.children.isNotEmpty()) {
                        val child = buildTyped(current)
                        childNodes.add(child)
                    } else childNodes.add(children[index])
                    index++
                }
            }
        }

        return node.copy(children = childNodes)
    }

    fun build(node: MdxNode2 = MdxNode2(children = nodes)): MdxNode2 {
        if (node.children.isEmpty()) {
            return node
        }

        var index = 0
        val children = node.children
        val childNodes = mutableListOf<MdxNode2>()
        while (children.getOrNull(index) != null && children[index].type != MdxType2.EOF) {
            val current = children[index]
            when (current.type) {
                MdxType2.HEADER -> {
                    index++
                    val start = index
                    while (children.getOrNull(index) != null && children[index].type != MdxType2.NEWLINE && children[index] != MdxNode2.EOF) {
                        index++
                    }
                    val subChild = children.subList(start, index)

                    val nodeType = when (current.literal) {
                        "#1" -> MdxType2.H1
                        "#2" -> MdxType2.H2
                        "#3" -> MdxType2.H3
                        "#4" -> MdxType2.H4
                        "#5" -> MdxType2.H5
                        "#6" -> MdxType2.H6
                        else -> MdxType2.TEXT
                    }
                    val h1Node = MdxNode2(
                        type = nodeType,
                        children = subChild.trimWhiteSpaces()
                    )
                    val child = build(h1Node)
                    childNodes.add(child)
                }

                MdxType2.BLOCK_SYMBOL -> {
                    index++
                    val start = index
                    while (children.getOrNull(index) != null && children[index].literal != current.literal && children[index] != MdxNode2.EOF) {
                        index++
                    }
                    val subChild = children.subList(start, index)
                    index++ // skip the closing symbol

                    val nodeType = when (current.literal) {
                        "&" -> MdxType2.BOLD
                        "/" -> MdxType2.ITALIC
                        "_" -> MdxType2.UNDERLINE
                        "~" -> MdxType2.STRIKE
                        "\"" -> MdxType2.QUOTATION
                        else -> MdxType2.TEXT
                    }
                    val blockNode = MdxNode2(
                        type = nodeType,
                        children = subChild.trimWhiteSpaces()
                    )
                    val child = build(blockNode)
                    childNodes.add(child)
                }

                MdxType2.TABLE_START -> {
                    index++
                    val start = index
                    while (children.getOrNull(index) != null && children[index].type != MdxType2.TABLE_END && children[index] != MdxNode2.EOF) {
                        index++
                    }
                    val subChild = children.subList(start, index).trimWhiteSpaces()
                    index++ // skip the closing symbol

                    val tableColumns = mutableListOf<MdxNode2>()
                    var cols = mutableListOf<MdxNode2>()

                    subChild.forEach {
                        if (it.type == MdxType2.NEWLINE) {
                            val colNode = MdxNode2(
                                type = MdxType2.TABLE_COLOMN,
                                children = cols.trimWhiteSpaces()
                            )
                            val child = build(colNode)
                            tableColumns.add(child)
                            cols = mutableListOf()
                        } else cols.add(it)
                    }

                    if (cols.isNotEmpty()) {
                        val colNode = MdxNode2(
                            type = MdxType2.TABLE_COLOMN,
                            children = cols.trimWhiteSpaces()
                        )
                        val child = build(colNode)
                        tableColumns.add(child)
                        cols = mutableListOf()
                    }

                    val tableNode = MdxNode2(
                        type = MdxType2.TABLE,
                        children = tableColumns
                    )
                    childNodes.add(tableNode)
                }

                MdxType2.COLOR_START -> {
                    index++
                    val start = index
                    while (children.getOrNull(index) != null && children[index].type != MdxType2.COLOR_END && children[index] != MdxNode2.EOF) {
                        index++
                    }

                    var colorValue = "#222222"
                    var subChild = children.subList(start, index)

                    val last = subChild.last()
                    if (last.type == MdxType2.COLOR_HEX) {
                        colorValue = last.literal
                        subChild = subChild.dropLast(1)
                    }

                    index++ // skip the closing symbol
                    val coloredNode = MdxNode2(
                        type = MdxType2.COLORED,
                        literal = colorValue,
                        children = subChild.trimWhiteSpaces()
                    )
                    val child = build(coloredNode)
                    childNodes.add(child)
                }

                MdxType2.ELEMENT -> {
                    index++
                    val start = index
                    while (children.getOrNull(index) != null && children[index].type != MdxType2.NEWLINE && children[index] != MdxNode2.EOF) {
                        index++
                    }
                    val subChild = children.subList(start, index)
                    val nodeType = when (current.literal) {
                        "*" -> MdxType2.ELEMENT_BULLET
                        "*o" -> MdxType2.ELEMENT_UNCHECKED
                        "*x" -> MdxType2.ELEMENT_CHECKED
                        else -> MdxType2.ELEMENT
                    }

                    val elementNode = MdxNode2(
                        type = nodeType,
                        children = subChild.trimWhiteSpaces()
                    )
                    val child = build(elementNode)
                    childNodes.add(child)
                }

                else -> {
                    childNodes.add(children[index])
                    index++
                }
            }
        }

        return node.copy(children = childNodes)
    }

    private fun List<MdxNode2>.trimWhiteSpaces(): List<MdxNode2> {
        if (isEmpty()) {
            return emptyList()
        }

        if (size == 1) {
            val child = first()
            return if (child.type == MdxType2.WHITESPACE || child.type == MdxType2.NEWLINE) emptyList() else this
        } else {
            var modifiedList = this
            while (modifiedList.first().type == MdxType2.WHITESPACE || modifiedList.first().type == MdxType2.NEWLINE) {
                modifiedList = modifiedList.drop(1)
                if (modifiedList.isEmpty()) break
            }

            if (modifiedList.isEmpty()) {
                return emptyList()
            }

            while (modifiedList.last().type == MdxType2.WHITESPACE || modifiedList.last().type == MdxType2.NEWLINE) {
                modifiedList = modifiedList.dropLast(1)
                if (modifiedList.isEmpty()) break
            }

            return modifiedList.ifEmpty { emptyList() }
        }
    }
}


fun main() {
    val node = MdxParser2(MDX_TEST).parse()
    printNode(node)
}

private fun printNode(node: MdxNode2, level: Int = 0) {
    if (node.children.isEmpty()) {
        if (level > 0) {
            repeat(level) {
                print("|    ")
            }
        }
        println(node.type)
        return
    }

    if (level > 0) {
        repeat(level) {
            print("|    ")
        }
    }
    println(node.type)
    node.children.forEach {
        printNode(it, level + 1)
    }
}