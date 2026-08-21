val modId: String by project
val minecraftVersion: String = libs.versions.minecraft.get()
val fabricApiVersion: String = libs.versions.fabric.api.get()
val architecturyVersion: String = libs.versions.architectury.get()
val forgeConfigPortVersion: String = libs.versions.fabric.forgeConfigPort.get()

val gameTestRuntime: Configuration by configurations.creating
val gameTestResultsDir = layout.buildDirectory.dir("test-results/gameTest")

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    runs {
        create("gameTest") {
            server()
            runDir = "run/gametest"
            vmArg("-Dfabric-api.gametest")
            vmArg("-Dfabric-api.gametest.report-file=${gameTestResultsDir.get().asFile.absolutePath}/fabric-game-tests.xml")
            vmArg("-ea")
        }

    }
}

repositories {
    exclusiveContent {
        forRepository { maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") }
        filter { includeGroup("fuzs.forgeconfigapiport") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.shedaniel.me") }
        filter { includeGroup("me.shedaniel") }
    }
}

dependencies {
    modImplementation(libs.fabric.loader)
    modApi(libs.fabric.api)
    modApi(libs.fabric.architectury)

    modImplementation(libs.fabric.forgeConfigPort)
    include(modApi(libs.fabric.energy.get().toString()) {
        exclude(group = "net.fabricmc.fabric-api")
    })

    // Not used by mod, just for dev convenience.
    modRuntimeOnly(libs.fabric.tooltipFix)
    modRuntimeOnly(libs.fabric.roughlyEnoughItems) {
        exclude(group = "net.fabricmc.fabric-api")
    }

    gameTestRuntime(project(path = ":gametest-fabric", configuration = "namedElements")) { isTransitive = false }
}

val cleanGameTestResults = tasks.register<Delete>("cleanGameTestResults") {
    description = "Deletes game test results and the scratch world from previous runs."
    delete(gameTestResultsDir)
    delete(layout.projectDirectory.dir("run/gametest/world"))
}

val fixGameTestReport = tasks.register("fixGameTestReport") {
    val reportFile = gameTestResultsDir.map { it.file("fabric-game-tests.xml") }
    outputs.upToDateWhen { false }
    doLast {
        normalizeGameTestReport(reportFile.get().asFile)
    }
}

tasks.named<JavaExec>("runGameTest") {
    dependsOn(cleanGameTestResults)
    classpath += gameTestRuntime
    finalizedBy(fixGameTestReport)
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
