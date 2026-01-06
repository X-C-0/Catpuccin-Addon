plugins {
    id("fabric-loom")
    id("systems.manifold.manifold-gradle-plugin") version "0.0.2-alpha"
}

val minecraftVersion = gradle.extra["minecraft_version"] as String
val yarnMappings = gradle.extra["yarn_mappings"] as String
val loaderVersion = project.property("fabric_loader") as String
val modVersion = project.property("mod_version") as String
val mavenGroup = project.property("mod_group") as String
val meteorVersion = gradle.extra["meteor_version"] as String
val manifoldVersion = project.property("manifold_version") as String

@Suppress("UNCHECKED_CAST")
val mcVers = gradle.extra["mcVers"] as List<String>
val mcIndex = gradle.extra["mcIndex"] as Int

fun versionToInt(version: String): Int {
    val parts = version.split(".")
    val major = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0
    return major * 10000 + minor * 100 + patch
}

fun writeBuildProperties() {
    val byMajorMinor = mcVers.groupBy { ver ->
        val parts = ver.split(".")
        val major = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
        Pair(major, minor)
    }

    val macros = mutableSetOf<String>()
    for ((key, versions) in byMajorMinor) {
        val patches = versions.map { ver ->
            ver.split(".").getOrNull(2)?.toIntOrNull() ?: 0
        }
        val minPatch = patches.minOrNull() ?: 0
        val maxPatch = patches.maxOrNull() ?: 0
        for (patch in minPatch..maxPatch) {
            macros.add("${key.first}.${key.second}.${patch}")
        }
    }

    val sb = StringBuilder()
    sb.append("# DON'T TOUCH THIS FILE, This is handled by the build script\n")
    macros.sortedWith { a, b ->
        versionToInt(a).compareTo(versionToInt(b))
    }.forEach { ver ->
        val macroName = "MC_" + ver.replace(".", "_")
        sb.append(macroName).append("=").append(versionToInt(ver)).append("\n")
    }
    sb.append("MC_VER=").append(versionToInt(minecraftVersion)).append("\n")

    file("build.properties").writeText(sb.toString())
}

writeBuildProperties()

base {
    archivesName = project.property("mod_id") as String
    version = "${modVersion}+mc${minecraftVersion}"
    group = mavenGroup
}

repositories {
    maven {
        name = "meteor-maven"
        url = uri("https://maven.meteordev.org/releases")
    }
    maven {
        name = "meteor-maven-snapshots"
        url = uri("https://maven.meteordev.org/snapshots")
    }
}

dependencies {
    // Fabric
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$yarnMappings:v2")
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

    // Meteor
    modImplementation("meteordevelopment:meteor-client:$meteorVersion")

    annotationProcessor("systems.manifold:manifold-preprocessor:$manifoldVersion")
}

manifold {
    manifoldVersion = manifoldVersion
}

tasks {
    processResources {
        val propertyMap = mapOf(
            "version" to project.property("mod_version"),
            "mc_version" to gradle.extra["mod_mc_dep"]
        )

        inputs.properties(propertyMap)

        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand(propertyMap)
        }
    }

    // Builds the version into a shared folder in `build/libs/${mod version}/`
    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar.map { it.archiveFile }, remapSourcesJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/$modVersion"))
        dependsOn("build")
    }

    jar {
        inputs.property("archivesName", project.base.archivesName.get())

        from("LICENSE") {
            rename { "${it}_${inputs.properties["archivesName"]}" }
        }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    withType<JavaCompile> {
        inputs.property("mcVer", minecraftVersion)
        inputs.file(file("build.properties"))

        options.encoding = "UTF-8"
        options.release = 21
        options.compilerArgs.add("-Xlint:deprecation")
        options.compilerArgs.add("-Xlint:unchecked")
    }
}
