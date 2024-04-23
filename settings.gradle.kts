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
include("paper")
