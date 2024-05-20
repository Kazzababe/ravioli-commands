plugins {
    `java-library`
    `maven-publish`
}

repositories {
    maven("https://libraries.minecraft.net")
}

dependencies {
    api(project(":ravioli-commands-core"))

    api("com.mojang:brigadier:1.0.18")
    api("net.kyori:adventure-text-minimessage:4.17.0")
    api("net.kyori:adventure-api:4.17.0")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}