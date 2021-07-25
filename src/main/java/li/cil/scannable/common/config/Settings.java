package li.cil.scannable.common.config;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import li.cil.scannable.api.API;
import net.minecraft.ResourceLocationException;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.GameData;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = API.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Settings {
    private static final Logger LOGGER = LogManager.getLogger();

    // --------------------------------------------------------------------- //
    // Server settings

    private static final ServerSettings SERVER_INSTANCE;
    private static final IConfigSpec<ForgeConfigSpec> SERVER_SPEC;

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
    public static int scanStayDuration = 10000;

    public static Set<ResourceLocation> ignoredBlocks = Util.make(new HashSet<>(), c -> {
        c.add(Blocks.COMMAND_BLOCK.getRegistryName());
    });
    public static Set<ResourceLocation> ignoredBlockTags = new HashSet<>();

    public static Set<ResourceLocation> commonOreBlocks = Util.make(new HashSet<>(), c -> {
        c.add(Blocks.CLAY.getRegistryName());
    });
    public static Set<ResourceLocation> commonOreBlockTags = Util.make(new HashSet<>(), c -> {
        c.add(Tags.Blocks.ORES_COAL.getName());
        c.add(Tags.Blocks.ORES_IRON.getName());
        c.add(Tags.Blocks.ORES_REDSTONE.getName());
        c.add(Tags.Blocks.ORES_QUARTZ.getName());
    });
    public static Set<ResourceLocation> rareOreBlocks = Util.make(new HashSet<>(), c -> {
        c.add(Blocks.GLOWSTONE.getRegistryName());
    });
    public static Set<ResourceLocation> rareOreBlockTags = new HashSet<>();

    public static Set<ResourceLocation> ignoredFluidTags = new HashSet<>();

    public static Set<String> structures = Util.make(new HashSet<>(), c -> {
        c.addAll(GameData.getStructureMap().keySet());
    });

    // --------------------------------------------------------------------- //
    // Client settings

    private static final ClientSettings CLIENT_INSTANCE;
    private static final IConfigSpec<ForgeConfigSpec> CLIENT_SPEC;

    public static Object2IntMap<ResourceLocation> blockColors = new Object2IntOpenHashMap<>();
    public static Object2IntMap<ResourceLocation> blockTagColors = Util.make(new Object2IntOpenHashMap<>(), c -> {
        // Minecraft
        c.put(Tags.Blocks.ORES_COAL.getName(), MaterialColor.COLOR_GRAY.col);
        c.put(Tags.Blocks.ORES_IRON.getName(), MaterialColor.COLOR_BROWN.col); // MaterialColor.IRON is also gray, so...
        c.put(Tags.Blocks.ORES_GOLD.getName(), MaterialColor.GOLD.col);
        c.put(Tags.Blocks.ORES_LAPIS.getName(), MaterialColor.LAPIS.col);
        c.put(Tags.Blocks.ORES_DIAMOND.getName(), MaterialColor.DIAMOND.col);
        c.put(Tags.Blocks.ORES_REDSTONE.getName(), MaterialColor.COLOR_RED.col);
        c.put(Tags.Blocks.ORES_EMERALD.getName(), MaterialColor.EMERALD.col);
        c.put(Tags.Blocks.ORES_QUARTZ.getName(), MaterialColor.QUARTZ.col);

        // Common modded ores
        c.put(new ResourceLocation("forge", "ores/tin"), MaterialColor.COLOR_CYAN.col);
        c.put(new ResourceLocation("forge", "ores/copper"), MaterialColor.TERRACOTTA_ORANGE.col);
        c.put(new ResourceLocation("forge", "ores/lead"), MaterialColor.TERRACOTTA_BLUE.col);
        c.put(new ResourceLocation("forge", "ores/silver"), MaterialColor.COLOR_LIGHT_GRAY.col);
        c.put(new ResourceLocation("forge", "ores/nickel"), MaterialColor.COLOR_LIGHT_BLUE.col);
        c.put(new ResourceLocation("forge", "ores/platinum"), MaterialColor.TERRACOTTA_WHITE.col);
        c.put(new ResourceLocation("forge", "ores/mithril"), MaterialColor.COLOR_PURPLE.col);
    });
    public static Object2IntMap<ResourceLocation> fluidColors = new Object2IntOpenHashMap<>();
    public static Object2IntMap<ResourceLocation> fluidTagColors = Util.make(new Object2IntOpenHashMap<>(), c -> {
        c.put(FluidTags.WATER.getName(), MaterialColor.WATER.col);
        c.put(FluidTags.LAVA.getName(), MaterialColor.TERRACOTTA_ORANGE.col);
    });

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

    public static boolean isServerConfig(final IConfigSpec<ForgeConfigSpec> spec) {
        return spec == SERVER_SPEC;
    }

    public static boolean isClientConfig(final IConfigSpec<ForgeConfigSpec> spec) {
        return spec == CLIENT_SPEC;
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent configEvent) {
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
            scanStayDuration = SERVER_INSTANCE.scanStayDuration.get();

            ignoredBlocks = deserializeSet(SERVER_INSTANCE.ignoredBlocks.get(), ResourceLocation::new);
            ignoredBlockTags = deserializeSet(SERVER_INSTANCE.ignoredBlockTags.get(), ResourceLocation::new);

            commonOreBlocks = deserializeSet(SERVER_INSTANCE.commonOreBlocks.get(), ResourceLocation::new);
            commonOreBlockTags = deserializeSet(SERVER_INSTANCE.commonOreBlockTags.get(), ResourceLocation::new);
            rareOreBlocks = deserializeSet(SERVER_INSTANCE.rareOreBlocks.get(), ResourceLocation::new);
            rareOreBlockTags = deserializeSet(SERVER_INSTANCE.rareOreBlockTags.get(), ResourceLocation::new);

            ignoredFluidTags = deserializeSet(SERVER_INSTANCE.ignoredFluidTags.get(), ResourceLocation::new);

            structures = deserializeSet(SERVER_INSTANCE.structures.get(), s -> s);
        }

        if (isClientConfig(configEvent.getConfig().getSpec())) {
            deserializeMap(blockColors, CLIENT_INSTANCE.blockColors.get(), ResourceLocation::new, Integer::decode);
            deserializeMap(blockTagColors, CLIENT_INSTANCE.blockTagColors.get(), ResourceLocation::new, Integer::decode);
            deserializeMap(fluidColors, CLIENT_INSTANCE.fluidColors.get(), ResourceLocation::new, Integer::decode);
            deserializeMap(fluidTagColors, CLIENT_INSTANCE.fluidTagColors.get(), ResourceLocation::new, Integer::decode);
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
        public ForgeConfigSpec.IntValue scanStayDuration;

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
                    .comment("Whether to consume energy when performing a scan. Will make the scanner a chargeable item.")
                    .worldRestart()
                    .define("useEnergy", Settings.useEnergy);

            energyCapacityScanner = builder
                    .comment("Amount of energy that can be stored in a scanner.")
                    .worldRestart()
                    .defineInRange("energyCapacityScanner", Settings.energyCapacityScanner, 0, Integer.MAX_VALUE);

            energyCostModuleRange = builder
                    .comment("Amount of energy used by the range module per scan.")
                    .worldRestart()
                    .defineInRange("energyCostModuleRange", Settings.energyCostModuleRange, 0, Integer.MAX_VALUE);

            energyCostModuleAnimal = builder
                    .comment("Amount of energy used by the animal module per scan.")
                    .worldRestart()
                    .defineInRange("energyCostModuleAnimal", Settings.energyCostModuleAnimal, 0, Integer.MAX_VALUE);

            energyCostModuleMonster = builder
                    .comment("Amount of energy used by the monster module per scan.")
                    .worldRestart()
                    .defineInRange("energyCostModuleMonster", Settings.energyCostModuleMonster, 0, Integer.MAX_VALUE);

            energyCostModuleOreCommon = builder
                    .comment("Amount of energy used by the common ore module per scan.")
                    .worldRestart()
                    .defineInRange("energyCostModuleOreCommon", Settings.energyCostModuleOreCommon, 0, Integer.MAX_VALUE);

            energyCostModuleOreRare = builder
                    .comment("Amount of energy used by the rare ore module per scan.")
                    .worldRestart()
                    .defineInRange("energyCostModuleOreRare", Settings.energyCostModuleOreRare, 0, Integer.MAX_VALUE);

            energyCostModuleBlock = builder
                    .comment("Amount of energy used by the block module per scan.")
                    .worldRestart()
                    .defineInRange("energyCostModuleBlock", Settings.energyCostModuleBlock, 0, Integer.MAX_VALUE);

            energyCostModuleStructure = builder
                    .comment("Amount of energy used by the structure module per scan.")
                    .worldRestart()
                    .defineInRange("energyCostModuleStructure", Settings.energyCostModuleStructure, 0, Integer.MAX_VALUE);

            energyCostModuleFluid = builder
                    .comment("Amount of energy used by the fluid module per scan.")
                    .worldRestart()
                    .defineInRange("energyCostModuleFluid", Settings.energyCostModuleFluid, 0, Integer.MAX_VALUE);

            energyCostModuleEntity = builder
                    .comment("Amount of energy used by the entity module per scan.")
                    .worldRestart()
                    .defineInRange("energyCostModuleEntity", Settings.energyCostModuleEntity, 0, Integer.MAX_VALUE);

            builder.pop();

            builder.push("general");

            baseScanRadius = builder
                    .comment("The basic scan radius without range modules. Higher values mean more computational\n" +
                             "overhead and thus potentially worse performance while scanning.\n" +
                             "IMPORTANT: some modules such as the block and ore scanner modules will already use\n" +
                             "a reduced radius based on this value. Specifically, the ore scanners multiply this\n" +
                             "value by " + Constants.ORE_MODULE_RADIUS_MULTIPLIER + ", and the block scanner multiplies it by " + Constants.BLOCK_MODULE_RADIUS_MULTIPLIER + ".\n" +
                             "Range modules will boost the range by half this value.")
                    .worldRestart()
                    .defineInRange("baseScanRadius", Settings.baseScanRadius, 16, 128);

            scanStayDuration = builder
                    .comment("How long the results from a scan should remain visible, in milliseconds.")
                    .worldRestart()
                    .defineInRange("scanStayDuration", Settings.scanStayDuration, 1000, 60000 * 5);

            builder.pop();

            builder.push("blocks");

            ignoredBlocks = builder
                    .comment("""
                            Registry names of blocks that should be ignored.
                            Blocks in this list will be excluded from the default ore list based on the forge:ores
                            tag and it will be impossible to tune the entity module to this block.""")
                    .worldRestart()
                    .defineList("ignoredBlocks", serializeSet(Settings.ignoredBlocks, ResourceLocation::toString), Settings::validateResourceLocation);

            ignoredBlockTags = builder
                    .comment("""
                            Tag names of block tags that should be ignored.
                            Blocks matching a tag in this list will be excluded from the default ore list based on the
                            forge:ores tag and it will be impossible to tune the entity module to this block.""")
                    .worldRestart()
                    .defineList("ignoredBlockTags", serializeSet(Settings.ignoredBlockTags, ResourceLocation::toString), Settings::validateResourceLocation);

            builder.pop();

            builder.push("ores");

            commonOreBlocks = builder
                    .comment("Registry names of blocks considered 'common ores', requiring the common ore scanner module.")
                    .worldRestart()
                    .defineList("commonOreBlocks", serializeSet(Settings.commonOreBlocks, ResourceLocation::toString), Settings::validateResourceLocation);

            commonOreBlockTags = builder
                    .comment("Block tags of blocks considered 'common ores', requiring the common ore scanner module.")
                    .worldRestart()
                    .defineList("commonOreBlockTags", serializeSet(Settings.commonOreBlockTags, ResourceLocation::toString), Settings::validateResourceLocation);

            rareOreBlocks = builder
                    .comment("Registry names of blocks considered 'rare ores', requiring the common ore scanner module.")
                    .worldRestart()
                    .defineList("rareOreBlocks", serializeSet(Settings.rareOreBlocks, ResourceLocation::toString), Settings::validateResourceLocation);

            rareOreBlockTags = builder
                    .comment("""
                            Block tags of blocks considered 'rare ores', requiring the common ore scanner module.
                            Any block with the forge:ores tag is implicitly in this list, unless the block also
                            matches an ignored or common ore block tag, or is an ignored or common block.""")
                    .worldRestart()
                    .defineList("rareOreBlockTags", serializeSet(Settings.rareOreBlockTags, ResourceLocation::toString), Settings::validateResourceLocation);

            builder.pop();

            builder.push("fluids");

            ignoredFluidTags = builder
                    .comment("Fluid tags of fluids that should be ignored.")
                    .worldRestart()
                    .defineList("ignoredFluidTags", serializeSet(Settings.ignoredFluidTags, ResourceLocation::toString), Settings::validateResourceLocation);

            builder.pop();

            builder.push("structures");

            structures = builder
                    .comment("The list of structures the structure module scans for.")
                    .worldRestart()
                    .defineList("structures", serializeSet(Settings.structures, v -> v), o -> GameData.getStructureMap().containsKey(o));

            builder.pop();
        }
    }

    private static final class ClientSettings {
        public ForgeConfigSpec.ConfigValue<List<? extends String>> blockColors;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> blockTagColors;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> fluidColors;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> fluidTagColors;

        public ClientSettings(final ForgeConfigSpec.Builder builder) {
            blockColors = builder
                    .comment("""
                            The colors for blocks used when rendering their result bounding box
                            by block name. Each entry must be a key-value pair separated by a `=`,
                            with the key being the tag name and the value being the hexadecimal
                            RGB value of the color.""")
                    .worldRestart()
                    .defineList("blockColors", serializeMap(Settings.blockColors, ResourceLocation::toString, c -> "0x" + Integer.toHexString(c)),
                            Settings::validateResourceLocationMapEntry);
            blockTagColors = builder
                    .comment("The colors for blocks used when rendering their result bounding box\n" +
                             "by block tag. See `blockColors` for format entries have to be in.")
                    .worldRestart()
                    .defineList("blockTagColors", serializeMap(Settings.blockTagColors, ResourceLocation::toString, c -> "0x" + Integer.toHexString(c)),
                            Settings::validateResourceLocationMapEntry);

            fluidColors = builder
                    .comment("The colors for fluids used when rendering their result bounding box\n" +
                             "by fluid name. See `blockColors` for format entries have to be in.")
                    .worldRestart()
                    .defineList("fluidColors", serializeMap(Settings.fluidColors, ResourceLocation::toString, c -> "0x" + Integer.toHexString(c)),
                            Settings::validateResourceLocationMapEntry);
            fluidTagColors = builder
                    .comment("The colors for fluids used when rendering their result bounding box\n" +
                             "by fluid tag. See `blockColors` for format entries have to be in.")
                    .worldRestart()
                    .defineList("fluidTagColors", serializeMap(Settings.fluidTagColors, ResourceLocation::toString, c -> "0x" + Integer.toHexString(c)),
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
            LOGGER.error(e);
            return false;
        }
    }

    private static <T> List<? extends String> serializeSet(final Set<T> set, final Function<T, String> serializer) {
        final ArrayList<String> result = new ArrayList<>();
        for (final T v : set) {
            final String serialized = serializer.apply(v);
            if (serialized != null) {
                result.add(serialized);
            }
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
        map.forEach((k, v) -> {
            final String serializedKey = keySerializer.apply(k);
            final String serializedValue = valueSerializer.apply(v);
            if (serializedKey != null && serializedValue != null) {
                result.add(serializedKey + "=" + serializedValue);
            }
        });
        return result;
    }

    private static <K, V> void deserializeMap(final Map<K, V> map, final List<? extends String> list, final Function<String, K> keyDeserializer, final Function<String, V> valueDeserializer) {
        map.clear();
        for (final String v : list) {
            final String[] keyValue = v.split("=", 2);
            if (keyValue.length != 2) {
                LOGGER.error("Failed parsing setting value [{}].", v);
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
