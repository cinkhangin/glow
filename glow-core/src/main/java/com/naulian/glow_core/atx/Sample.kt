package com.naulian.glow_core.atx

val SAMPLE_ATX = """
    @h(this is heading 1)[1]
    @h(this is heading 2)[2]
    @h(this is heading 3)[3]
    @h(this is heading 4)[4]
    @h(this is heading 5)[5]
    @h(this is heading 6)[6]
    
    this is @b(bold) text @j
    this is @c(colored)[#00FF00] text @n
    this is @i(italic) text @n
    this is @u(underline) text @j
    this is @s(strikethrough) text @n
    
    @f(
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
    )[comment]
    
    @q(
    this is quote text
    )[naulian]
    
    @f(
    fun main(varargs args: String) {
        println("Hello World!")
    }
    )[kotlin]
    
    @f(
    def main():
        print("Hello World!")
        
    
    if __name__ == '__main__':
        main()
    )[python]
   
    @l(http://www.google.com)[Google]
    
    @p(https://picsum.photos/id/67/300/200)
    @y(https://www.youtube.com/watch?v=dQw4w9WgXcQ)
    @m(https://www.youtube.com/watch?v=dQw4w9WgXcQ)
    @v(https://www.youtube.com/watch?v=dQw4w9WgXcQ)
    
    @h(Logical And)[6]
    @t(
    true, true, true, 
    true, false, false,
    false, false, false
    )[a, b, result]
    
    @d(------***------)
    
    @t(
    Char 1 Byte,
    Short 2 Bytes,
    Int 4 Bytes,
    Long 8 Bytes
    )[Primitive Types]
    
    @e(
    unordered element 1
    unordered element 2
    )[*]
    @e(
    ordered element 1
    ordered element 2
    )[1]
    @e(
    alphabetical element 1
    alphabetical element 2
    )[a]
    
    @f(
    these are not used yet
    @a @r @h @o @w @x @z @n @f @g
    )[comment]
""".trimIndent()

val SAMPLE_KT = """
    fun main(args: Array<String>) {
        println("Hello World!")
    }
""".trimIndent()