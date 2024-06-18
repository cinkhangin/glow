package com.naulian.glow_core.mdx

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val mdxNl = "mdx.nl"
const val mdxBr = "mdx.br"
fun CharSequence.str(): String = toString().trim()
fun CharSequence.str(textToReplace: String) = str().replace(textToReplace, "")

data class MdxComponentGroup(
    val type: MdxComponentType,
    val children: List<MdxToken>,
)

enum class MdxComponentType {
    TEXT, OTHER,
}


const val dollarSign = "$"
const val mdxMillis = "mdx.millis"
const val mdxSecond = "mdx.second"
const val mdxMinute = "mdx.minute"
const val mdxHour = "mdx.hour"
const val mdxToday = "mdx.day"
const val mdxMonth = "mdx.month"
const val mdxYear = "mdx.year"
const val mdxDate = "mdx.date"
const val mdxDateTime = "mdx.datetime"
const val mdxTime = "mdx.time"

val currentMillis = System.currentTimeMillis()

val mdxAdhocMap = hashMapOf(
    mdxMillis to currentMillis.toString(),
    mdxSecond to formattedDateTime("ss"),
    mdxMinute to formattedDateTime("mm"),
    mdxHour to formattedDateTime("hh"),
    mdxToday to formattedDateTime("dd"),
    mdxMonth to formattedDateTime("MM"),
    mdxYear to formattedDateTime("yyyy"),
    mdxDate to formattedDateTime("dd/MM/yyyy"),
    mdxDateTime to formattedDateTime("dd/MM/yyyy hh:mm:ss a"),
    mdxTime to formattedDateTime("hh:mm:ss a")
)

val MDX_TEST = """
    #1 heading
    * item
    
    some text
    
    (img@https://picsum.photos/id/67/300/200)
    
    some more text
""".trimIndent()

fun List<MdxComponentGroup>.getFormattedString(): String {
    val strBuilder = StringBuilder()
    forEach { group ->
        when (group.type) {
            MdxComponentType.TEXT -> {
                group.children.forEach { token ->
                    when (token.type) {
                        MdxType.COLORED -> {
                            val (value, hexColor) = token.getTextColorPair()
                            strBuilder.append("$value: $hexColor")
                        }

                        MdxType.LINK,
                        MdxType.HYPER_LINK -> {
                            val (hyper, link) = token.getHyperLink()
                            strBuilder.append("$hyper: $link")
                        }

                        MdxType.ELEMENT -> {
                            when {
                                token.text.startsWith("o ") -> {
                                    val text = token.text.replace("o ", "")
                                    strBuilder.append("\u2610 $text")
                                }

                                token.text.startsWith("x ") -> {
                                    val text = token.text.replace("x ", "")
                                    strBuilder.append("\u2611 $text")
                                }

                                else -> strBuilder.append("\u25CF ${token.text}")
                            }
                        }

                        else -> strBuilder.append(token.text)
                    }
                }
                strBuilder.append("\n")
            }

            MdxComponentType.OTHER -> {
                group.children.forEach { token ->
                    when (token.type) {
                        MdxType.CODE -> {
                            val (lang, code) = token.getLangCodePair()
                            strBuilder.append("$lang: $code")
                        }

                        MdxType.DIVIDER -> {
                            strBuilder.append("---")
                        }

                        MdxType.TABLE -> {
                            val rows = token.getTableItemPairs()
                            strBuilder.append(rows)
                        }

                        else -> strBuilder.append(token.text)
                    }

                    strBuilder.append("\n")
                }
            }
        }
    }

    return strBuilder.toString()
}

fun formattedDateTime(pattern: String): String {
    val localDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return localDateTime.format(formatter)
}

fun main() {
    val text = MdxParser.parse(MDX_SAMPLE).getFormattedString()
    println(text)
}