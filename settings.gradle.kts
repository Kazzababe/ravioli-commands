rootProject.name = "command-library"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

if (System.getenv("JITPACK").isNullOrBlank()) {
    include("example")
}
include("core")
project(":core").name = "ravioli-commands-core"
include("paper")
project(":paper").name = "ravioli-commands-paper"
