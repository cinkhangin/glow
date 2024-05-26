package com.naulian.glow_core.atx

val SAMPLE = """
    @w this is heading 1
    @x this is heading 2
    @y this is heading 3
    @z this is heading 4
    
    @n
    
    this is @b bold @b text
    @g #00FF00 green text @g
    this is @i italic @i text
    this is @u underline @u text
    this is @s strikethrough @s text
    this is @c code @c text @j
    this is @m italic bold @m text
    
    @f comment
    j -> join (replace newline character with space)
    m -> for bold italic (aka mark)
    g -> for hex colored text (default is gray)
    k -> k is used for constant
         there are multiple types in constant
         example
         @k millis will be replaced with the current millis
         @k data will be replaced with the current date
         @k time will be replaced with the current time
         @k random will be replaced with a random number
    @f
    
    @n
    
    this is a @g #FF0000 colored @g text
    
    @n
    
    @q 
    this is quote text
    - naulian
    @q
    
    @n
    
    @f kotlin
    fun main(varargs args: String) {
        println("Hello World!")
    }
    @f
    
    @f python
    def main():
        print("Hello World!")
        
    
    if __name__ == '__main__':
        main()
    @f
    
    @n
   
    @h Google @a http://www.google.com
    
    @n
    
    @p https://picsum.photos/id/67/300/200
    @v https://www.youtube.com/watch?v=dQw4w9WgXcQ
    
    @n
    
    @z Logical And
    @n
    
    @t a, b, result
    @r 
    true, true, true, 
    true, false, false,
    false, false, false
    
    @n
    
    @z Logical Or
    @n
    
    @t a, b, result
    @r 
    true, true, true, 
    true, false, true,
    false, false, false
    
    @d -
    
    @l Primitive Types
    Char 1 Byte,
    Short 2 Bytes,
    Int 4 Bytes,
    Long 8 Bytes
    
    @n
    
    @e unordered element 1
    @e unordered element 2
    @o ordered element 1
    @o ordered element 2
""".trimIndent()