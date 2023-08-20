val modId: String by project
val minecraftVersion: String = libs.versions.minecraft.get()
val fabricApiVersion: String = libs.versions.fabricApi.get()
val architecturyVersion: String = libs.versions.architectury.get()
val forgeConfigPortVersion: String = libs.versions.forgeConfigPort.get()

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    runs {
        create("data") {
            client()
            name("Data Generation")
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file("src/generated/resources")}")
            vmArg("-Dfabric-api.datagen.modid=${modId}")
            vmArg("-Dfabric-api.datagen.strict-validation")

            runDir("build/datagen")
        }
    }
}

repositories {
    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") {
        content { includeGroup("fuzs.forgeconfigapiport") }
    }
    maven("https://maven.shedaniel.me") {
        content { includeGroup("me.shedaniel") }
    }
}

dependencies {
    modImplementation(libs.fabricLoader)
    modApi(libs.fabricApi)
    modApi(libs.architecturyFabric)

    modImplementation(libs.forgeConfigPort)
    include(modApi(libs.fabricEnergy.get().toString()) {
        exclude(group = "net.fabricmc.fabric-api")
    })

    // Not used by mod, just for dev convenience.
    modRuntimeOnly(libs.tooltipFix)
    modRuntimeOnly(libs.roughlyEnoughItems) {
        exclude(group = "net.fabricmc.fabric-api")
    }
}

tasks {
    processResources {
        val properties = mapOf(
            "version" to project.version,
            "minecraftVersion" to minecraftVersion,
            "fabricApiVersion" to fabricApiVersion,
            "architecturyVersion" to architecturyVersion,
            "forgeConfigPortVersion" to forgeConfigPortVersion
        )
        inputs.properties(properties)
        filesMatching("fabric.mod.json") {
            expand(properties)
        }
    }

    remapJar {
        injectAccessWidener.set(true)
    }
}
