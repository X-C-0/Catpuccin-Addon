plugins {
    id("maven-publish")
    id("dev.kikugie.loom-back-compat")
}

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    else -> JavaVersion.VERSION_1_8
}

// Global properties
val mavenGroup = property("mod.group") as String
val modVersion = property("mod.version") as String
val modId = property("mod.id") as String

// Per version properties
val minecraftVersion = stonecutter.current.version
val minecraftTargets = sc.properties["mc.targets"] as String

val loaderVersion = sc.properties["deps.fabric_loader"] as String
val meteorVersion = sc.properties["deps.meteor_version"] as String

base {
    archivesName = modId
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
    minecraft("com.mojang:minecraft:$minecraftVersion")

    // Mappings
    loomx.applyMojangMappings()

    // Fabric
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

    // Meteor
    modImplementation("meteordevelopment:meteor-client:$meteorVersion")
}

tasks {
    processResources {
        val propertyMap = mapOf(
            "version" to minecraftVersion,
            "mc_targets" to minecraftTargets
        )

        inputs.properties(propertyMap)

        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand(propertyMap)
        }
    }

    // Builds just the API into a standalone jar
    val apiJar = register<Jar>("buildApi") {
        group = "build"
        archiveClassifier.set("api")
        from(sourceSets["main"].output)

        include("me/pindour/catppuccin/api/**")
    }

    // Builds the sources for the API
    val apiSourcesJar = register<Jar>("buildApiSources") {
        group = "build"
        archiveClassifier.set("api-sources")
        from(sourceSets["main"].allSource)
        include("me/pindour/catppuccin/api/**")
    }

    // Builds the version into a shared folder in `build/libs/${mod version}/`
    val buildAndCollect = register<Copy>("buildAndCollect") {
        group = "build"

        // loomx.mod(Sources)Jar returns the jar task for the applied loom variant
        from(loomx.modJar.map { it.archiveFile }, loomx.modSourcesJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }

    if (stonecutter.current.isActive) {
        register("buildActive") {
            group = "project"
            dependsOn(buildAndCollect)
        }

        register("runActive") {
            group = "project"
            dependsOn(named("runClient"))
        }
    }

    jar {
        inputs.property("archivesName", project.base.archivesName.get())

        from("LICENSE") {
            rename { "${it}_${inputs.properties["archivesName"]}" }
        }
    }

    java {
        withSourcesJar()
        targetCompatibility = requiredJava
        sourceCompatibility = requiredJava

        toolchain {
            vendor = JvmVendorSpec.ADOPTIUM
            languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = requiredJava.majorVersion.toInt()
        options.compilerArgs.add("-Xlint:deprecation")
        options.compilerArgs.add("-Xlint:unchecked")
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                artifact(apiJar)
                artifact(apiSourcesJar)
            }
        }
    }
}