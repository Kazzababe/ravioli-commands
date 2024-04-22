plugins {
    `maven-publish`
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":core"))

    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
}


publishing {
    publications {
        register<MavenPublication>("shadow") {
            project.shadow.component(this)

            artifactId = "ravioli-commands-paper"
        }
    }
}