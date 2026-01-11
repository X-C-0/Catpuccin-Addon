plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.14-SNAPSHOT" apply false
}
stonecutter active "1.21.11" /* [SC] DO NOT EDIT */

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    swaps["mod_version"] = "\"" + property("mod.version") + "\";"
    swaps["minecraft_version"] = "\"" + node.metadata.version + "\";"
    swaps["yarn_mappings"] = "\"" + node.project.property("deps.yarn_mappings") + "\";"
    swaps["fabric_loader"] = "\"" + node.project.property("deps.fabric_loader") + "\";"
    swaps["meteor_version"] = "\"" + node.project.property("deps.meteor_version") + "\";"
}