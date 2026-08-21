val modId: String by project
val gameTestResultsDir = layout.buildDirectory.dir("test-results/gameTest")
val minecraftVersion: String = libs.versions.minecraft.get()
val neoforgeVersion: String = libs.versions.neoforge.platform.get()
val neoforgeLoaderVersion: String = libs.versions.neoforge.loader.get()
val architecturyVersion: String = libs.versions.architectury.get()
val gameTestRuntime: Configuration by configurations.creating

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    runs {
        named("client") { runDir = "run/client" }
        named("server") { runDir = "run/server" }

        create("clientData") {
            clientData()
            programArgs("--mod", modId)
            programArgs("--output", file("src/generated/resources/").absolutePath)
            programArgs("--existing", project(":common").file("src/main/resources").absolutePath)
            programArgs("--existing", file("src/main/resources").absolutePath)
        }
        create("gameTestServer") {
            server()
            name("Game Test Server")
            mainClass.set("net.neoforged.fml.startup.GameTestServer")
            runDir = "run/gametest"
            programArgs("--tests", "scannable_gametest:*")
            property("scannable.gameTest.junitDir", gameTestResultsDir.get().asFile.absolutePath)
            vmArg("-ea")
        }
        create("serverData") {
            serverData()
            programArgs("--mod", modId)
            programArgs("--output", file("src/generated/resources/").absolutePath)
            programArgs("--existing", project(":common").file("src/main/resources").absolutePath)
            programArgs("--existing", file("src/main/resources").absolutePath)
        }
    }
}

repositories {
    maven("https://maven.neoforged.net/releases")
}

dependencies {
    neoForge(libs.neoforge.platform)
    modImplementation(libs.neoforge.architectury)

    gameTestRuntime(project(":gametest-neoforge"))
}

tasks {
    processResources {
        val properties = mapOf(
            "version" to project.version,
            "minecraftVersion" to minecraftVersion,
            "neoforgeVersion" to neoforgeVersion,
            "loaderVersion" to neoforgeLoaderVersion,
            "architecturyVersion" to architecturyVersion
        )
        inputs.properties(properties)
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(properties)
        }
    }

    remapJar {
        atAccessWideners.add("${modId}.accesswidener")
    }
}

val cleanGameTestResults = tasks.register<Delete>("cleanGameTestResults") {
    description = "Deletes game test results and the scratch world from previous runs."
    delete(gameTestResultsDir)
    delete(layout.projectDirectory.dir("run/gametest/gametestserver"))
    delete(layout.projectDirectory.dir("run/gametest/world"))
}

tasks.named<JavaExec>("runGameTestServer") {
    dependsOn(cleanGameTestResults)
    classpath += gameTestRuntime
}
