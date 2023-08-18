<img src="screenshots/javascript.jpg" alt="screenshot" width="240"></img>

# glow 
A simple syntax highlighter to use with TextView. Kotlin, Java, JavaScript, and Python are Supported.
[![](https://jitpack.io/v/cinkhangin/glow.svg)](https://jitpack.io/#cinkhangin/glow)

[Jitpack](https://jitpack.io/#cinkhangin/glow)

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.cinkhangin:glow:0.1.8'
}
```

```kotlin
val sourceCode = "print(\\"hello\\")"
val language = "py" //or "python"

val highlighted = glowSyntax(sourceCode, language, CodeTheme.kotlinLight)
//textView.text = highlighted.raw 
textView.setCodeTheme(CodeTheme.kotlinLight.normal)
textView.text = highlighted.spanned
```
