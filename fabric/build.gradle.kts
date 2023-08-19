import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

apply(plugin = rootProject.libs.plugins.shadow.get().pluginId)

val modId: String by project

architectury {
    platformSetupLoomIde()
    fabric()
}

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

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating

configurations {
    compileClasspath.get().extendsFrom(common)
    runtimeClasspath.get().extendsFrom(common)
    getByName("developmentFabric").extendsFrom(common)
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

    common(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(path = ":common", configuration = "transformProductionFabric")) { isTransitive = false }

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

    withType<ShadowJar> {
        exclude("architectury.common.json")
        configurations = listOf(shadowCommon)
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        injectAccessWidener.set(true)
        val shadowJarTask = getByName<ShadowJar>("shadowJar")
        inputFile.set(shadowJarTask.archiveFile)
        dependsOn(getByName("shadowJar"))
        archiveClassifier.set(null as String?)
    }

    jar {
        archiveClassifier.set("dev")
    }
}

(components["java"] as AdhocComponentWithVariants)
    .withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
        skip()
    }
