val modId: String by project

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
    modImplementation(libs.fabric.loader)
    modApi(libs.fabric.api)
    modApi(libs.architectury.fabric)

    include(modApi("teamreborn:energy:3.0.0") {
        exclude(group = "net.fabricmc.fabric-api")
    })

    modImplementation("fuzs.forgeconfigapiport:forgeconfigapiport-fabric:8.0.0")

    // Not used by mod, just for dev convenience.
    modRuntimeOnly("curse.maven:tooltipfix-411557:4577194")
    modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-fabric:12.0.645") {
        exclude(group = "net.fabricmc.fabric-api")
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(mapOf("version" to project.version))
        }
    }

    remapJar {
        injectAccessWidener.set(true)
    }
}
