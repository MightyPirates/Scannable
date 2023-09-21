package li.cil.scannable.common.config;

import dev.architectury.injectables.annotations.ExpectPlatform;
import li.cil.scannable.util.config.*;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import java.util.HashSet;
import java.util.Set;

public final class CommonConfig {
    @Path("energy") @WorldRestart
    @Comment("Whether to consume energy when performing a scan. Will make the scanner a chargeable item.")
    public static boolean useEnergy = true;

    @Path("energy") @WorldRestart @Min(0)
    @Comment("Amount of energy that can be stored in a scanner.")
    public static int energyCapacityScanner = 5000;

    @Path("energy") @WorldRestart @Min(0)
    @Comment("Amount of energy used by the range module per scan.")
    public static int energyCostModuleRange = 100;

    @Path("energy") @WorldRestart @Min(0)
    @Comment("Amount of energy used by the animal module per scan.")
    public static int energyCostModuleAnimal = 25;

    @Path("energy") @WorldRestart @Min(0)
    @Comment("Amount of energy used by the monster module per scan.")
    public static int energyCostModuleMonster = 50;

    @Path("energy") @WorldRestart @Min(0)
    @Comment("Amount of energy used by the common ore module per scan.")
    public static int energyCostModuleOreCommon = 75;

    @Path("energy") @WorldRestart @Min(0)
    @Comment("Amount of energy used by the rare ore module per scan.")
    public static int energyCostModuleOreRare = 100;

    @Path("energy") @WorldRestart @Min(0)
    @Comment("Amount of energy used by the block module per scan.")
    public static int energyCostModuleBlock = 100;

    @Path("energy") @WorldRestart @Min(0)
    @Comment("Amount of energy used by the fluid module per scan.")
    public static int energyCostModuleFluid = 50;

    @Path("energy") @WorldRestart @Min(0)
    @Comment("Amount of energy used by the entity module per scan.")
    public static int energyCostModuleEntity = 75;

    @Path("energy") @WorldRestart @Min(0)
    @Comment("Amount of energy used by the chest module per scan.")
    public static int energyCostModuleChest = 100;

    @Path("range") @WorldRestart @Min(0) @Max(1)
    @Comment("Relative amount of base scan radius added by each installed range module.")
    public static float rangeModifierModuleRange = 0.5f;

    @Path("range") @WorldRestart @Min(0) @Max(1)
    @Comment("Relative effective range of the common ore module.")
    public static float rangeModifierModuleOreCommon = 0.25f;

    @Path("range") @WorldRestart @Min(0) @Max(1)
    @Comment("Relative effective range of the rare ore module.")
    public static float rangeModifierModuleOreRare = 0.25f;

    @Path("range") @WorldRestart @Min(0) @Max(1)
    @Comment("Relative effective range of the block module.")
    public static float rangeModifierModuleBlock = 0.5f;

    @Path("range") @WorldRestart @Min(0) @Max(1)
    @Comment("Relative effective range of the fluid module.")
    public static float rangeModifierModuleFluid = 0.5f;

    @Path("range") @WorldRestart @Min(0) @Max(1)
    @Comment("Relative effective range of the chest module.")
    public static float rangeModifierModuleChest = 0.25f;

    // TODO Migrate to range category in next major version.
    @Path("general") @WorldRestart @Min(16) @Max(128)
    @Comment("The basic scan radius without range modules. Higher values mean more computational\n" +
        "overhead and thus potentially worse performance while scanning.")
    public static int baseScanRadius = 64;

    @Path("general") @WorldRestart @Min(1000) @Max(60000 * 5)
    @Comment("How long the results from a scan should remain visible, in milliseconds.")
    public static int scanStayDuration = 10000;

    @Path("blocks") @WorldRestart
    @Comment("""
        Registry names of blocks that should be ignored.
        Blocks in this list will be excluded from the default ore list based on the forge:ores
        tag and it will be impossible to tune the entity module to this block.""")
    @ItemType(ResourceLocation.class)
    public static Set<ResourceLocation> ignoredBlocks = Util.make(new HashSet<>(), c -> {
        c.add(BuiltInRegistries.BLOCK.getKey(Blocks.COMMAND_BLOCK));
    });

    @Path("blocks") @WorldRestart
    @Comment("""
        Tag names of block tags that should be ignored.
        Blocks matching a tag in this list will be excluded from the default ore list based on the
        forge:ores tag and it will be impossible to tune the entity module to this block.""")
    @ItemType(ResourceLocation.class)
    public static Set<ResourceLocation> ignoredBlockTags = new HashSet<>();

    @Path("ores") @WorldRestart
    @Comment("Registry names of blocks considered 'common ores', requiring the common ore scanner module.")
    @ItemType(ResourceLocation.class)
    public static Set<ResourceLocation> commonOreBlocks = Util.make(new HashSet<>(), c -> {
        c.add(BuiltInRegistries.BLOCK.getKey(Blocks.CLAY));
    });

    @Path("ores") @WorldRestart
    @Comment("Block tags of blocks considered 'common ores', requiring the common ore scanner module.")
    @ItemType(ResourceLocation.class)
    public static Set<ResourceLocation> commonOreBlockTags = getDefaultCommonOreTags();

    @Path("ores") @WorldRestart
    @Comment("Registry names of blocks considered 'rare ores', requiring the rare ore scanner module.")
    @ItemType(ResourceLocation.class)
    public static Set<ResourceLocation> rareOreBlocks = Util.make(new HashSet<>(), c -> {
        c.add(BuiltInRegistries.BLOCK.getKey(Blocks.GLOWSTONE));
    });

    @Path("ores") @WorldRestart
    @Comment("""
        Block tags of blocks considered 'rare ores', requiring the common ore scanner module.
        Any block with the forge:ores tag is implicitly in this list, unless the block also
        matches an ignored or common ore block tag, or is an ignored or common block.""")
    @ItemType(ResourceLocation.class)
    public static Set<ResourceLocation> rareOreBlockTags = new HashSet<>();

    @Path("fluids") @WorldRestart
    @Comment("Fluid tags of fluids that should be ignored.")
    @ItemType(ResourceLocation.class)
    public static Set<ResourceLocation> ignoredFluidTags = new HashSet<>();

    @Path("chests") @WorldRestart
    @Comment("Registry names of blocks considered 'chests', requiring the chest scanner module.")
    @ItemType(ResourceLocation.class)
    public static Set<ResourceLocation> commonChestBlocks = new HashSet<>();

    @Path("chests") @WorldRestart
    @Comment("Registry names of blocks considered 'chests', requiring the chest scanner module.")
    @ItemType(ResourceLocation.class)
    public static Set<ResourceLocation> commonChestTags = getDefaultChestsTags();

    @ExpectPlatform
    private static Set<ResourceLocation> getDefaultCommonOreTags() {
        throw new AssertionError();
    }

    @ExpectPlatform
    private static Set<ResourceLocation> getDefaultChestsTags() {
        throw new AssertionError();
    }
}
