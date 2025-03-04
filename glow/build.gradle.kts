import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.vanniktech.maven.publish")
}

android {
    compileSdk = 35
    namespace = "com.naulian.glow"

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.naulian.anhance)
    implementation(project(":glow-core"))
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)

    signAllPublications()

    coordinates(
        groupId = "com.naulian",
        artifactId = "glow",
        version = "1.8.0-alpha01"
    )
    //./gradlew publishAndReleaseToMavenCentral --no-configuration-cache

    pom {
        name.set("Glow")
        description.set("A simple syntax highlighter to use with TextView. Kotlin, Java, JavaScript, and Python are Supported.")
        inceptionYear.set("2023")
        url.set("https://github.com/cinkhangin/glow/")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("naulian")
                name.set("Naulian")
                url.set("https://github.com/cinkhangin/")
            }
        }
        scm {
            url.set("https://github.com/cinkhangin/glow/")
            connection.set("scm:git:git://github.com/cinkhangin/glow.git")
            developerConnection.set("scm:git:ssh://git@github.com/cinkhangin/glow.git")
        }
    }
}