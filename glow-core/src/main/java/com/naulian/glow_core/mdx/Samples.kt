package com.naulian.glow_core.mdx


val MDX_SAMPLE = """
    #3 Mdx Example
    
    =line=
    
    #5 Lorem Ipsum 1
    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut~
    labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco~
    laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in~
    voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat~
    non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
    
    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut~
    labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco~
    laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in~
    voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat~
    non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
    
    =br=
    #5 Lorem Ipsum 2
    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut~
    labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco~
    laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in~
    voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat~
    non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
    
    Lorem ipsum dolor sit amet
""".trimIndent().replace("~\n", " ")


val MDX_TEST = """
    #1 heading 1
    #2 heading 2
    #3 heading 3
    #4 heading 4
    #5 heading 5
    #6 heading 6
   
    =line=
    
    `ignore ~syntax~ here`
    
    Lorem ipsum dolor sit amet, consectetur adipiscing elit. 
    
    Lorem ipsum dolor sit amet, consectetur adipiscing elit.
    
    <color this text#FF0000>
    
    this is &bold& text
    this is /italic/ text
    this is _underline_ text
    this is ~strikethrough~ text.
    date: %dd/MM/yyyy%
    
    Current time : mdx.time
    
    "this is quote text -author"
    
    {
    .kt
    fun main(varargs args: String) {
        println("Hello World!")
        val millis = System.currentTimeMillis()
        println("Current time in millis: ${dollarSign}millis")
        // output : mdx.millis
    }
    }
    
    \"this should not show quote\"
    
    {
    .py
    def main():
        print("Hello World!")
        
    if __name__ == '__main__':
        main()
    }
    
    Search (here@http://www.google.com) for anything.
    
    (img@https://picsum.photos/id/67/300/200)
    (ytb@https://www.youtube.com/watch?v=dQw4w9WgXcQ)
    
    [
    a    |b    |result
    true |true |true  
    true |false|false 
    &false&|false|false 
    ]
    
    * unordered item
    * unordered item
    
    *o uncheck item
    *x checked item
""".trimIndent()


private val AST = """
    root
    |    h1
    |    |    whitespace
    |    |    text
    |    newline
    |    h2
    |    |    whitespace
    |    |    text
    |    newline
    |    h3
    |    |    whitespace
    |    |    text
    |    newline
    |    h4
    |    |    whitespace
    |    |    text
    |    newline
    |    h5
    |    |    whitespace
    |    |    text
    |    newline
    |    h6
    |    |    whitespace
    |    |    text
    |    newline
    |    divider
    |    newline
    |    ignore
    |    newline
    |    text
    |    newline
    |    text
    |    newline
    |    colored
    |    |    text
    |    |    color_hex
    |    newline
    |    text
    |    bold
    |    |    text
    |    whitespace
    |    text
    |    newline
    |    text
    |    italic
    |    |    text
    |    whitespace
    |    text
    |    newline
    |    text
    |    underline
    |    |    text
    |    whitespace
    |    text
    |    newline
    |    text
    |    strike
    |    |    text
    |    whitespace
    |    text
    |    newline
    |    text
    |    datetime
    |    newline
    |    text
    |    newline
    |    quotation
    |    |    text
    |    newline
    |    code
    |    newline
    |    escape
    |    text
    |    escape
    |    newline
    |    code
    |    newline
    |    text
    |    hyper_link
    |    whitespace
    |    text
    |    newline
    |    image
    |    newline
    |    youtube
    |    newline
    |    table
    |    |    newline
    |    |    text
    |    |    pipe
    |    |    text
    |    |    pipe
    |    |    text
    |    |    newline
    |    |    text
    |    |    pipe
    |    |    text
    |    |    pipe
    |    |    text
    |    |    newline
    |    |    text
    |    |    pipe
    |    |    text
    |    |    pipe
    |    |    text
    |    |    newline
    |    |    text
    |    |    pipe
    |    |    text
    |    |    pipe
    |    |    text
    |    |    newline
    |    newline
    |    element_bullet
    |    |    whitespace
    |    |    text
    |    newline
    |    element_bullet
    |    |    whitespace
    |    |    text
    |    newline
    |    element_unchecked
    |    |    whitespace
    |    |    text
    |    newline
    |    element_checked
    |    |    whitespace
    |    |    text
""".trimIndent()