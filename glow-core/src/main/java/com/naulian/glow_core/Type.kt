package com.naulian.glow_core

enum class Type {
    NONE,

    //White space
    NEWLINE,
    SPACE,

    WORD,

    //Operator
    GT,
    LT,

    //PUNCTUATION
    AT,
    DOT,
    SINGLE_QUOTE,
    DOUBLE_QUOTE,

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
    QUESTION_MARK,
    POW,
    AND,
    OR,
    ESCAPE,
    PARAM,
    ASTERISK,
    ARGUMENT,
    WHITE_SPACE,
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
    LEFT_BRACE,
    RIGHT_BRACE,
    LEFT_PARENTHESES,
    RIGHT_PARENTHESES,
    LEFT_BRACKET,
    RIGHT_BRACKET,
    COMMENT_SINGLE,
    COMMENT_MULTI,
    FORWARD_SLASH,
    BACK_SLASH,
    INTERPOLATION,
    STRING_BRACE
}