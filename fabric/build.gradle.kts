val modId: String by project
val minecraftVersion: String = libs.versions.minecraft.get()
val fabricApiVersion: String = libs.versions.fabric.api.get()
val architecturyVersion: String = libs.versions.architectury.get()
val forgeConfigPortVersion: String = libs.versions.fabric.forgeConfigPort.get()

val gameTestRuntime: Configuration by configurations.creating
val gameTestResultsDir = layout.buildDirectory.dir("test-results/gameTest")
val devOnlyMods: Configuration by configurations.creating
val devOnlyModNames = provider { devOnlyMods.resolvedConfiguration.resolvedArtifacts.map { it.moduleVersion.id.name } }

fabricApi {
    configureTests {
        createSourceSet = false
        modId = "scannable_gametest"
        enableGameTests = true
        enableClientGameTests = false
        eula = true
        clearRunDirectory = true
    }
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    runs {
        named("client") { runDir = "run/client" }
        named("server") { runDir = "run/server" }

        create("data") {
            client()
            name("Data Generation")
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file("src/generated/resources")}")
            vmArg("-Dfabric-api.datagen.modid=${modId}")
            vmArg("-Dfabric-api.datagen.strict-validation")

            runDir("build/datagen")
        }
        named("gameTest") {
            property(
                "fabric-api.gametest.report-file",
                gameTestResultsDir.get().file("fabric-game-tests.xml").asFile.absolutePath
            )
        }
    }
}

repositories {
    exclusiveContent {
        forRepository { maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") }
        filter { includeGroup("fuzs.forgeconfigapiport") }
    }
}

configurations.named("modRuntimeOnly") { extendsFrom(devOnlyMods) }

dependencies {
    modImplementation(libs.fabric.loader)
    modApi(libs.fabric.api)
    modApi(libs.fabric.architectury)

    modImplementation(libs.fabric.forgeConfigPort)
    include(modApi(libs.fabric.energy.get().toString()) {
        exclude(group = "net.fabricmc.fabric-api")
    })

    // Not used by mod, just for dev convenience.
    devOnlyMods(libs.jei.fabric)

    gameTestRuntime(project(path = ":gametest-fabric", configuration = "namedElements")) { isTransitive = false }
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

val cleanGameTestResults = tasks.register<Delete>("cleanGameTestResults") {
    description = "Deletes game test results and the scratch world from previous runs."
    delete(gameTestResultsDir)
    delete(layout.buildDirectory.dir("run/gameTest/world"))
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
    classpath = classpath.filter { file -> devOnlyModNames.get().none { file.name.startsWith("${it}-") } }
    finalizedBy(fixGameTestReport)
}

tasks.named("test") {
    setDependsOn(dependsOn.filterNot { "runGameTest" in it.toString() })
}
