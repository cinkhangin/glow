package com.naulian.glow_core.atx

enum class AtxType(val code: Char, val kind: AtxKind = AtxKind.TEXT) {
    HEADER('w', AtxKind.OTHER),
    SUB_HEADER('x', AtxKind.OTHER),
    TITLE('y', AtxKind.OTHER),
    SUB_TITLE('z', AtxKind.OTHER),

    BOLD('b', AtxKind.TEXT),
    ITALIC('i', AtxKind.TEXT),
    BOLD_ITALIC('m', AtxKind.TEXT),
    UNDERLINE('u', AtxKind.TEXT),
    STRIKE('s', AtxKind.TEXT),

    QUOTE('q', AtxKind.OTHER),

    CODE('f', AtxKind.OTHER),
    CODE_BLOCK('c', AtxKind.TEXT),

    LINK('a', AtxKind.LINK),
    HYPER('h', AtxKind.LINK),

    PICTURE('p', AtxKind.OTHER),
    VIDEO('v', AtxKind.OTHER),

    TEXT(' ', AtxKind.TEXT),
    COLORED('g', AtxKind.TEXT),

    LIST('l', AtxKind.OTHER),
    TABLE('t', AtxKind.TABLE),
    ROW('r', AtxKind.TABLE),

    ELEMENT('e', AtxKind.OTHER),
    ORDERED_ELEMENT('o', AtxKind.OTHER),

    JOIN('j', AtxKind.TEXT),
    DIVIDER('d', AtxKind.OTHER),
    NEWLINE('n', AtxKind.SPACE),

    CONSTANT('k', AtxKind.OTHER),
    END(Char.MIN_VALUE, AtxKind.OTHER)
}