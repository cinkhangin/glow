package com.naulian.glow_core

enum class Type {
    NONE,

    //White space
    NEWLINE,

    WORD,

    //Operator
    GT,
    LT,

    //PUNCTUATION
    AT,
    DOT,
    SQUOTE,
    DQUOTE,

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
    QMARK,
    POW,
    AND,
    OR,
    ESCAPE,
    PARAM,
    ASTERISK,
    ARGUMENT,
    SPACE,
    ASSIGNMENT,
    PROPERTY,
    CLASS,
    COMMA,
    COLON,
    STRING,
    NUMBER,
    ILLEGAL,
    KEYWORD,
    FUNCTION,
    VAR_NAME,
    SEMICOLON,
    FUNC_NAME,
    FUNC_CALL,

    VALUE_INT,
    VALUE_LONG,
    VALUE_FLOAT,

    //    FUNC_CALL,
    DATA_TYPE,
    CLASS_NAME,
    IDENTIFIER,
    LBRACE,
    RBRACE,
    LPAREN,
    RPAREN,
    LBRACK,
    RBRACKET,
    SCOMMENT,
    MCOMMENT,
    FSLASH,
    BSLASH,
    INTERPOLATION,
    STRING_BRACE
}