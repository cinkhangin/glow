package com.naulian.glow_core.mdx

private val SOURCE = """
    #1 this is heading 1
    #2 this is heading 2
    #3 this is heading 3
    #4 this is heading 4
    #5 this is heading 5
    #6 this is heading 6
   
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
    
    image space bug test
    
    [
    a    |b    |result
    true |true |true  
    true |false|false 
    false|false|false 
    ]
    
    * unordered item
    * unordered item
    
    *o uncheck item
    *x checked item
""".trimIndent()