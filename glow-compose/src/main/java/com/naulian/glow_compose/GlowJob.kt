@file:Suppress("unused")

package com.naulian.glow_compose

import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.naulian.glow.Theme
import com.naulian.glow.language.kotlin.tokenizeKt
import com.naulian.glow.tokens.JTokens
import com.naulian.glow.tokens.JsTokens
import com.naulian.glow.tokens.PTokens
import com.naulian.glow.tokens.TxtTokens

fun launchGlowJob(source: String, language: String, theme: Theme): GlowJob {
    return GlowJob().launch(source, language, theme)
}

class GlowJob {
    private var _tokens = emptyList<com.naulian.glow_core.Token>()
    val tokens get() = _tokens

    private var _highLight = HighLight(value = buildAnnotatedString { })
    val highLight get() = _highLight

    fun launch(source: String, language: String, theme: Theme): GlowJob {
        tokenize(source, language)
        highLight(language, theme)
        return this
    }

    private fun highLight(language: String, theme: Theme) {
        _highLight = when (language.lowercase()) {
            "java" -> hlJava(theme)
            "python", "py" -> hlPython(theme)
            "kotlin", "kt" -> hlKotlin(theme)
            "javascript", "js" -> hlJavaScript(theme)
            else -> hlText()
        }
    }

    private fun hlText(): HighLight {
        val annotatedString = buildAnnotatedString {
            tokens.forEach { append(it.value) }
        }
        return HighLight(annotatedString, annotatedString.text)
    }


    private fun hlJava(theme: Theme = Theme()): HighLight {
        val builder = buildAnnotatedString {
            tokens.forEach {
                val hlColor = when (it.type) {
                    com.naulian.glow_core.Type.LT, com.naulian.glow_core.Type.GT -> theme.normal
                    com.naulian.glow_core.Type.KEYWORD -> theme.keyword
                    com.naulian.glow_core.Type.PROPERTY -> theme.property
                    com.naulian.glow_core.Type.VARIABLE -> theme.keyword
                    com.naulian.glow_core.Type.VAR_NAME -> theme.variable
                    com.naulian.glow_core.Type.CLASS -> theme.keyword
                    com.naulian.glow_core.Type.FUNCTION -> theme.keyword
                    com.naulian.glow_core.Type.FUNC_NAME -> theme.method
                    com.naulian.glow_core.Type.FUNC_CALL -> theme.method
                    com.naulian.glow_core.Type.NUMBER -> theme.number
                    com.naulian.glow_core.Type.VALUE_INT -> theme.number
                    com.naulian.glow_core.Type.VALUE_LONG -> theme.number
                    com.naulian.glow_core.Type.VALUE_FLOAT -> theme.number
                    com.naulian.glow_core.Type.CHAR -> theme.string
                    com.naulian.glow_core.Type.STRING -> theme.string
                    com.naulian.glow_core.Type.ASSIGNMENT -> theme.normal
                    com.naulian.glow_core.Type.COMMENT_MULTI -> theme.comment
                    com.naulian.glow_core.Type.COMMENT_SINGLE -> theme.comment
                    else -> theme.normal
                }.toComposeColor()

                val hlCode = it.value
                withStyle(style = spanStyle(color = hlColor)) {
                    append(hlCode)
                }
            }
        }

        return HighLight(builder, builder.toString())
    }

    private fun hlJavaScript(theme: Theme = Theme()): HighLight {
        val builder = buildAnnotatedString {
            tokens.forEach {
                val hlColor = when (it.type) {
                    com.naulian.glow_core.Type.LT, com.naulian.glow_core.Type.GT -> theme.normal
                    com.naulian.glow_core.Type.KEYWORD -> theme.keyword
                    com.naulian.glow_core.Type.VARIABLE -> theme.keyword
                    com.naulian.glow_core.Type.VAR_NAME -> theme.variable
                    com.naulian.glow_core.Type.CLASS -> theme.keyword
                    com.naulian.glow_core.Type.FUNCTION -> theme.keyword
                    com.naulian.glow_core.Type.FUNC_NAME -> theme.method
                    com.naulian.glow_core.Type.NUMBER -> theme.number
                    com.naulian.glow_core.Type.VALUE_INT -> theme.number
                    com.naulian.glow_core.Type.VALUE_LONG -> theme.number
                    com.naulian.glow_core.Type.VALUE_FLOAT -> theme.number
                    com.naulian.glow_core.Type.CHAR -> theme.string
                    com.naulian.glow_core.Type.STRING -> theme.string
                    com.naulian.glow_core.Type.ASSIGNMENT -> theme.normal
                    com.naulian.glow_core.Type.COMMENT_MULTI -> theme.comment
                    com.naulian.glow_core.Type.COMMENT_SINGLE -> theme.comment
                    else -> theme.normal
                }.toComposeColor()

                val hlCode = it.value

                withStyle(style = spanStyle(color = hlColor)) {
                    append(hlCode)
                }
            }
        }

        return HighLight(builder, builder.toString())
    }

    private fun hlPython(theme: Theme = Theme()): HighLight {
        val builder = buildAnnotatedString {
            tokens.forEach {
                val hlColor = when (it.type) {
                    com.naulian.glow_core.Type.LT, com.naulian.glow_core.Type.GT -> theme.normal
                    com.naulian.glow_core.Type.KEYWORD -> theme.keyword
                    com.naulian.glow_core.Type.PROPERTY -> theme.property
                    com.naulian.glow_core.Type.CLASS -> theme.keyword
                    com.naulian.glow_core.Type.FUNCTION -> theme.keyword
                    com.naulian.glow_core.Type.FUNC_NAME -> theme.method
                    com.naulian.glow_core.Type.FUNC_CALL -> theme.method
                    com.naulian.glow_core.Type.NUMBER -> theme.number
                    com.naulian.glow_core.Type.VALUE_INT -> theme.number
                    com.naulian.glow_core.Type.VALUE_LONG -> theme.number
                    com.naulian.glow_core.Type.VALUE_FLOAT -> theme.number
                    com.naulian.glow_core.Type.STRING -> theme.string
                    com.naulian.glow_core.Type.ASSIGNMENT -> theme.normal
                    com.naulian.glow_core.Type.COMMENT_SINGLE -> theme.comment
                    else -> theme.normal
                }.toComposeColor()

                val hlCode = it.value
                withStyle(style = spanStyle(color = hlColor)) {
                    append(hlCode)
                }
            }
        }

        return HighLight(builder, builder.toString())
    }

    private fun hlKotlin(theme: Theme = Theme()): HighLight {
        val builder = buildAnnotatedString {
            tokens.forEach {
                val hlColor = when (it.type) {
                    com.naulian.glow_core.Type.LT, com.naulian.glow_core.Type.GT -> theme.normal
                    com.naulian.glow_core.Type.KEYWORD -> theme.keyword
                    com.naulian.glow_core.Type.VARIABLE -> theme.keyword
                    com.naulian.glow_core.Type.VAR_NAME -> theme.variable
                    com.naulian.glow_core.Type.CLASS -> theme.keyword
                    com.naulian.glow_core.Type.FUNCTION -> theme.keyword
                    com.naulian.glow_core.Type.FUNC_NAME -> theme.method
                    com.naulian.glow_core.Type.FUNC_CALL -> theme.method
                    com.naulian.glow_core.Type.NUMBER -> theme.number
                    com.naulian.glow_core.Type.VALUE_INT -> theme.number
                    com.naulian.glow_core.Type.VALUE_LONG -> theme.number
                    com.naulian.glow_core.Type.VALUE_FLOAT -> theme.number
                    com.naulian.glow_core.Type.CHAR -> theme.string
                    com.naulian.glow_core.Type.PROPERTY -> theme.property
                    com.naulian.glow_core.Type.STRING -> theme.string
                    com.naulian.glow_core.Type.STRING_BRACE -> theme.keyword
                    com.naulian.glow_core.Type.INTERPOLATION -> theme.property
                    com.naulian.glow_core.Type.ASSIGNMENT -> theme.normal
                    com.naulian.glow_core.Type.ESCAPE -> theme.keyword
                    com.naulian.glow_core.Type.COMMENT_MULTI -> theme.comment
                    com.naulian.glow_core.Type.COMMENT_SINGLE -> theme.comment
                    else -> theme.normal
                }.toComposeColor()

                val hlCode = it.value
                withStyle(style = spanStyle(color = hlColor)) {
                    append(hlCode)
                }
            }
        }
        return HighLight(builder, builder.toString())
    }

    private fun tokenize(source: String, language: String) {
        _tokens = when (language.lowercase()) {
            "java" -> JTokens.tokenize(source)
            "python", "py" -> PTokens.tokenize(source)
            "kotlin", "kt" -> tokenizeKt(source)
            "javascript", "js" -> JsTokens.tokenize(source)
            else -> TxtTokens.tokenize(source)
        }
    }
}

