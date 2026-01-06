pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
    }

    plugins {
        id("fabric-loom") version "1.14-SNAPSHOT"
    }
}

import java.io.File
import java.io.FileInputStream
import java.util.Properties

fun parseVersion(version: String): Triple<Int, Int, Int> {
    val parts = version.split(".")
    val major = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0
    return Triple(major, minor, patch)
}

fun compareSemver(a: String, b: String): Int {
    val (am, amin, ap) = parseVersion(a)
    val (bm, bmin, bp) = parseVersion(b)
    return when {
        am != bm -> am.compareTo(bm)
        amin != bmin -> amin.compareTo(bmin)
        else -> ap.compareTo(bp)
    }
}

val versionsDir = File(rootDir, "versions")
val mcVers = versionsDir.listFiles()
    ?.filter { it.isDirectory }
    ?.map { it.name }
    ?.sortedWith(::compareSemver)
    ?.toMutableList()
    ?: mutableListOf()

require(mcVers.isNotEmpty()) { "No versions found in ./versions" }

val requestedMcVer = providers.gradleProperty("mcVer").orNull
val defaultMcVer = mcVers.last()
val mcVersion = if (requestedMcVer != null && mcVers.contains(requestedMcVer)) {
    requestedMcVer
} else {
    if (requestedMcVer != null) {
        println("Invalid mcVer '$requestedMcVer'; falling back to $defaultMcVer.")
    }
    defaultMcVer
}

val mcIndex = mcVers.indexOf(mcVersion)
println("Available MC versions: $mcVers")
println("Using MC version: $mcVersion")

val versionProps = Properties()
FileInputStream(File(rootDir, "versions/$mcVersion/gradle.properties")).use { stream ->
    versionProps.load(stream)
}

versionProps.forEach { (key, value) ->
    gradle.extra.set(key.toString(), value)
}
gradle.extra.set("minecraft_version", mcVersion)
gradle.extra.set("mcVers", mcVers)
gradle.extra.set("mcIndex", mcIndex)

rootProject.name = "catpuccin-addon"
