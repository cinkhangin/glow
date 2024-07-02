package com.naulian.glow_core.mdx

class MdxParser(private val source: String) {

    fun parse(): MdxNode {
        val nodes = MdxLexer(source).tokenize()
        nodes.forEach(::println)
        val astBuilder = ASTBuilder(nodes)
        val treeNode = astBuilder.build()
        val typedNode = astBuilder.buildTyped(treeNode)
        return typedNode
    }
}

private val mdxTextTypes = listOf(
    MdxType.BLOCK_SYMBOL,
    MdxType.COLOR_START,
    MdxType.COLOR_END,
    MdxType.BOLD,
    MdxType.ITALIC,
    MdxType.UNDERLINE,
    MdxType.STRIKE,
    MdxType.TEXT,
    MdxType.PARAGRAPH,
    MdxType.LINK,
    MdxType.HYPER_LINK,
    MdxType.WHITESPACE,
    MdxType.NEWLINE,
    MdxType.DATETIME,
    MdxType.ESCAPE,
    MdxType.IGNORE,
    MdxType.COLORED
)

private val mdxElementTypes = listOf(
    MdxType.ELEMENT,
    MdxType.ELEMENT_BULLET,
    MdxType.ELEMENT_UNCHECKED,
    MdxType.ELEMENT_CHECKED
)

class ASTBuilder(private val nodes: List<MdxNode>) {

    fun buildTyped(node: MdxNode = MdxNode(children = nodes)): MdxNode {
        if (node.children.isEmpty()) {
            return node
        }

        var index = 0
        val children = node.children
        val childNodes = mutableListOf<MdxNode>()
        while (children.getOrNull(index) != null && children[index].type != MdxType.EOF) {
            val current = children[index]
            when (current.type) {
                in mdxTextTypes -> {
                    val start = index
                    while (children.getOrNull(index) != null && children[index].type in mdxTextTypes && children[index] != MdxNode.EOF) {
                        index++
                    }

                    val subChild = children.subList(start, index)
                    val paragraphNode = MdxNode(
                        type = MdxType.PARAGRAPH,
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
                                || children[index].type == MdxType.NEWLINE)
                        && children[index] != MdxNode.EOF
                    ) {
                        index++
                    }
                    val subChild = children.subList(start, index)
                        .filter { it.type != MdxType.NEWLINE }
                        .map { buildTyped(it) }

                    val elementNode = MdxNode(
                        type = MdxType.ELEMENT,
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

    fun build(node: MdxNode = MdxNode(children = nodes)): MdxNode {
        if (node.children.isEmpty()) {
            return node
        }

        var index = 0
        val children = node.children
        val childNodes = mutableListOf<MdxNode>()
        while (children.getOrNull(index) != null && children[index].type != MdxType.EOF) {
            val current = children[index]
            when (current.type) {
                MdxType.HEADER -> {
                    index++
                    val start = index
                    while (children.getOrNull(index) != null && children[index].type != MdxType.NEWLINE && children[index] != MdxNode.EOF) {
                        index++
                    }
                    val subChild = children.subList(start, index)

                    val nodeType = when (current.literal) {
                        "#1" -> MdxType.H1
                        "#2" -> MdxType.H2
                        "#3" -> MdxType.H3
                        "#4" -> MdxType.H4
                        "#5" -> MdxType.H5
                        "#6" -> MdxType.H6
                        else -> MdxType.TEXT
                    }
                    val h1Node = MdxNode(
                        type = nodeType,
                        children = subChild.trimWhiteSpaces()
                    )
                    val child = build(h1Node)
                    childNodes.add(child)
                }

                MdxType.BLOCK_SYMBOL -> {
                    index++
                    val start = index
                    while (children.getOrNull(index) != null && children[index].literal != current.literal && children[index] != MdxNode.EOF) {
                        index++
                    }
                    val subChild = children.subList(start, index)
                    index++ // skip the closing symbol

                    val nodeType = when (current.literal) {
                        "&" -> MdxType.BOLD
                        "/" -> MdxType.ITALIC
                        "_" -> MdxType.UNDERLINE
                        "~" -> MdxType.STRIKE
                        "\"" -> MdxType.QUOTATION
                        else -> MdxType.TEXT
                    }
                    val blockNode = MdxNode(
                        type = nodeType,
                        children = subChild.trimWhiteSpaces()
                    )
                    val child = build(blockNode)
                    childNodes.add(child)
                }

                MdxType.TABLE_START -> {
                    index++
                    val start = index
                    while (children.getOrNull(index) != null && children[index].type != MdxType.TABLE_END && children[index] != MdxNode.EOF) {
                        index++
                    }
                    val subChild = children.subList(start, index).trimWhiteSpaces()
                    index++ // skip the closing symbol

                    val tableColumns = mutableListOf<MdxNode>()
                    var cols = mutableListOf<MdxNode>()

                    subChild.forEach {
                        if (it.type == MdxType.NEWLINE) {
                            val colNode = MdxNode(
                                type = MdxType.TABLE_COLOMN,
                                children = cols.trimWhiteSpaces()
                            )
                            val child = build(colNode)
                            tableColumns.add(child)
                            cols = mutableListOf()
                        } else cols.add(it)
                    }

                    if (cols.isNotEmpty()) {
                        val colNode = MdxNode(
                            type = MdxType.TABLE_COLOMN,
                            children = cols.trimWhiteSpaces()
                        )
                        val child = build(colNode)
                        tableColumns.add(child)
                        cols = mutableListOf()
                    }

                    val tableNode = MdxNode(
                        type = MdxType.TABLE,
                        children = tableColumns
                    )
                    childNodes.add(tableNode)
                }

                MdxType.COLOR_START -> {
                    index++
                    val start = index
                    while (children.getOrNull(index) != null && children[index].type != MdxType.COLOR_END && children[index] != MdxNode.EOF) {
                        index++
                    }

                    var colorValue = "#222222"
                    var subChild = children.subList(start, index)

                    val last = subChild.last()
                    if (last.type == MdxType.COLOR_HEX) {
                        colorValue = last.literal
                        subChild = subChild.dropLast(1)
                    }

                    index++ // skip the closing symbol
                    val coloredNode = MdxNode(
                        type = MdxType.COLORED,
                        literal = colorValue,
                        children = subChild.trimWhiteSpaces()
                    )
                    val child = build(coloredNode)
                    childNodes.add(child)
                }

                MdxType.ELEMENT -> {
                    index++
                    val start = index
                    while (children.getOrNull(index) != null && children[index].type != MdxType.NEWLINE && children[index] != MdxNode.EOF) {
                        index++
                    }
                    val subChild = children.subList(start, index)
                    val nodeType = when (current.literal) {
                        "*" -> MdxType.ELEMENT_BULLET
                        "*o" -> MdxType.ELEMENT_UNCHECKED
                        "*x" -> MdxType.ELEMENT_CHECKED
                        else -> MdxType.ELEMENT
                    }

                    val elementNode = MdxNode(
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

    private fun List<MdxNode>.trimWhiteSpaces(): List<MdxNode> {
        if (isEmpty()) {
            return emptyList()
        }

        if (size == 1) {
            val child = first()
            return if (child.type == MdxType.WHITESPACE || child.type == MdxType.NEWLINE) emptyList() else this
        } else {
            var modifiedList = this
            while (modifiedList.first().type == MdxType.WHITESPACE || modifiedList.first().type == MdxType.NEWLINE) {
                modifiedList = modifiedList.drop(1)
                if (modifiedList.isEmpty()) break
            }

            if (modifiedList.isEmpty()) {
                return emptyList()
            }

            while (modifiedList.last().type == MdxType.WHITESPACE || modifiedList.last().type == MdxType.NEWLINE) {
                modifiedList = modifiedList.dropLast(1)
                if (modifiedList.isEmpty()) break
            }

            return modifiedList.ifEmpty { emptyList() }
        }
    }
}


fun main() {
    val node = MdxParser(MDX_TEST).parse()
    printNode(node)
}

private fun printNode(node: MdxNode, level: Int = 0) {
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