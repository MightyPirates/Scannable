dependencies {
    modImplementation(libs.fabric.loader)
    modApi(libs.fabric.api)
    modApi(libs.fabric.architectury)

    modCompileOnly(libs.fabric.energy.get().toString()) {
        exclude(group = "net.fabricmc.fabric-api")
    }

    compileOnly(project(path = ":common", configuration = "namedElements"))
}
