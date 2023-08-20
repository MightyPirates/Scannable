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
    maven { url = uri("https://maven.shedaniel.me") }
    maven { url = uri("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") }
    mavenCentral()
}

dependencies {
    modImplementation(libs.fabricLoader)
    modApi(libs.fabricApi)
    modApi(libs.architecturyFabric)

    include(modApi("teamreborn:energy:3.0.0") {
        exclude(group = "net.fabricmc.fabric-api")
    })

    modImplementation(libs.forgeConfigPort)

    // Not used by mod, just for dev convenience.
    modRuntimeOnly("curse.maven:tooltipfix-411557:4577194")
    modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-fabric:12.0.645") {
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
