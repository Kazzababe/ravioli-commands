plugins {
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

repositories {
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
//    implementation(project(":paper"))
    implementation(project(":core"))
    implementation("ravioli.gravioli:ravioli-commands-paper:1.0-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveClassifier.set("")
    }
}