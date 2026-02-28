plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.14-SNAPSHOT" apply false
}
stonecutter active "1.21.11" /* [SC] DO NOT EDIT */

tasks.register("buildAllAndCollect") {
    group = "build"
    description = "Builds all Stonecutter versions and collects the mod jars into build/libs/<modVersion>/"
    stonecutter.versions.forEach { ver ->
        dependsOn(":${ver.version}:buildAndCollect")
    }
}
