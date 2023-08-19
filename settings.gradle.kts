pluginManagement {
    repositories {
        maven("https://maven.architectury.dev") {
            content {
                includeGroup("architectury-plugin")
                includeGroupByRegex("dev\\.architectury.*")
            }
        }
        maven("https://maven.fabricmc.net") {
            content {
                includeGroup("net.fabricmc")
                includeGroup("fabric-loom")
            }
        }
        maven("https://maven.minecraftforge.net") {
            content {
                includeGroupByRegex("net\\.minecraftforge.*")
                includeGroup("de.oceanlabs.mcp")
            }
        }
        gradlePluginPortal()
    }
}

include("common")
include("fabric")
include("forge")

val modId: String by settings
rootProject.name = modId
