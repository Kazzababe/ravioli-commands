rootProject.name = "command-library"

if (System.getenv("JITPACK").isNullOrBlank()) {
    include("example")
}
include("core")
include("paper")
