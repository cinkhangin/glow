package com.naulian.glow_core

import com.naulian.glow_core.mdx.MdxParser
import org.junit.Assert.assertEquals
import org.junit.Test

private val laelar_about = """
    #4 Course အကြောင်း
    
    "
    တစ်ကြိမ်အလုပ်လုပ်ပြီး တစ်သက်သာအတွက် passive income ဖန်တီးလိုက်ပါ။
    - Laelar
    "
    
    #6 Course ဖန်တီးခြင်း
    
    Course တစ်ခုဖန်တီးရန်အတွက် account verified ဖြစ်ဖို့လိုအပ်မည်ဖြစ်သည်။ ~
    ဖန်တီးထားသော course များကိုအသုံးပြုသူများတွေ့နိုင်ရန်အတွက် publish လုပ်ရမည်ဖြစ်သည်။ ~
    publish လုပ်ပြီး course ကို Laelar ဘက်မှ appoved ပြီးမှ အသုံးပြုသူများတွေ့နိုင်ပါမည်။
    
    #5 Verification ဘယ်ကရမလဲ?
    
    Account verification ကို Laelar )Facebook Page မှရယူနိုင်သည်။ ~
    Verify လုပ်ရာတွင် Verification fee ($10), payment method, contact number ~
    စသည့်အချက်အလက်များလိုအပ်မည်ဖြစ်သည်။
    
    #5 Course ရောင်းခြင်း
    
    Course များ approved ဖြစ်ပြီးပါက ရောင်းဝယ်ယူမှုအားလုံးကို ~
    Laelar မှပြုလုပ်သွားမည်ဖြစ်သည်။ ~
    threshold 100,000 mmk ကျော်ပါက Laelar ဘက်မှ 30\% ကိုနှုတ်ပြီး payment ပေးမည်ဖြစ်သည်။
    
    #5 Course ပုံစံ
    Course တစ်ခုကို Chapter(အခန်း) များဖြင့်ဖွဲ့စည်းထားသည်။ Chapter သည် Block ~
    များဖြင့်ဖွဲ့ထား screen ဖြစ်သည်။ ယခုမြင်နေရသော ~
    screen သည် block များဖြင့်ပေါင်းထားသော Chapter တစ်ခုဖြစ်သည်။
    
    Block Example ကိုအောက်မှာပြထားသည်
""".trimIndent().replace(" ~\n", " ")

class CrashTest {
    @Test
    fun crashTest() {
        val source = laelar_about
        var isNotCrash = true
        try {
            MdxParser(source).parse().children
        } catch (e: Exception) {
            isNotCrash = false
        }
        assertEquals(true, isNotCrash)
    }
}