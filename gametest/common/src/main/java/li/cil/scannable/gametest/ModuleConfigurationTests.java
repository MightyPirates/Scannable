/* SPDX-License-Identifier: MIT */

package li.cil.scannable.gametest;

import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.item.ConfigurableBlockScannerModuleItem;
import li.cil.scannable.common.item.ConfigurableEntityScannerModuleItem;
import li.cil.scannable.common.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static li.cil.scannable.gametest.TestSupport.assertEquals;
import static li.cil.scannable.gametest.TestSupport.assertTrue;

public final class ModuleConfigurationTests {
    private static final BlockPos TARGET = new BlockPos(1, 1, 1);

    private static final List<Block> FILLER_BLOCKS = List.of(
        Blocks.IRON_ORE,
        Blocks.GOLD_ORE,
        Blocks.DIAMOND_ORE,
        Blocks.COAL_ORE,
        Blocks.COPPER_ORE,
        Blocks.EMERALD_ORE
    );

    // --------------------------------------------------------------------- //

    public static void blockModuleRecordsUsedBlock(final GameTestHelper helper) {
        final ItemStack module = new ItemStack(Items.BLOCK_MODULE.get());

        useOnBlock(helper, module, Blocks.IRON_ORE);

        final List<Block> blocks = ConfigurableBlockScannerModuleItem.getBlocks(module);
        assertEquals(helper, "configured block count", 1, blocks.size());
        assertTrue(helper, "the used block should be configured, got " + blocks,
            blocks.contains(Blocks.IRON_ORE));

        helper.succeed();
    }

    public static void blockModuleIgnoresDuplicates(final GameTestHelper helper) {
        final ItemStack module = new ItemStack(Items.BLOCK_MODULE.get());

        useOnBlock(helper, module, Blocks.IRON_ORE);
        useOnBlock(helper, module, Blocks.IRON_ORE);

        assertEquals(helper, "configuring the same block twice should record it once",
            1, ConfigurableBlockScannerModuleItem.getBlocks(module).size());

        helper.succeed();
    }

    public static void blockModuleStopsAtSlotLimit(final GameTestHelper helper) {
        final ItemStack module = new ItemStack(Items.BLOCK_MODULE.get());
        assertTrue(helper, "test needs more filler blocks than the module has slots",
            FILLER_BLOCKS.size() > Constants.CONFIGURABLE_MODULE_SLOTS);

        for (final Block block : FILLER_BLOCKS) {
            useOnBlock(helper, module, block);
        }

        final List<Block> blocks = ConfigurableBlockScannerModuleItem.getBlocks(module);
        assertEquals(helper, "configured blocks should stop at the slot limit",
            Constants.CONFIGURABLE_MODULE_SLOTS, blocks.size());
        assertTrue(helper, "the block past the limit should not be configured",
            !blocks.contains(FILLER_BLOCKS.get(FILLER_BLOCKS.size() - 1)));

        helper.succeed();
    }

    public static void blockModuleRejectsIgnoredBlock(final GameTestHelper helper) {
        final ItemStack module = new ItemStack(Items.BLOCK_MODULE.get());

        useOnBlock(helper, module, Blocks.COMMAND_BLOCK);

        assertEquals(helper, "an ignored block should not be configured",
            0, ConfigurableBlockScannerModuleItem.getBlocks(module).size());

        helper.succeed();
    }

    public static void entityModuleRecordsInteractedEntity(final GameTestHelper helper) {
        final ItemStack module = new ItemStack(Items.ENTITY_MODULE.get());

        interactWith(helper, module, EntityType.COW);

        final List<EntityType<?>> types = ConfigurableEntityScannerModuleItem.getEntityTypes(module);
        assertEquals(helper, "configured entity count", 1, types.size());
        assertTrue(helper, "the interacted entity type should be configured, got " + types,
            types.contains(EntityType.COW));

        helper.succeed();
    }

    public static void entityModuleIgnoresDuplicates(final GameTestHelper helper) {
        final ItemStack module = new ItemStack(Items.ENTITY_MODULE.get());

        interactWith(helper, module, EntityType.COW);
        interactWith(helper, module, EntityType.COW);

        assertEquals(helper, "configuring the same entity type twice should record it once",
            1, ConfigurableEntityScannerModuleItem.getEntityTypes(module).size());

        helper.succeed();
    }

    // --------------------------------------------------------------------- //

    private static void useOnBlock(final GameTestHelper helper, final ItemStack module, final Block block) {
        helper.setBlock(TARGET, block);

        final Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, module);

        final BlockPos absolute = helper.absolutePos(TARGET);
        final BlockHitResult hit = new BlockHitResult(
            Vec3.atCenterOf(absolute).add(0, 0.5, 0), Direction.UP, absolute, false);

        module.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
    }

    private static void interactWith(final GameTestHelper helper, final ItemStack module, final EntityType<? extends LivingEntity> entityType) {
        final LivingEntity target = helper.spawn(entityType, TARGET);

        final Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, module);

        module.interactLivingEntity(player, target, InteractionHand.MAIN_HAND);

        target.discard();
    }

    private ModuleConfigurationTests() {
    }
}
