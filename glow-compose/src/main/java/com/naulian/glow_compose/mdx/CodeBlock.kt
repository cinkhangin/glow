package com.naulian.glow_compose.mdx

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.naulian.anhance.copyString
import com.naulian.glow.CodeTheme
import com.naulian.glow.Theme
import com.naulian.glow_compose.Glow
import com.naulian.glow_compose.R
import com.naulian.glow_compose.font

val emptyAnnotatedString = buildAnnotatedString { }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CodeSnippet(
    modifier: Modifier = Modifier,
    source: String,
    language: String = "txt",
    theme: Theme = CodeTheme.defaultLight,
    fontFamily: FontFamily = font,
    onLongClick: (() -> Unit)? = null
) {
    var glowCode by remember { mutableStateOf(emptyAnnotatedString) }

    LaunchedEffect(key1 = Unit) {
        glowCode = Glow.highlight(source, language, theme).value
    }

    LazyRow(
        modifier = modifier
            .combinedClickable(
                enabled = onLongClick != null,
                onLongClick = onLongClick,
                onClick = {}
            )
    ) {
        item {
            Text(
                text = glowCode,
                fontFamily = fontFamily,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.dp),
            )
        }
    }
}

@Preview
@Composable
private fun CodeSnippetPreview() {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            tertiary = Color(0xFFE8EAF6)
        )
    ) {
        Surface(color = Color.LightGray) {
            CodeSnippet(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                source = """
                    fun main() {
                        println("Hello World")
                    }
                """.trimIndent(),
                language = "kt"
            )
        }
    }
}


@Composable
fun CodeBlock(
    modifier: Modifier = Modifier,
    source: String,
    language: String = "txt",
    codeName: String = "",
    codeTheme: Theme = CodeTheme.defaultLight,
    actionIcon: Int = R.drawable.ic_copy,
    onClickAction: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (name, action, code) = createRefs()

            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiary)
                    .constrainAs(name) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = codeName.ifEmpty { language },
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            IconButton(
                onClick = {
                    onClickAction?.invoke() ?: context.copyString(source)
                },
                modifier = Modifier.constrainAs(action) {
                    end.linkTo(parent.end)
                    top.linkTo(name.top)
                    bottom.linkTo(name.bottom)
                }
            ) {
                Icon(
                    painter = painterResource(id = actionIcon),
                    contentDescription = null
                )
            }

            CodeSnippet(
                modifier = Modifier
                    .constrainAs(code) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(name.bottom)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    },
                source = source,
                language = language,
                theme = codeTheme
            )
        }
    }
}

@Preview
@Composable
private fun CodeBlockPreview() {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            tertiary = Color(0xFFE8EAF6)
        )
    ) {
        Surface(color = Color.LightGray) {
            CodeBlock(
                modifier = Modifier.padding(16.dp),
                source = """
                    fun main() {
                        println("Hello World")
                    }
                """.trimIndent(),
                codeName = "Kotlin Hello World",
                language = "kt"
            )
        }
    }
}

