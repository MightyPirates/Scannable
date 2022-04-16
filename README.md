# Scannable (Fabric)

This is the official port of Scannable to the Fabric modding platform.

Please refer to the [original README](https://github.com/MightyPirates/Scannable#readme) for more information.

## License / Use in Modpacks
This mod is [licensed under the **MIT license**](LICENSE). All **assets are public domain**, unless otherwise stated; all are free to be distributed as long as the license / source credits are kept. This means you can use this mod in any mod pack **as you please**. I'd be happy to hear about you using it, though, just out of curiosity.

## Extending
In general, please refer to [the API](src/main/java/li/cil/scannable/api), everything you need to know should be explained in the Javadoc of the API classes and interfaces. The scan result provider API allows registering custom scanning logic, you can provide custom scanner modules by providing a scan result provider as a capability of an item.

### Gradle
To add a dependency to Scannable for use in your mod, you can use [CurseMaven](https://cursemaven.com).
