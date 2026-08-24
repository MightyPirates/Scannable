val enabledPlatforms: String by project
val modId: String by project

architectury {
    common(enabledPlatforms.split(","))
}

sourceSets.main {
    // The client and server datagen passes are separate processes, and each prunes anything in its
    // output directory it did not write, so they cannot share one.
    resources.srcDir("src/generated/client")
    resources.srcDir("src/generated/server")
    resources.exclude(".cache/**")
}

loom {
    accessWidenerPath.set(file("src/main/resources/${modId}.accesswidener"))
}

dependencies {
    modImplementation(libs.fabric.loader)
    modApi(libs.architectury.api)
}
