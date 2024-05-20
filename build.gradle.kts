plugins {
    java
}

allprojects {
    apply(plugin = "java")

    group = "ravioli.gravioli"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:24.0.1")
        compileOnly("org.projectlombok:lombok:1.18.30")

        annotationProcessor("org.projectlombok:lombok:1.18.30")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
}