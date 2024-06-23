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
import com.naulian.glow_core.mdx.MdxNode2
import com.naulian.glow_core.mdx.MdxParser2
import com.naulian.glow_core.mdx.MdxType2

@Composable
fun MdxBlock2(
    modifier: Modifier = Modifier,
    source: String,
    onClickLink: (String) -> Unit = {},
    components: MdxComponents2 = mdxComponents2(),
    contentSpacing: Dp = 12.dp
) {
    var node by remember {
        mutableStateOf(MdxNode2())
    }

    LaunchedEffect(key1 = source) {
        node = MdxParser2(source).parse()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(contentSpacing)
    ) {
        HandleNode(node, components, onClickLink)
    }
}

@Composable
fun HandleNode(node: MdxNode2, components: MdxComponents2, onClickLink: (String) -> Unit) {
    node.children.forEach {
        when (it.type) {
            MdxType2.PARAGRAPH -> components.paragraph(it, onClickLink)
            MdxType2.H1, MdxType2.H2, MdxType2.H3, MdxType2.H4, MdxType2.H5, MdxType2.H6 -> {
                components.header(it)
            }

            MdxType2.DIVIDER -> components.divider(it)
            MdxType2.CODE -> components.codeBlock(it)
            MdxType2.QUOTATION -> components.quote(it)
            MdxType2.IMAGE -> components.image(it)
            MdxType2.YOUTUBE -> components.youtube(it)
            MdxType2.VIDEO -> components.video(it)
            MdxType2.ELEMENT -> components.elements(it)

            MdxType2.TABLE -> components.table(it)
            else -> components.text(it, 16.sp, 20.sp) {}
        }
    }
}


@Composable
fun AnnotatedString.Builder.handleText(
    node: MdxNode2,
    linkMap: Map<String, String> = emptyMap()
): Map<String, String> {

    val map = hashMapOf<String, String>()
    linkMap.forEach { map[it.key] = it.value }

    if (node.children.isEmpty()) {
        when (node.type) {
            MdxType2.LINK, MdxType2.HYPER_LINK -> {
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
        MdxType2.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
        MdxType2.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
        MdxType2.STRIKE -> SpanStyle(textDecoration = TextDecoration.LineThrough)
        MdxType2.UNDERLINE -> SpanStyle(textDecoration = TextDecoration.Underline)
        MdxType2.COLORED -> SpanStyle(color = node.literal.hexToColor())
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

data class MdxComponents2(
    val fontFamily: FontFamily,
    val text: @Composable (
        node: MdxNode2,
        size: TextUnit,
        height: TextUnit,
        onClickLink: (String) -> Unit
    ) -> Unit,
    val paragraph: @Composable (MdxNode2, onClickLink: (String) -> Unit) -> Unit,
    val header: @Composable (MdxNode2) -> Unit,
    val quote: @Composable (MdxNode2) -> Unit,
    val codeBlock: @Composable (MdxNode2) -> Unit,
    val image: @Composable (MdxNode2) -> Unit,
    val youtube: @Composable (MdxNode2) -> Unit,
    val video: @Composable (MdxNode2) -> Unit,
    val divider: @Composable (MdxNode2) -> Unit,
    val table: @Composable (MdxNode2) -> Unit,
    val elements: @Composable (MdxNode2) -> Unit,
)

@Composable
fun buildContentPair(node: MdxNode2): MdxParagraphContent {
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

fun mdxComponents2(
    fontFamily: FontFamily = FontFamily.Default,
    text: @Composable (
        node: MdxNode2,
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
    paragraph: @Composable (MdxNode2, onClickLink: (String) -> Unit) -> Unit = { node, onClickLink ->
        text(node, 16.sp, 20.sp, onClickLink)
    },
    header: @Composable (MdxNode2) -> Unit = { node ->
        val sizePair = when (node.type) {
            MdxType2.H1 -> 32.sp to 36.sp
            MdxType2.H2 -> 28.sp to 32.sp
            MdxType2.H3 -> 24.sp to 38.sp
            MdxType2.H4 -> 20.sp to 24.sp
            MdxType2.H5 -> 18.sp to 22.sp
            MdxType2.H6 -> 16.sp to 20.sp
            else -> 24.sp to 32.sp
        }
        text(node, sizePair.first, sizePair.second) {}
    },
    quote: @Composable (MdxNode2) -> Unit = { node ->
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
                        MdxType2.PARAGRAPH -> text(it, 16.sp, 20.sp) {}
                        else -> {}
                    }
                }
            }
        }
    },
    codeBlock: @Composable (MdxNode2) -> Unit = { node ->
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
    image: @Composable (MdxNode2) -> Unit = { node ->
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
    youtube: @Composable (MdxNode2) -> Unit = { },
    video: @Composable (MdxNode2) -> Unit = { },
    divider: @Composable (MdxNode2) -> Unit = { node ->
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
    table: @Composable (MdxNode2) -> Unit = { node ->
        Column(modifier = Modifier.fillMaxWidth()) {
            node.children.forEach { cols ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    cols.children.forEach { child ->
                        when (child.type) {
                            MdxType2.PARAGRAPH -> {
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
    elements: @Composable (MdxNode2) -> Unit = { node ->
        Column(modifier = Modifier.fillMaxWidth()) {
            node.children.forEach { element ->
                when (element.type) {
                    MdxType2.ELEMENT_UNCHECKED -> {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "\u2610")
                            Spacer(modifier = Modifier.width(12.dp))
                            element.children.forEach {
                                when (it.type) {
                                    MdxType2.PARAGRAPH -> text(it, 16.sp, 20.sp) {}
                                    else -> {}
                                }
                            }
                        }
                    }

                    MdxType2.ELEMENT_CHECKED -> {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "\u2611")
                            Spacer(modifier = Modifier.width(12.dp))
                            element.children.forEach {
                                when (it.type) {
                                    MdxType2.PARAGRAPH -> text(it, 16.sp, 20.sp) {}
                                    else -> {}
                                }
                            }
                        }
                    }

                    MdxType2.ELEMENT_BULLET -> {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "\u2022")
                            Spacer(modifier = Modifier.width(12.dp))
                            element.children.forEach {
                                when (it.type) {
                                    MdxType2.PARAGRAPH -> text(it, 16.sp, 20.sp) {}
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }
        }
    }
) = MdxComponents2(
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
            MdxBlock2(modifier = Modifier.padding(16.dp), source = MDX_TEST)
        }
    }
}