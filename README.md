# Scannable
Scannable is a Minecraft mod that adds a single scanner item and a couple of scanner modules. Using the scanner will bring up a couple of overlays highlighting nearby points of interest, such as animals, monsters and ores, depending on the installed modules.

*This mod requires Java 8!*

## License / Use in Modpacks
This mod is [licensed under the **MIT license**](LICENSE). All **assets are public domain**, unless otherwise stated; all are free to be distributed as long as the license / source credits are kept. This means you can use this mod in any mod pack **as you please**. I'd be happy to hear about you using it, though, just out of curiosity.

## Extending
In general, please refer to [the API](src/main/java/li/cil/scannable/api), everything you need to know should be explained in the Javadoc of the API classes and interfaces. The scan result provider API allows registering custom scanning logic, you can provide custom scanner modules by providing a scan result provider as a capability of an item.

### Gradle
To add a dependency to Scannable for use in your mod, add the following to your `build.gradle`:

```groovy
repositories {
  maven {
    url = 'https://maven.cil.li/'
  }
}
dependencies {
  compile "li.cil.scannable:scannable-1.16.5-forge:${config.scannable.version}"
}
```

Where `${config.scannable.version}` is the version you'd like to build against.
