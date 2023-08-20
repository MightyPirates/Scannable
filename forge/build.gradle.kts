val modId: String by project
val minecraftVersion: String = libs.versions.minecraft.get()
val forgeVersion: String = libs.versions.forge.platform.get().split("-")[1]
val architecturyVersion: String = libs.versions.architectury.get()

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    forge {
        convertAccessWideners.set(true)
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)

        mixinConfig("${modId}-common.mixins.json")
        mixinConfig("${modId}.mixins.json")
    }

    runs {
        create("data") {
            data()
            programArgs("--existing", project(":common").file("src/main/resources").absolutePath)
            programArgs("--existing", file("src/main/resources").absolutePath)
        }
    }
}

dependencies {
    forge(libs.forge.platform)
    modApi(libs.forge.architectury)

    // Not used by mod, just for dev convenience.
    modRuntimeOnly(libs.forge.justEnoughItems)
}

tasks {
    processResources {
        val properties = mapOf(
            "version" to project.version,
            "minecraftVersion" to minecraftVersion,
            "loaderVersion" to forgeVersion.split(".").first(),
            "forgeVersion" to forgeVersion,
            "architecturyVersion" to architecturyVersion
        )
        inputs.properties(properties)
        filesMatching("META-INF/mods.toml") {
            expand(properties)
        }
    }
}
