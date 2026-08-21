dependencies {
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
    modImplementation(libs.fabric.architectury)

    modCompileOnly(libs.fabric.energy.get().toString()) {
        exclude(group = "net.fabricmc.fabric-api")
    }

    compileOnly(project(path = ":common", configuration = "namedElements"))
}
