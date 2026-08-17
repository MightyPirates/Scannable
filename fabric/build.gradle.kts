val modId: String by project
val gameTestResultsDir = layout.buildDirectory.dir("test-results/gameTest")
val minecraftVersion: String = libs.versions.minecraft.get()
val fabricApiVersion: String = libs.versions.fabric.api.get()
val architecturyVersion: String = libs.versions.architectury.get()
val forgeConfigPortVersion: String = libs.versions.fabric.forgeConfigPort.get()
val gameTestRuntime: Configuration by configurations.creating

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
            property("fabric-api.gametest.report-file",
                gameTestResultsDir.get().file("fabric-game-tests.xml").asFile.absolutePath)
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
    modRuntimeOnly(libs.fabric.roughlyEnoughItems) {
        exclude(group = "net.fabricmc.fabric-api")
    }

    gameTestRuntime(project(path = ":gametest-fabric", configuration = "namedElements"))
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
    description = "Deletes game test results from previous runs."
    delete(gameTestResultsDir)
}

// Fabric seems to have a typo in their test result root node, missing the last s...
val fixGameTestReport = tasks.register("fixGameTestReport") {
    val reportFile = gameTestResultsDir.map { it.file("fabric-game-tests.xml") }
    outputs.upToDateWhen { false }
    onlyIf { reportFile.get().asFile.exists() }
    doLast {
        val file = reportFile.get().asFile
        val document = javax.xml.parsers.DocumentBuilderFactory.newInstance()
            .newDocumentBuilder().parse(file)
        val root = document.documentElement
        var changed = false

        if (root.tagName == "testsuite" && root.getElementsByTagName("testsuite").length > 0) {
            document.renameNode(root, null, "testsuites")
            changed = true
        }

        val suites = document.getElementsByTagName("testsuite")
        for (i in 0 until suites.length) {
            val suite = suites.item(i) as org.w3c.dom.Element
            if (!suite.hasAttribute("name")) {
                suite.setAttribute("name", "gameTest")
                changed = true
            }
        }

        if (changed) {
            javax.xml.transform.TransformerFactory.newInstance().newTransformer()
                .transform(
                    javax.xml.transform.dom.DOMSource(document),
                    javax.xml.transform.stream.StreamResult(file)
                )
        }
    }
}

tasks.named<JavaExec>("runGameTest") {
    dependsOn(cleanGameTestResults)
    classpath += gameTestRuntime
    finalizedBy(fixGameTestReport)
}

tasks.named("test") {
    setDependsOn(dependsOn.filterNot { "runGameTest" in it.toString() })
}
