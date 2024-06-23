package com.naulian.glow_compose.mdx

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.text.isDigitsOnly
import coil.compose.AsyncImage
import com.naulian.anhance.millisOfNow
import com.naulian.glow_compose.R
import com.naulian.glow_compose.hexToColor
import com.naulian.glow_core.mdx.MDX_TEST
import com.naulian.glow_core.mdx.MdxNode
import com.naulian.glow_core.mdx.MdxParser
import com.naulian.glow_core.mdx.MdxType

@Composable
fun MdxBlock(
    modifier: Modifier = Modifier,
    source: String,
    onClickLink: (String) -> Unit = {},
    components: MdxComponents = mdxComponents(),
    contentSpacing: Dp = 12.dp
) {
    var node by remember {
        mutableStateOf(MdxNode())
    }

    LaunchedEffect(key1 = source) {
        node = MdxParser(source).parse()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(contentSpacing)
    ) {
        HandleNode(node, components, onClickLink)
    }
}

@Composable
fun HandleNode(node: MdxNode, components: MdxComponents, onClickLink: (String) -> Unit) {
    node.children.forEach {
        when (it.type) {
            MdxType.PARAGRAPH -> components.paragraph(it, onClickLink)
            MdxType.H1, MdxType.H2, MdxType.H3, MdxType.H4, MdxType.H5, MdxType.H6 -> {
                components.header(it)
            }

            MdxType.DIVIDER -> components.divider(it)
            MdxType.CODE -> components.codeBlock(it)
            MdxType.QUOTATION -> components.quote(it)
            MdxType.IMAGE -> components.image(it)
            MdxType.YOUTUBE -> components.youtube(it)
            MdxType.VIDEO -> components.video(it)
            MdxType.ELEMENT -> components.elements(it)

            MdxType.TABLE -> components.table(it)
            else -> components.text(it, 16.sp, 20.sp) {}
        }
    }
}


@Composable
fun AnnotatedString.Builder.handleText(
    node: MdxNode,
    linkMap: Map<String, String> = emptyMap()
): Map<String, String> {

    val map = hashMapOf<String, String>()
    linkMap.forEach { map[it.key] = it.value }

    if (node.children.isEmpty()) {
        when (node.type) {
            MdxType.LINK, MdxType.HYPER_LINK -> {
                pushStyle(style = SpanStyle(color = Color.Blue))
                val tag = "link$millisOfNow"
                val (hyper, link) = node.getHyperLink()
                pushStringAnnotation(tag, "link")
                append(hyper.ifEmpty { link })
                pop() // annotation
                pop() //style
                map[tag] = link
                return map
            }

            else -> append(node.literal)
        }
        return map
    }

    val spanStyle = when (node.type) {
        MdxType.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
        MdxType.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
        MdxType.STRIKE -> SpanStyle(textDecoration = TextDecoration.LineThrough)
        MdxType.UNDERLINE -> SpanStyle(textDecoration = TextDecoration.Underline)
        MdxType.COLORED -> SpanStyle(color = node.literal.hexToColor())
        else -> SpanStyle()
    }

    pushStyle(style = spanStyle)
    node.children.forEach { child ->
        val innerMap = handleText(node = child, map)
        innerMap.forEach { map[it.key] = it.value }
    }
    pop()
    return map
}

data class MdxComponents(
    val fontFamily: FontFamily,
    val text: @Composable (
        node: MdxNode,
        size: TextUnit,
        height: TextUnit,
        onClickLink: (String) -> Unit
    ) -> Unit,
    val paragraph: @Composable (MdxNode, onClickLink: (String) -> Unit) -> Unit,
    val header: @Composable (MdxNode) -> Unit,
    val quote: @Composable (MdxNode) -> Unit,
    val codeBlock: @Composable (MdxNode) -> Unit,
    val image: @Composable (MdxNode) -> Unit,
    val youtube: @Composable (MdxNode) -> Unit,
    val video: @Composable (MdxNode) -> Unit,
    val divider: @Composable (MdxNode) -> Unit,
    val table: @Composable (MdxNode) -> Unit,
    val elements: @Composable (MdxNode) -> Unit,
)

@Composable
fun buildContentPair(node: MdxNode): MdxParagraphContent {
    var linkMap = mapOf<String, String>()
    val annotatedString = buildAnnotatedString {
        linkMap = handleText(node = node)
    }
    return MdxParagraphContent(annotatedString, linkMap)
}

data class MdxParagraphContent(
    val annotatedString: AnnotatedString = emptyAnnotatedString,
    val linkMap: Map<String, String> = emptyMap()
)

fun mdxComponents(
    fontFamily: FontFamily = FontFamily.Default,
    text: @Composable (
        node: MdxNode,
        size: TextUnit,
        height: TextUnit,
        onClickLink: (String) -> Unit
    ) -> Unit = { node, size, height, onClickLink ->
        when {
            node.children.isEmpty() && node.literal.isBlank() -> {}
            else -> {
                val paragraphContent = buildContentPair(node)
                ClickableText(
                    text = paragraphContent.annotatedString,
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = size,
                        lineHeight = height
                    ),
                ) { offset ->
                    paragraphContent.annotatedString.getStringAnnotations(
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let { paragraphContent.linkMap[it.tag]?.let(onClickLink) }
                }
            }
        }
    },
    paragraph: @Composable (MdxNode, onClickLink: (String) -> Unit) -> Unit = { node, onClickLink ->
        text(node, 16.sp, 20.sp, onClickLink)
    },
    header: @Composable (MdxNode) -> Unit = { node ->
        val sizePair = when (node.type) {
            MdxType.H1 -> 32.sp to 36.sp
            MdxType.H2 -> 28.sp to 32.sp
            MdxType.H3 -> 24.sp to 38.sp
            MdxType.H4 -> 20.sp to 24.sp
            MdxType.H5 -> 18.sp to 22.sp
            MdxType.H6 -> 16.sp to 20.sp
            else -> 24.sp to 32.sp
        }
        text(node, sizePair.first, sizePair.second) {}
    },
    quote: @Composable (MdxNode) -> Unit = { node ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.small
                )
                .clip(MaterialTheme.shapes.small)
        ) {
            val (accent, content) = createRefs()
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .constrainAs(accent) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    }
            )
            Box(
                modifier = Modifier
                    .constrainAs(content) {
                        start.linkTo(accent.end)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }
                    .padding(12.dp)
            ) {
                node.children.forEach {
                    when (it.type) {
                        MdxType.PARAGRAPH -> text(it, 16.sp, 20.sp) {}
                        else -> {}
                    }
                }
            }
        }
    },
    codeBlock: @Composable (MdxNode) -> Unit = { node ->
        val (lang, code) = node.getLangCodePair()
        when (lang) {
            "comment" -> {}
            else -> CodeSnippet(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.small
                    )
                    .clip(MaterialTheme.shapes.small),
                source = code,
                language = lang
            )
        }
    },
    image: @Composable (MdxNode) -> Unit = { node ->
        var isError by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            isError = false
        }

        if (!isError) {
            AsyncImage(
                model = node.literal,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small),
                contentDescription = "Atx Picture",
                contentScale = ContentScale.FillWidth,
                placeholder = painterResource(R.drawable.img_placeholder),
                onError = { isError = true },
            )
        }
    },
    youtube: @Composable (MdxNode) -> Unit = { },
    video: @Composable (MdxNode) -> Unit = { },
    divider: @Composable (MdxNode) -> Unit = { node ->
        when (node.literal) {
            "" -> {}
            "br" -> Spacer(modifier = Modifier.height(1.dp))
            "line" -> HorizontalDivider()
            "dash" -> HorizontalDashDivider()
            else -> {
                if (node.literal.isDigitsOnly()) {
                    val sizeInt = node.literal.toInt()
                    Spacer(modifier = Modifier.height(sizeInt.dp))
                }
            }
        }
    },
    table: @Composable (MdxNode) -> Unit = { node ->
        Column(modifier = Modifier.fillMaxWidth()) {
            node.children.forEach { cols ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    cols.children.forEach { child ->
                        when (child.type) {
                            MdxType.PARAGRAPH -> {
                                Box(modifier = Modifier.weight(1f)) {
                                    text(child, 16.sp, 20.sp) {}
                                }
                            }
                        }
                    }
                }
            }
        }
    },
    elements: @Composable (MdxNode) -> Unit = { node ->
        Column(modifier = Modifier.fillMaxWidth()) {
            node.children.forEach { element ->
                when (element.type) {
                    MdxType.ELEMENT_UNCHECKED -> {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "\u2610")
                            Spacer(modifier = Modifier.width(12.dp))
                            element.children.forEach {
                                when (it.type) {
                                    MdxType.PARAGRAPH -> text(it, 16.sp, 20.sp) {}
                                    else -> {}
                                }
                            }
                        }
                    }

                    MdxType.ELEMENT_CHECKED -> {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "\u2611")
                            Spacer(modifier = Modifier.width(12.dp))
                            element.children.forEach {
                                when (it.type) {
                                    MdxType.PARAGRAPH -> text(it, 16.sp, 20.sp) {}
                                    else -> {}
                                }
                            }
                        }
                    }

                    MdxType.ELEMENT_BULLET -> {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "\u2022")
                            Spacer(modifier = Modifier.width(12.dp))
                            element.children.forEach {
                                when (it.type) {
                                    MdxType.PARAGRAPH -> text(it, 16.sp, 20.sp) {}
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }
        }
    }
) = MdxComponents(
    fontFamily = fontFamily,
    text = text,
    paragraph = paragraph,
    header = header,
    quote = quote,
    codeBlock = codeBlock,
    image = image,
    youtube = youtube,
    video = video,
    divider = divider,
    table = table,
    elements = elements
)


@Preview(heightDp = 1500)
@Composable
private fun MdxBlock2Preview() {
    MaterialTheme {
        Surface(color = Color.White) {
            MdxBlock(modifier = Modifier.padding(16.dp), source = MDX_TEST)
        }
    }
}