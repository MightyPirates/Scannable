package li.cil.scannable.common.config;

import li.cil.scannable.api.API;
import li.cil.scannable.client.scanning.ScanResultProviderBlock;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

@Config(modid = API.MOD_ID)
public final class Settings {
    @Config.LangKey(Constants.CONFIG_USE_ENERGY)
    @Config.Comment("Whether to consume energy when performing a scan.\n" +
                    "Will make the scanner a chargeable item.")
    @Config.RequiresWorldRestart
    public static boolean useEnergy = true;

    @Config.LangKey(Constants.CONFIG_ENERGY_CAPACITY_SCANNER)
    @Config.Comment("Amount of energy that can be stored in a scanner.")
    @Config.RangeInt(min = 0, max = 1000000)
    @Config.RequiresWorldRestart
    public static int energyCapacityScanner = 5000;

    @Config.LangKey(Constants.CONFIG_ENERGY_MODULE_RANGE)
    @Config.Comment("Amount of energy used by the range module per scan.")
    @Config.RangeInt(min = 0, max = 5000)
    @Config.RequiresWorldRestart
    public static int energyCostModuleRange = 100;

    @Config.LangKey(Constants.CONFIG_ENERGY_MODULE_ANIMAL)
    @Config.Comment("Amount of energy used by the animal module per scan.")
    @Config.RangeInt(min = 0, max = 5000)
    @Config.RequiresWorldRestart
    public static int energyCostModuleAnimal = 25;

    @Config.LangKey(Constants.CONFIG_ENERGY_MODULE_MONSTER)
    @Config.Comment("Amount of energy used by the monster module per scan.")
    @Config.RangeInt(min = 0, max = 5000)
    @Config.RequiresWorldRestart
    public static int energyCostModuleMonster = 50;

    @Config.LangKey(Constants.CONFIG_ENERGY_MODULE_ORE_COMMON)
    @Config.Comment("Amount of energy used by the common ore module per scan.")
    @Config.RangeInt(min = 0, max = 5000)
    @Config.RequiresWorldRestart
    public static int energyCostModuleOreCommon = 75;

    @Config.LangKey(Constants.CONFIG_ENERGY_MODULE_ORE_RARE)
    @Config.Comment("Amount of energy used by the rare ore module per scan.")
    @Config.RangeInt(min = 0, max = 5000)
    @Config.RequiresWorldRestart
    public static int energyCostModuleOreRare = 100;

    @Config.LangKey(Constants.CONFIG_ENERGY_MODULE_BLOCK)
    @Config.Comment("Amount of energy used by the block module per scan.")
    @Config.RangeInt(min = 0, max = 5000)
    @Config.RequiresWorldRestart
    public static int energyCostModuleBlock = 100;

    @Config.LangKey(Constants.CONFIG_ENERGY_MODULE_STRUCTURE)
    @Config.Comment("Amount of energy used by the structure module per scan.")
    @Config.RangeInt(min = 0, max = 5000)
    @Config.RequiresWorldRestart
    public static int energyCostModuleStructure = 150;

    @Config.LangKey(Constants.CONFIG_ENERGY_MODULE_FLUID)
    @Config.Comment("Amount of energy used by the fluid module per scan.")
    @Config.RangeInt(min = 0, max = 5000)
    @Config.RequiresWorldRestart
    public static int energyCostModuleFluid = 50;

    @Config.LangKey(Constants.CONFIG_ENERGY_MODULE_ENTITY)
    @Config.Comment("Amount of energy used by the entity module per scan.")
    @Config.RangeInt(min = 0, max = 5000)
    @Config.RequiresWorldRestart
    public static int energyCostModuleEntity = 75;

    @Config.LangKey(Constants.CONFIG_BASE_SCAN_RADIUS)
    @Config.Comment("The basic scan radius without range modules.\n" +
                    "IMPORTANT: some modules such as the block and ore scanner modules will already use\n" +
                    "a reduced radius based on this value. Specifically, the ore scanners multiply this\n" +
                    "value by " + Constants.MODULE_ORE_RADIUS_MULTIPLIER + ", and the block scanner multiplies it by " + Constants.MODULE_BLOCK_RADIUS_MULTIPLIER + ".\n" +
                    "Range modules will boost the range by half this value.")
    @Config.RangeInt(min = 16, max = 128)
    @Config.RequiresWorldRestart
    public static int baseScanRadius = 64;

    @Config.LangKey(Constants.CONFIG_BLOCK_BLACKLIST)
    @Config.Comment("Ore dictionary entries that match the common ore pattern but should be ignored.")
    @Config.RequiresWorldRestart
    public static String[] oreBlacklist = {
    };

    @Config.LangKey(Constants.CONFIG_ORE_BLACKLIST)
    @Config.Comment("Registry names of blocks that will never be scanned.")
    @Config.RequiresWorldRestart
    public static String[] blockBlacklist = {
            "minecraft:command_block"
    };

    @Config.LangKey(Constants.CONFIG_ORES_COMMON)
    @Config.Comment("Ore dictionary entries considered common ores, requiring the common ore scanner module.\n" +
                    "Use this to mark ores as common, as opposed to rare (see oresRare).")
    @Config.RequiresWorldRestart
    public static String[] oresCommon = {
            // Minecraft
            "oreCoal",
            "oreIron",
            "oreRedstone",
            "glowstone",

            // Thermal Foundation
            "oreCopper",
            "oreTin",
            "oreLead",

            // Immersive Engineering
            "oreAluminum",
            "oreAluminium",

            // Thaumcraft
            "oreCinnabar"
    };

    @Config.LangKey(Constants.CONFIG_ORES_RARE)
    @Config.Comment("Ore dictionary names of ores considered 'rare', requiring the rare ore scanner module.\n" +
                    "Anything matching /ore[A-Z].*/ that isn't in the common ore list is\n" +
                    "automatically considered a rare ore (as opposed to the other way around,\n" +
                    "to make missing entries less likely be a problem). Use this to add rare\n" +
                    "ores that do follow this pattern.")
    @Config.RequiresWorldRestart
    public static String[] oresRare = {
    };

    @Config.LangKey(Constants.CONFIG_ORE_COLORS)
    @Config.Comment("The colors for ores used when rendering their result bounding box.\n" +
                    "Each entry must be a key-value pair separated by a `=`, with the.\n" +
                    "key being the ore dictionary name and the value being the hexadecimal\n" +
                    "RGB value of the color.")
    @Config.RequiresWorldRestart
    public static String[] oreColors = {
            // Minecraft
            "oreCoal=0x433E3B",
            "oreIron=0xA17951",
            "oreGold=0xF4F71F",
            "oreLapis=0x4863F0",
            "oreDiamond=0x48E2F0",
            "oreRedstone=0xE61E1E",
            "oreEmerald=0x12BA16",
            "oreQuartz=0xB3D9D2",
            "glowstone=0xE9E68E",

            // Thermal Foundation
            "oreCopper=0xE4A020",
            "oreLead=0x8187C3",
            "oreMithril=0x97D5FE",
            "oreNickel=0xD0D3AC",
            "orePlatinum=0x7AC0FD",
            "oreSilver=0xE8F2FB",
            "oreTin=0xCCE4FE",

            // Misc.
            "oreAluminum=0xCBE4E2",
            "oreAluminium=0xCBE4E2",
            "orePlutonium=0x9DE054",
            "oreUranium=0x9DE054",
            "oreYellorium=0xD8E054",

            // Tinker's Construct
            "oreArdite=0xB77E11",
            "oreCobalt=0x413BB8",

            // Thaumcraft
            "oreCinnabar=0xF5DA25",
            "oreInfusedAir=0xF7E677",
            "oreInfusedFire=0xDC7248",
            "oreInfusedWater=0x9595D5",
            "oreInfusedEarth=0x49B45A",
            "oreInfusedOrder=0x9FF2DE",
            "oreInfusedEntropy=0x545476"
    };

    @Config.LangKey(Constants.CONFIG_STRUCTURES)
    @Config.Comment("The list of structures the structure module scans for.")
    @Config.RequiresWorldRestart
    public static String[] structures = {
            "EndCity",
            "Fortress",
            "Mansion",
            "Mineshaft",
            "Monument",
            "Stronghold",
            "Temple",
            "Village"
    };

    @Config.LangKey(Constants.CONFIG_FLUID_BLACKLIST)
    @Config.Comment("Fluid names of fluids that should be ignored.")
    @Config.RequiresWorldRestart
    public static String[] fluidBlacklist = {
    };

    @Config.LangKey(Constants.CONFIG_FLUID_COLORS)
    @Config.Comment("The colors for fluids used when rendering their result bounding box.\n" +
                    "See `oreColors` for format entries have to be in.")
    @Config.RequiresWorldRestart
    public static String[] fluidColors = {
            "water=0x4275DC",
            "lava=0xE26723"
    };

    @Config.LangKey(Constants.CONFIG_INJECT_DEPTH_TEXTURE)
    @Config.Comment("Whether to try to inject a depth texture into Minecraft's FBO when rendering the\n" +
                    "scan wave effect. This is much faster as it will not have to re-render the world\n" +
                    "geometry to retrieve the depth information required for the effect. However, it\n" +
                    "appears that on some systems this doesn't work. The mod tries to detect that and\n" +
                    "will fall back to re-rendering automatically, but you can force re-rendering by\n" +
                    "setting this to false, e.g. for debugging or just to avoid the one logged warning.")
    public static boolean injectDepthTexture = true;

    // --------------------------------------------------------------------- //

    private static ServerSettings serverSettings;
    private static final Set<Block> blockBlacklistSet = new HashSet<>();

    public static void setServerSettings(@Nullable final ServerSettings serverSettings) {
        Settings.serverSettings = serverSettings;

        ScanResultProviderBlock.INSTANCE.rebuildOreCache();

        blockBlacklistSet.clear();
        for (final String blockName : getBlockBlacklist()) {
            final Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
            if (block != null && block != Blocks.AIR) {
                blockBlacklistSet.add(block);
            }
        }
    }

    public static Set<Block> getBlockBlacklistSet() {
        return blockBlacklistSet;
    }

    // --------------------------------------------------------------------- //

    public static boolean useEnergy() {
        return serverSettings != null ? serverSettings.useEnergy : useEnergy;
    }

    public static int getEnergyCapacityScanner() {
        return serverSettings != null ? serverSettings.energyCapacityScanner : energyCapacityScanner;
    }

    public static int getEnergyCostModuleRange() {
        return serverSettings != null ? serverSettings.energyCostModuleRange : energyCostModuleRange;
    }

    public static int getEnergyCostModuleAnimal() {
        return serverSettings != null ? serverSettings.energyCostModuleAnimal : energyCostModuleAnimal;
    }

    public static int getEnergyCostModuleMonster() {
        return serverSettings != null ? serverSettings.energyCostModuleMonster : energyCostModuleMonster;
    }

    public static int getEnergyCostModuleOreCommon() {
        return serverSettings != null ? serverSettings.energyCostModuleOreCommon : energyCostModuleOreCommon;
    }

    public static int getEnergyCostModuleOreRare() {
        return serverSettings != null ? serverSettings.energyCostModuleOreRare : energyCostModuleOreRare;
    }

    public static int getEnergyCostModuleBlock() {
        return serverSettings != null ? serverSettings.energyCostModuleBlock : energyCostModuleBlock;
    }

    public static int getEnergyCostModuleStructure() {
        return serverSettings != null ? serverSettings.energyCostModuleStructure : energyCostModuleStructure;
    }

    public static int getEnergyCostModuleFluid() {
        return serverSettings != null ? serverSettings.energyCostModuleFluid : energyCostModuleFluid;
    }

    public static int getEnergyCostModuleEntity() {
        return serverSettings != null ? serverSettings.energyCostModuleEntity : energyCostModuleEntity;
    }

    public static int getBaseScanRadius() {
        return serverSettings != null ? serverSettings.baseScanRadius : baseScanRadius;
    }

    public static String[] getBlockBlacklist() {
        return serverSettings != null ? serverSettings.blockBlacklist : blockBlacklist;
    }

    public static String[] getOreBlacklist() {
        return serverSettings != null ? serverSettings.oresBlacklist : oreBlacklist;
    }

    public static String[] getCommonOres() {
        return serverSettings != null ? serverSettings.oresCommon : oresCommon;
    }

    public static String[] getRareOres() {
        return serverSettings != null ? serverSettings.oresRare : oresRare;
    }

    public static String[] getStructures() {
        return serverSettings != null ? serverSettings.structures : structures;
    }

    public static String[] getFluidBlacklist() {
        return serverSettings != null ? serverSettings.fluidBlacklist : fluidBlacklist;
    }

    // --------------------------------------------------------------------- //

    private Settings() {
    }
}
