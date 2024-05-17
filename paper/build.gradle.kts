plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version("8.1.1")
    id("io.papermc.paperweight.userdev") version("1.5.11")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(project(":ravioli-commands-core"))

    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}

publishing {
    publications {
        register<MavenPublication>("devBundle") {
            artifact(tasks.reobfJar) {
                artifactId = "ravioli-commands-paper"
                classifier = ""
            }
        }
    }
}