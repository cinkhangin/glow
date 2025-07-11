@file:Suppress("unused")

package com.naulian.glow.lexer

enum class Type {
    //BaseTypes
    SPACE,
    SYMBOL,
    NEWLINE,
    STRING,
    NUMBER,

    //Other
    NONE,
    WORD,
    GT,
    LT,
    AT,
    DOT,
    S_QUOTE,
    D_QUOTE,

    VARIABLE,
    EOF, //End of file
    EOL, //End of line
    PLUS,
    CHAR,
    DASH,
    BANG,
    HASH,
    DOLLAR,
    MODULO,
    Q_MARK,
    POW,
    AND,
    OR,
    B_SCAPE,
    PARAM,
    STAR,
    ARGUMENT,
    EQUAL_TO,
    PROPERTY,
    CLASS,
    COMMA,
    COLON,
    ILLEGAL,
    KEYWORD,
    FUNCTION,
    VAR_NAME,
    S_COLON,
    FUNC_NAME,
    FUNC_CALL,

    VALUE_INT,
    VALUE_LONG,
    VALUE_FLOAT,

    //FUNC_CALL,
    DATA_TYPE,
    CLASS_NAME,
    IDENTIFIER,
    L_BRACE,
    R_BRACE,
    L_PAREN,
    R_PAREN,
    L_BRACKET,
    R_BRACKET,
    S_COMMENT,
    M_COMMENT,
    F_SLASH,
    B_SLASH,
    INTERPOLATION,
    STRING_BRACE
}