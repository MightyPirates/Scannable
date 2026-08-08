# Scannable
Scannable is a Minecraft mod that adds a single scanner item and a couple of scanner modules. Using the scanner will bring up a couple of overlays highlighting nearby points of interest, such as animals, monsters and ores, depending on the installed modules.

## License / Use in Modpacks
This mod is [licensed under the **MIT license**](LICENSE). All **assets are public domain**, unless otherwise stated; all are free to be distributed as long as the license / source credits are kept. This means you can use this mod in any mod pack **as you please**. I'd be happy to hear about you using it, though, just out of curiosity.

## Extending
In general, please refer to [the API](common/src/main/java/li/cil/scannable/api), everything you need to know should be explained in the Javadoc of the API classes and interfaces. The scan result provider API allows registering custom scanning logic, you can provide custom scanner modules by providing a scan result provider as a capability of an item.

### Gradle
To add a dependency to Scannable for use in your mod, add the following to your `build.gradle`:

```groovy
repositories {
    exclusiveContent {
        forRepository { maven("https://cursemaven.com") }
        filter { includeGroup("curse.maven") }
    }
}
dependencies {
    // Fabric and NeoForge
    modImplementation("curse.maven:scannable-266784:<file-id>")
}
```

Replace `<file-id>` with the ID of the file you want to build against, which you can
find on the [CurseForge files page](https://www.curseforge.com/minecraft/mc-mods/scannable/files)
(the last path segment of a file download URL).
