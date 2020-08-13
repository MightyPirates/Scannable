package li.cil.scannable.common.config;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import li.cil.scannable.api.API;
import li.cil.scannable.common.Scannable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.Util;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = API.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Settings {
    // --------------------------------------------------------------------- //
    // Server settings

    private static final ServerSettings SERVER_INSTANCE;
    private static final ForgeConfigSpec SERVER_SPEC;

    // --------------------------------------------------------------------- //

    public static boolean useEnergy = true;
    public static int energyCapacityScanner = 5000;
    public static int energyCostModuleRange = 100;
    public static int energyCostModuleAnimal = 25;
    public static int energyCostModuleMonster = 50;
    public static int energyCostModuleOreCommon = 75;
    public static int energyCostModuleOreRare = 100;
    public static int energyCostModuleBlock = 100;
    public static int energyCostModuleStructure = 150;
    public static int energyCostModuleFluid = 50;
    public static int energyCostModuleEntity = 75;

    public static int baseScanRadius = 64;

    public static Set<Block> ignoredBlocks = Util.make(new HashSet<>(), c -> {
        c.add(Blocks.COMMAND_BLOCK);
    });
    public static Set<Tag<Block>> ignoredBlockTags = new HashSet<>();

    public static Set<Block> commonOreBlocks = Util.make(new HashSet<>(), c -> {
        c.add(Blocks.CLAY);
    });
    public static Set<Tag<Block>> commonOreBlockTags = Util.make(new HashSet<>(), c -> {
        c.add(Tags.Blocks.ORES_COAL);
        c.add(Tags.Blocks.ORES_IRON);
        c.add(Tags.Blocks.ORES_REDSTONE);
        c.add(Tags.Blocks.ORES_QUARTZ);
    });
    public static Set<Block> rareOreBlocks = Util.make(new HashSet<>(), c -> {
        c.add(Blocks.GLOWSTONE);
    });
    public static Set<Tag<Block>> rareOreBlockTags = new HashSet<>();

    public static Set<Tag<Fluid>> ignoredFluidTags = new HashSet<>();

    public static Set<String> structures = Util.make(new HashSet<>(), c -> {
        c.addAll(GameData.getStructureMap().keySet());
    });

    // --------------------------------------------------------------------- //
    // Client settings

    private static final ClientSettings CLIENT_INSTANCE;
    private static final ForgeConfigSpec CLIENT_SPEC;

    public static Object2IntMap<Tag<Block>> blockColors = Util.make(new Object2IntOpenHashMap<>(), c -> {
        // Minecraft
        c.put(Tags.Blocks.ORES_COAL, MaterialColor.GRAY.colorValue);
        c.put(Tags.Blocks.ORES_IRON, MaterialColor.BROWN.colorValue); // MaterialColor.IRON is also gray, so...
        c.put(Tags.Blocks.ORES_GOLD, MaterialColor.GOLD.colorValue);
        c.put(Tags.Blocks.ORES_LAPIS, MaterialColor.LAPIS.colorValue);
        c.put(Tags.Blocks.ORES_DIAMOND, MaterialColor.DIAMOND.colorValue);
        c.put(Tags.Blocks.ORES_REDSTONE, MaterialColor.RED.colorValue);
        c.put(Tags.Blocks.ORES_EMERALD, MaterialColor.EMERALD.colorValue);
        c.put(Tags.Blocks.ORES_QUARTZ, MaterialColor.QUARTZ.colorValue);
        c.put(oreTag("glowstone"), MaterialColor.YELLOW.colorValue);

        // Common modded ores
        c.put(oreTag("tin"), MaterialColor.CYAN.colorValue);
        c.put(oreTag("copper"), MaterialColor.ORANGE_TERRACOTTA.colorValue);
        c.put(oreTag("lead"), MaterialColor.BLUE_TERRACOTTA.colorValue);
        c.put(oreTag("silver"), MaterialColor.LIGHT_GRAY.colorValue);
        c.put(oreTag("nickel"), MaterialColor.LIGHT_BLUE.colorValue);
        c.put(oreTag("platinum"), MaterialColor.WHITE_TERRACOTTA.colorValue);
        c.put(oreTag("mithril"), MaterialColor.PURPLE.colorValue);
    });
    public static Object2IntMap<Tag<Fluid>> fluidColors = Util.make(new Object2IntOpenHashMap<>(), c -> {
        c.put(FluidTags.WATER, MaterialColor.WATER.colorValue);
        c.put(FluidTags.LAVA, MaterialColor.ORANGE_TERRACOTTA.colorValue);
    });

    private static Tag<Block> oreTag(final String oreName) {
        return new BlockTags.Wrapper(new ResourceLocation("forge", "ores/" + oreName));
    }

    // --------------------------------------------------------------------- //

    static {
        final Pair<ServerSettings, ForgeConfigSpec> serverConfig = new ForgeConfigSpec.Builder().configure(ServerSettings::new);
        SERVER_INSTANCE = serverConfig.getKey();
        SERVER_SPEC = serverConfig.getValue();

        final Pair<ClientSettings, ForgeConfigSpec> clientConfig = new ForgeConfigSpec.Builder().configure(ClientSettings::new);
        CLIENT_INSTANCE = clientConfig.getKey();
        CLIENT_SPEC = clientConfig.getValue();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Settings.SERVER_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Settings.CLIENT_SPEC);
    }

    public static boolean isServerConfig(final ForgeConfigSpec spec) {
        return spec == SERVER_SPEC;
    }

    public static boolean isClientConfig(final ForgeConfigSpec spec) {
        return spec == CLIENT_SPEC;
    }

    public static boolean shouldIgnore(final Block block) {
        if (ignoredBlocks.contains(block)) {
            return true;
        }

        for (final Tag<Block> ignoredBlockTag : ignoredBlockTags) {
            if (ignoredBlockTag.contains(block)) {
                return true;
            }
        }

        return false;
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
        if (isServerConfig(configEvent.getConfig().getSpec())) {
            useEnergy = SERVER_INSTANCE.useEnergy.get();
            energyCapacityScanner = SERVER_INSTANCE.energyCapacityScanner.get();
            energyCostModuleRange = SERVER_INSTANCE.energyCostModuleRange.get();
            energyCostModuleAnimal = SERVER_INSTANCE.energyCostModuleAnimal.get();
            energyCostModuleMonster = SERVER_INSTANCE.energyCostModuleMonster.get();
            energyCostModuleOreCommon = SERVER_INSTANCE.energyCostModuleOreCommon.get();
            energyCostModuleOreRare = SERVER_INSTANCE.energyCostModuleOreRare.get();
            energyCostModuleBlock = SERVER_INSTANCE.energyCostModuleBlock.get();
            energyCostModuleStructure = SERVER_INSTANCE.energyCostModuleStructure.get();
            energyCostModuleFluid = SERVER_INSTANCE.energyCostModuleFluid.get();
            energyCostModuleEntity = SERVER_INSTANCE.energyCostModuleEntity.get();

            baseScanRadius = SERVER_INSTANCE.baseScanRadius.get();

            ignoredBlocks = deserializeSet(SERVER_INSTANCE.ignoredBlocks.get(), Settings::getBlock);
            ignoredBlockTags = deserializeSet(SERVER_INSTANCE.ignoredBlockTags.get(), Settings::getBlockTag);

            commonOreBlocks = deserializeSet(SERVER_INSTANCE.commonOreBlocks.get(), Settings::getBlock);
            commonOreBlockTags = deserializeSet(SERVER_INSTANCE.commonOreBlockTags.get(), Settings::getBlockTag);
            rareOreBlocks = deserializeSet(SERVER_INSTANCE.rareOreBlocks.get(), Settings::getBlock);
            rareOreBlockTags = deserializeSet(SERVER_INSTANCE.rareOreBlockTags.get(), Settings::getBlockTag);

            ignoredFluidTags = deserializeSet(SERVER_INSTANCE.ignoredFluidTags.get(), Settings::getFluidTag);

            structures = deserializeSet(SERVER_INSTANCE.structures.get(), s -> s);
        }

        if (isClientConfig(configEvent.getConfig().getSpec())) {
            deserializeMap(blockColors, CLIENT_INSTANCE.blockColors.get(), Settings::getBlockTag, Integer::decode);
            deserializeMap(fluidColors, CLIENT_INSTANCE.fluidColors.get(), Settings::getFluidTag, Integer::decode);
        }
    }

    private static final class ServerSettings {
        public ForgeConfigSpec.BooleanValue useEnergy;

        public ForgeConfigSpec.IntValue energyCapacityScanner;
        public ForgeConfigSpec.IntValue energyCostModuleRange;
        public ForgeConfigSpec.IntValue energyCostModuleAnimal;
        public ForgeConfigSpec.IntValue energyCostModuleMonster;
        public ForgeConfigSpec.IntValue energyCostModuleOreCommon;
        public ForgeConfigSpec.IntValue energyCostModuleOreRare;
        public ForgeConfigSpec.IntValue energyCostModuleBlock;
        public ForgeConfigSpec.IntValue energyCostModuleStructure;
        public ForgeConfigSpec.IntValue energyCostModuleFluid;
        public ForgeConfigSpec.IntValue energyCostModuleEntity;

        public ForgeConfigSpec.IntValue baseScanRadius;

        public ForgeConfigSpec.ConfigValue<List<? extends String>> ignoredBlocks;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> ignoredBlockTags;

        public ForgeConfigSpec.ConfigValue<List<? extends String>> commonOreBlocks;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> commonOreBlockTags;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> rareOreBlocks;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> rareOreBlockTags;

        public ForgeConfigSpec.ConfigValue<List<? extends String>> ignoredFluidTags;

        public ForgeConfigSpec.ConfigValue<List<? extends String>> structures;

        public ServerSettings(final ForgeConfigSpec.Builder builder) {
            builder.push("energy");

            useEnergy = builder
                    .translation(Constants.CONFIG_USE_ENERGY)
                    .comment("Whether to consume energy when performing a scan. Will make the scanner a chargeable item.")
                    .worldRestart()
                    .define("useEnergy", Settings.useEnergy);

            energyCapacityScanner = builder
                    .translation(Constants.CONFIG_ENERGY_CAPACITY_SCANNER)
                    .comment("Amount of energy that can be stored in a scanner.")
                    .worldRestart()
                    .defineInRange("energyCapacityScanner", Settings.energyCapacityScanner, 0, Integer.MAX_VALUE);

            energyCostModuleRange = builder
                    .translation(Constants.CONFIG_ENERGY_MODULE_RANGE)
                    .comment("Amount of energy used by the range module per scan.")
                    .worldRestart()
                    .defineInRange("energyCostModuleRange", Settings.energyCostModuleRange, 0, Integer.MAX_VALUE);

            energyCostModuleAnimal = builder
                    .translation(Constants.CONFIG_ENERGY_MODULE_ANIMAL)
                    .comment("Amount of energy used by the animal module per scan.")
                    .worldRestart()
                    .defineInRange("energyCostModuleAnimal", Settings.energyCostModuleAnimal, 0, Integer.MAX_VALUE);

            energyCostModuleMonster = builder
                    .translation(Constants.CONFIG_ENERGY_MODULE_MONSTER)
                    .comment("Amount of energy used by the monster module per scan.")
                    .worldRestart()
                    .defineInRange("energyCostModuleMonster", Settings.energyCostModuleMonster, 0, Integer.MAX_VALUE);

            energyCostModuleOreCommon = builder
                    .translation(Constants.CONFIG_ENERGY_MODULE_ORE_COMMON)
                    .comment("Amount of energy used by the common ore module per scan.")
                    .worldRestart()
                    .defineInRange("energyCostModuleOreCommon", Settings.energyCostModuleOreCommon, 0, Integer.MAX_VALUE);

            energyCostModuleOreRare = builder
                    .translation(Constants.CONFIG_ENERGY_MODULE_ORE_RARE)
                    .comment("Amount of energy used by the rare ore module per scan.")
                    .worldRestart()
                    .defineInRange("energyCostModuleOreRare", Settings.energyCostModuleOreRare, 0, Integer.MAX_VALUE);

            energyCostModuleBlock = builder
                    .translation(Constants.CONFIG_ENERGY_MODULE_BLOCK)
                    .comment("Amount of energy used by the block module per scan.")
                    .worldRestart()
                    .defineInRange("energyCostModuleBlock", Settings.energyCostModuleBlock, 0, Integer.MAX_VALUE);

            energyCostModuleStructure = builder
                    .translation(Constants.CONFIG_ENERGY_MODULE_STRUCTURE)
                    .comment("Amount of energy used by the structure module per scan.")
                    .worldRestart()
                    .defineInRange("energyCostModuleStructure", Settings.energyCostModuleStructure, 0, Integer.MAX_VALUE);

            energyCostModuleFluid = builder
                    .translation(Constants.CONFIG_ENERGY_MODULE_FLUID)
                    .comment("Amount of energy used by the fluid module per scan.")
                    .worldRestart()
                    .defineInRange("energyCostModuleFluid", Settings.energyCostModuleFluid, 0, Integer.MAX_VALUE);

            energyCostModuleEntity = builder
                    .translation(Constants.CONFIG_ENERGY_MODULE_ENTITY)
                    .comment("Amount of energy used by the entity module per scan.")
                    .worldRestart()
                    .defineInRange("energyCostModuleEntity", Settings.energyCostModuleEntity, 0, Integer.MAX_VALUE);

            builder.pop();

            builder.push("general");

            baseScanRadius = builder
                    .translation(Constants.CONFIG_BASE_SCAN_RADIUS)
                    .comment("The basic scan radius without range modules. Higher values mean more computational\n" +
                            "overhead and thus potentially worse performance while scanning.\n" +
                            "IMPORTANT: some modules such as the block and ore scanner modules will already use\n" +
                            "a reduced radius based on this value. Specifically, the ore scanners multiply this\n" +
                            "value by " + Constants.MODULE_ORE_RADIUS_MULTIPLIER + ", and the block scanner multiplies it by " + Constants.MODULE_BLOCK_RADIUS_MULTIPLIER + ".\n" +
                            "Range modules will boost the range by half this value.")
                    .worldRestart()
                    .defineInRange("baseScanRadius", Settings.baseScanRadius, 16, 128);

            builder.pop();

            builder.push("blocks");

            ignoredBlocks = builder
                    .translation(Constants.CONFIG_IGNORED_BLOCKS)
                    .comment("Registry names of blocks that should be ignored.\n" +
                            "Blocks in this list will be excluded from the default ore list based on the forge:ores\n" +
                            "tag and it will be impossible to tune the entity module to this block.")
                    .worldRestart()
                    .defineList("ignoredBlocks", serializeSet(Settings.ignoredBlocks, v -> Objects.requireNonNull(v.getRegistryName()).toString()), Settings::validateResourceLocation);

            ignoredBlockTags = builder
                    .translation(Constants.CONFIG_IGNORED_BLOCK_TAGS)
                    .comment("Tag names of block tags that should be ignored.\n" +
                            "Blocks matching a tag in this list will be excluded from the default ore list based on the" +
                            "forge:ores tag and it will be impossible to tune the entity module to this block.")
                    .worldRestart()
                    .defineList("ignoredBlockTags", serializeSet(Settings.ignoredBlockTags, v -> v.getId().toString()), Settings::validateResourceLocation);

            builder.pop();

            builder.push("ores");

            commonOreBlocks = builder
                    .translation(Constants.CONFIG_ORE_COMMON_BLOCKS)
                    .comment("Registry names of blocks considered 'common ores', requiring the common ore scanner module.")
                    .worldRestart()
                    .defineList("commonOreBlocks", serializeSet(Settings.commonOreBlocks, v -> Objects.requireNonNull(v.getRegistryName()).toString()), Settings::validateResourceLocation);

            commonOreBlockTags = builder
                    .translation(Constants.CONFIG_ORE_COMMON_BLOCK_TAGS)
                    .comment("Block tags of blocks considered 'common ores', requiring the common ore scanner module.")
                    .worldRestart()
                    .defineList("commonOreBlockTags", serializeSet(Settings.commonOreBlockTags, v -> v.getId().toString()), Settings::validateResourceLocation);

            rareOreBlocks = builder
                    .translation(Constants.CONFIG_ORE_RARE_BLOCKS)
                    .comment("Registry names of blocks considered 'rare ores', requiring the common ore scanner module.")
                    .worldRestart()
                    .defineList("rareOreBlocks", serializeSet(Settings.rareOreBlocks, v -> Objects.requireNonNull(v.getRegistryName()).toString()), Settings::validateResourceLocation);

            rareOreBlockTags = builder
                    .translation(Constants.CONFIG_ORE_RARE_BLOCK_TAGS)
                    .comment("Block tags of blocks considered 'rare ores', requiring the common ore scanner module.\n" +
                            "Any block with the forge:ores tag is implicitly in this list, unless the block also\n" +
                            "matches an ignored or common ore block tag, or is an ignored or common block.")
                    .worldRestart()
                    .defineList("rareOreBlockTags", serializeSet(Settings.rareOreBlockTags, v -> v.getId().toString()), Settings::validateResourceLocation);

            builder.pop();

            builder.push("fluids");

            ignoredFluidTags = builder
                    .translation(Constants.CONFIG_IGNORED_FLUID_TAGS)
                    .comment("Fluid tags of fluids that should be ignored.")
                    .worldRestart()
                    .defineList("ignoredFluidTags", serializeSet(Settings.ignoredFluidTags, v -> v.getId().toString()), Settings::validateResourceLocation);

            builder.pop();

            builder.push("structures");

            structures = builder
                    .translation(Constants.CONFIG_STRUCTURES)
                    .comment("The list of structures the structure module scans for.")
                    .worldRestart()
                    .defineList("structures", serializeSet(Settings.structures, v -> v), o -> GameData.getStructureMap().containsKey(o));

            builder.pop();
        }
    }

    private static final class ClientSettings {
        public ForgeConfigSpec.ConfigValue<List<? extends String>> blockColors;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> fluidColors;

        public ClientSettings(final ForgeConfigSpec.Builder builder) {
            blockColors = builder
                    .translation(Constants.CONFIG_BLOCK_COLORS)
                    .comment("The colors for blocks used when rendering their result bounding box.\n" +
                            "Each entry must be a key-value pair separated by a `=`, with the.\n" +
                            "key being the ore dictionary name and the value being the hexadecimal\n" +
                            "RGB value of the color.")
                    .worldRestart()
                    .defineList("blockColors", serializeMap(Settings.blockColors, t -> t.getId().toString(), c -> "0x" + Integer.toHexString(c)),
                            Settings::validateResourceLocationMapEntry);

            fluidColors = builder
                    .translation(Constants.CONFIG_FLUID_COLORS)
                    .comment("The colors for fluids used when rendering their result bounding box.\n" +
                            "See `oreColors` for format entries have to be in.")
                    .worldRestart()
                    .defineList("fluidColors", serializeMap(Settings.fluidColors, t -> t.getId().toString(), c -> "0x" + Integer.toHexString(c)),
                            Settings::validateResourceLocationMapEntry);
        }
    }

    // --------------------------------------------------------------------- //

    private static boolean validateResourceLocationMapEntry(final Object value) {
        return validateMapEntry(value, Settings::validateResourceLocation);
    }

    private static boolean validateMapEntry(final Object value, final Predicate<String> validateKey) {
        final String[] keyValue = ((String) value).split("=", 2);
        if (keyValue.length != 2) {
            return false;
        }
        if (!validateKey.test(keyValue[0])) {
            return false;
        }
        try {
            Integer.decode(keyValue[1]);
        } catch (final NumberFormatException e) {
            return false;
        }

        return true;
    }

    private static boolean validateResourceLocation(final Object value) {
        try {
            new ResourceLocation((String) value);
            return true;
        } catch (final ResourceLocationException e) {
            Scannable.getLog().error(e);
            return false;
        }
    }

    @Nullable
    private static Block getBlock(final String o) {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(o));
    }

    private static Tag<Block> getBlockTag(final String o) {
        return new BlockTags.Wrapper(new ResourceLocation(o));
    }

    private static Tag<Fluid> getFluidTag(final String o) {
        return new FluidTags.Wrapper(new ResourceLocation(o));
    }

    private static <T> List<? extends String> serializeSet(final Set<T> set, final Function<T, String> serializer) {
        final ArrayList<String> result = new ArrayList<>();
        for (final T v : set) {
            result.add(serializer.apply(v));
        }
        return result;
    }

    private static <T> Set<T> deserializeSet(final List<? extends String> list, final Function<String, T> deserializer) {
        final Set<T> result = new HashSet<>();
        for (final String v : list) {
            final T t = deserializer.apply(v);
            if (t != null) {
                result.add(t);
            }
        }
        return result;
    }

    private static <K, V> List<? extends String> serializeMap(final Map<K, V> map, final Function<K, String> keySerializer, final Function<V, String> valueSerializer) {
        final ArrayList<String> result = new ArrayList<>();
        map.forEach((k, v) -> result.add(keySerializer.apply(k) + "=" + valueSerializer.apply(v)));
        return result;
    }

    private static <K, V> void deserializeMap(final Map<K, V> map, final List<? extends String> list, final Function<String, K> keyDeserializer, final Function<String, V> valueDeserializer) {
        map.clear();
        for (final String v : list) {
            final String[] keyValue = v.split("=", 2);
            if (keyValue.length != 2) {
                Scannable.getLog().error("Failed parsing setting value [{}].", v);
                continue;
            }

            final K key = keyDeserializer.apply(keyValue[0]);
            final V value = valueDeserializer.apply(keyValue[1]);

            if (key != null && value != null) {
                map.put(key, value);
            }
        }
    }

    // --------------------------------------------------------------------- //

    private Settings() {
    }
}
