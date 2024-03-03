package com.naulian.glow_compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import com.naulian.anhance.logError
import com.naulian.glow.Theme
import com.naulian.glow.tokens.JTokens
import com.naulian.glow.tokens.JsTokens
import com.naulian.glow.tokens.KTokens
import com.naulian.glow.tokens.PTokens
import com.naulian.glow.tokens.Type
import android.graphics.Color as LegacyColor

val font = FontFamily(Font(R.font.jetbrains_mono))
fun spanStyle(color: Color) = SpanStyle(
    color = color, fontFamily = font
)

fun String.toAnnotatedString(): AnnotatedString {
    return buildAnnotatedString {
        append(this@toAnnotatedString)
    }
}

fun String.toComposeColor(): Color {
    return if (startsWith("#")) {
        val legacyColor = LegacyColor.parseColor(this)
        Color(legacyColor)
    } else {
        logError("GLow: toComposeColor", "color should start with #. at $this")
        Color.Black
    }
}

object Glow {
    private val TAG = Glow::class.java.simpleName

    fun highlight(source: String, language: String, theme: Theme = Theme()): HighLight {
        return when (language.lowercase()) {
            "java" -> hlJava(source, theme)
            "python", "py" -> hlPython(source, theme)
            "kotlin", "kt" -> hlKotlin(source, theme)
            "javascript", "js" -> hlJavaScript(source, theme)
            else -> hlText(source)
        }
    }

    fun hlText(input: String): HighLight {
        val annotatedString = input.toAnnotatedString()
        return HighLight(annotatedString, input)
    }


    fun hlJava(input: String, theme: Theme = Theme()): HighLight {
        val tokens = JTokens.tokenize(input)

        val builder = buildAnnotatedString {
            tokens.forEach {
                val hlColor = when (it.type) {
                    Type.LT, Type.GT -> theme.normal
                    Type.KEYWORD -> theme.keyword
                    Type.PROPERTY -> theme.property
                    Type.VARIABLE -> theme.keyword
                    Type.VAR_NAME -> theme.variable
                    Type.CLASS -> theme.keyword
                    Type.FUNCTION -> theme.keyword
                    Type.FUNC_NAME -> theme.method
                    Type.FUNC_CALL -> theme.method
                    Type.NUMBER -> theme.number
                    Type.VALUE_INT -> theme.number
                    Type.VALUE_LONG -> theme.number
                    Type.VALUE_FLOAT -> theme.number
                    Type.CHAR -> theme.string
                    Type.STRING -> theme.string
                    Type.ASSIGNMENT -> theme.normal
                    Type.COMMENT_MULTI -> theme.comment
                    Type.COMMENT_SINGLE -> theme.comment
                    else -> it.value
                }.toComposeColor()

                val hlCode = it.value
                withStyle(style = spanStyle(color = hlColor)) {
                    append(hlCode)
                }
            }
        }

        return HighLight(builder, builder.toString())
    }

    fun hlJavaScript(input: String, theme: Theme = Theme()): HighLight {
        val tokens = JsTokens.tokenize(input)

        val builder = buildAnnotatedString {
            tokens.forEach {
                val hlColor = when (it.type) {
                    Type.LT, Type.GT -> theme.normal
                    Type.KEYWORD -> theme.keyword
                    Type.VARIABLE -> theme.keyword
                    Type.VAR_NAME -> theme.variable
                    Type.CLASS -> theme.keyword
                    Type.FUNCTION -> theme.keyword
                    Type.FUNC_NAME -> theme.method
                    Type.NUMBER -> theme.number
                    Type.VALUE_INT -> theme.number
                    Type.VALUE_LONG -> theme.number
                    Type.VALUE_FLOAT -> theme.number
                    Type.CHAR -> theme.string
                    Type.STRING -> theme.string
                    Type.ASSIGNMENT -> theme.normal
                    Type.COMMENT_MULTI -> theme.comment
                    Type.COMMENT_SINGLE -> theme.comment
                    else -> it.value
                }.toComposeColor()

                val hlCode = it.value

                withStyle(style = spanStyle(color = hlColor)) {
                    append(hlCode)
                }
            }
        }

        return HighLight(builder, builder.toString())
    }

    fun hlPython(input: String, theme: Theme = Theme()): HighLight {
        val tokens = PTokens.tokenize(input)

        val builder = buildAnnotatedString {
            tokens.forEach {
                val hlColor = when (it.type) {
                    Type.LT, Type.GT -> theme.normal
                    Type.KEYWORD -> theme.keyword
                    Type.PROPERTY -> theme.property
                    Type.CLASS -> theme.keyword
                    Type.FUNCTION -> theme.keyword
                    Type.FUNC_NAME -> theme.method
                    Type.FUNC_CALL -> theme.method
                    Type.NUMBER -> theme.number
                    Type.VALUE_INT -> theme.number
                    Type.VALUE_LONG -> theme.number
                    Type.VALUE_FLOAT -> theme.number
                    Type.STRING -> theme.string
                    Type.ASSIGNMENT -> theme.normal
                    Type.COMMENT_SINGLE -> theme.comment
                    else -> it.value
                }.toComposeColor()

                val hlCode = it.value
                withStyle(style = spanStyle(color = hlColor)) {
                    append(hlCode)
                }
            }
        }

        return HighLight(builder, builder.toString())
    }

    fun hlKotlin(input: String, theme: Theme = Theme()): HighLight {
        val tokens = KTokens.tokenize(input)

        val builder = buildAnnotatedString {
            tokens.forEach {
                val hlColor = when (it.type) {
                    Type.LT, Type.GT -> theme.normal
                    Type.KEYWORD -> theme.keyword
                    Type.VARIABLE -> theme.keyword
                    Type.VAR_NAME -> theme.variable
                    Type.CLASS -> theme.keyword
                    Type.FUNCTION -> theme.keyword
                    Type.FUNC_NAME -> theme.method
                    Type.FUNC_CALL -> theme.method
                    Type.NUMBER -> theme.number
                    Type.VALUE_INT -> theme.number
                    Type.VALUE_LONG -> theme.number
                    Type.VALUE_FLOAT -> theme.number
                    Type.CHAR -> theme.string
                    Type.PROPERTY -> theme.property
                    Type.STRING -> theme.string
                    Type.STRING_BRACE -> theme.keyword
                    Type.INTERPOLATION -> theme.property
                    Type.ASSIGNMENT -> theme.normal
                    Type.ESCAPE -> theme.keyword
                    Type.COMMENT_MULTI -> theme.comment
                    Type.COMMENT_SINGLE -> theme.comment
                    else -> it.value
                }.toComposeColor()

                val hlCode = it.value
                withStyle(style = spanStyle(color = hlColor)) {
                    append(hlCode)
                }
            }
        }
        return HighLight(builder, builder.toString())
    }
}