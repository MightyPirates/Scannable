import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    java
    alias(libs.plugins.architectury)
    alias(libs.plugins.loom) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.spotless)
}

val modId: String by project
val modVersion: String by project
val mavenGroup: String by project
val minecraftVersion: String = libs.versions.minecraft.get()

fun getGitRef(): String {
    return providers.exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        isIgnoreExitValue = true
    }.standardOutput.asText.get().trim()
}

subprojects {
    apply(plugin = "java")
    apply(plugin = rootProject.libs.plugins.architectury.get().pluginId)
    apply(plugin = rootProject.libs.plugins.loom.get().pluginId)

    version = "${modVersion}+${getGitRef()}"
    group = mavenGroup
    base.archivesName.set("${modId}-MC${minecraftVersion}-${project.name}")

    architectury {
        minecraft = minecraftVersion
    }

    project.extra.set("mod_id", modId)

    configure<LoomGradleExtensionAPI> {
        silentMojangMappingsLicense()
    }

    repositories {
        maven {
            url = uri("https://cursemaven.com")
            content { includeGroup("curse.maven") }
        }
    }

    dependencies {
        "minecraft"(rootProject.libs.minecraft)
        "mappings"(project.extensions.getByName<LoomGradleExtensionAPI>("loom").officialMojangMappings())
        "compileOnly"("com.google.code.findbugs:jsr305:3.0.2")
    }

    tasks {
        jar {
            from("LICENSE") {
                rename { "${it}_${modId}" }
            }
        }

        withType<JavaCompile>().configureEach {
            options.encoding = "utf-8"
            options.release.set(17)
        }
    }

    idea {
        module {
            for (exclude in arrayOf("out", "logs")) {
                excludeDirs.add(file(exclude))
            }
        }
    }
}

spotless {
    java {
        target("*/src/*/java/li/cil/**/*.java")

        endWithNewline()
        trimTrailingWhitespace()
        removeUnusedImports()
        indentWithSpaces()
    }
}
