plugins {
    id("fabric-loom")
}

val minecraftVersion = stonecutter.current.version
val yarnMappings = project.property("yarn_mappings") as String
val loaderVersion = project.property("fabric_loader") as String
val modVersion = project.property("mod_version") as String
val mavenGroup = project.property("mod_group") as String
val meteorVersion = project.property("meteor_version") as String

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
}

stonecutter {
    replacements {
        string(current.parsed <= "1.21.8") {
            // Click -> mouseX, mouseY, button
            replace("onMouseClicked(Click click", "onMouseClicked(double mouseX, double mouseY, int button")
            replace("onMouseReleased(Click click", "onMouseReleased(double mouseX, double mouseY, int button")
            replace("mouseReleased(Click click", "mouseReleased(double mouseX, double mouseY, int button")
            // CharInput -> char
            replace("onCharTyped(CharInput input)", "onCharTyped(char input)")
            // KeyInput -> key, mods
            replace("onKeyRepeated(KeyInput input)", "onKeyRepeated(int key, int mods)")
        }
        string(current.parsed <= "1.21.4") {
            // String utils
            replace("org.apache.commons.lang3.Strings", "org.apache.commons.lang3.StringUtils")
            replace("Strings.CI.contains", "StringUtils.containsIgnoreCase")
        }
    }
}

tasks {
    processResources {
        val propertyMap = mapOf(
            "version" to project.property("mod.version"),
            "mc_targets" to project.property("mod.mc_targets")
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
        options.encoding = "UTF-8"
        options.release = 21
        options.compilerArgs.add("-Xlint:deprecation")
        options.compilerArgs.add("-Xlint:unchecked")
    }
}