package com.naulian.glow_core.atx

enum class BaseType {
    VALUE, ARGUMENT, KEYWORD, TEXT, NEWLINE, EOF, OTHER
}

data class AtxGroup(
    val type: AtxContentType,
    val children: List<AtxToken>
)

enum class AtxContentType {
    TEXT, OTHER, TABLE, LINK
}

enum class AtxType {
    HEADER,
    TEXT,
    BOLD,
    ITALIC,
    UNDERLINE,
    STRIKE,
    COLORED,
    QUOTE,
    FUN,
    LINK,
    PICTURE,
    VIDEO,
    YOUTUBE,
    MEDIA,
    TABLE,
    ELEMENT,
    NEWLINE,
    DIVIDER,
    OTHER,
    EOF
}