package ca.teamdman.sfm.common.handler;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.config.SFMConfig;
import ca.teamdman.sfm.common.enchantment.SFMEnchantmentCollection;
import ca.teamdman.sfm.common.enchantment.SFMEnchantmentCollectionKind;
import ca.teamdman.sfm.common.enchantment.SFMEnchantmentEntry;
import ca.teamdman.sfm.common.item.FormItem;
import ca.teamdman.sfm.common.recipe.PrintingPressRecipe;
import ca.teamdman.sfm.common.registry.SFMBlockTags;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.registry.SFMRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FallingAnvilHandler {

    @SubscribeEvent
    public static void onLeave(EntityLeaveLevelEvent event) {

        // Only proceed if the entity was a falling block
        if (!(event.getEntity() instanceof FallingBlockEntity fbe)) {
            return;
        }

        // Only proceed if the falling block was an anvil
        if (!(fbe.getBlockState().getBlock() instanceof AnvilBlock)) {
            return;
        }

        // Determine where the anvil landed
        var landingPosition = fbe.blockPosition();

        // Get the level
        Level level = event.getLevel();

        // Do not proceed if the server is shutting down
        // https://github.com/TeamDman/SuperFactoryManager/issues/114
        if (!level.isLoaded(landingPosition.below())) {
            return;
        }

        // Get the block that was landed on
        Block block = level.getBlockState(landingPosition.below()).getBlock();

        // Check if the landed-on block can be turned into a printing press form
        boolean tryFormCreation = SFMBlockTags.blockHasTag(block, SFMBlockTags.ANVIL_PRINTING_PRESS_FORMING);
        if (tryFormCreation) {
            handlePrintingPressFormCreation(level, landingPosition);
            return;
        }

        // Check if the landed-on block can be used for anvil-disenchanting
        boolean tryCrushing = SFMBlockTags.blockHasTag(block, SFMBlockTags.ANVIL_DISENCHANTING);
        if (tryCrushing) {
            handleCrushing(level, landingPosition);
        }
    }

    private static void handleCrushing(
            Level level,
            BlockPos landPosition
    ) {

        // Gather the item entities in the landing area
        List<ItemEntity> items = new ArrayList<>();
        for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, new AABB(landPosition))) {
            if (itemEntity.isAlive() && !itemEntity.getItem().isEmpty()) {
                items.add(itemEntity);
            }
        }

        // Crush enchanted books
        crushEnchantedBooksIntoXpShards(items);

        // Disenchant items
        removeEnchantmentsFromItems(level, landPosition, items);
    }

    private static void removeEnchantmentsFromItems(
            Level level,
            BlockPos landPosition,
            List<ItemEntity> items
    ) {

        // Identify book entities
        List<ItemEntity> bookItemEntities = new ArrayList<>();
        List<ItemEntity> nonBookItemEntities = new ArrayList<>();
        for (ItemEntity item : items) {
            if (item.getItem().is(Items.BOOK)) {
                bookItemEntities.add(item);
            } else {
                nonBookItemEntities.add(item);
            }
        }

        // Compute the total number of books available
        int booksAvailable = 0;
        for (ItemEntity itemEntity : bookItemEntities) {
            int count = itemEntity.getItem().getCount();
            booksAvailable += count;
        }

        // Rip enchantments off of the item entities
        for (ItemEntity itemEntity : nonBookItemEntities) {

            // Get the stack from the item entity
            ItemStack stack = itemEntity.getItem();

            // Get the enchantments off the stack
            SFMEnchantmentCollection enchantments = SFMEnchantmentCollection.fromItemStack(
                    stack,
                    SFMEnchantmentCollectionKind.EnchantedLikeATool
            );

            // Skip if no enchantments
            if (enchantments.isEmpty()) {
                continue;
            }

            // Determine how large the stack is
            int stackSize = stack.getCount();

            // Rip enchantments off the stack until we run out of books
            var iter = enchantments.iterator();

            // Todo: strip partial stacks by decrementing instead of despawning
            while (iter.hasNext() && booksAvailable >= stackSize) {

                // Get the next enchantment to transfer
                var enchantment = iter.next();

                // Create an enchanted book
                ItemStack enchantedBookToSpawn = enchantment.createEnchantedBook();
                enchantedBookToSpawn.setCount(stackSize);

                // Spawn the book in the world
                level.addFreshEntity(new ItemEntity(
                        level,
                        landPosition.getX(),
                        landPosition.getY(),
                        landPosition.getZ(),
                        enchantedBookToSpawn
                ));

                // Remove the enchantment from the collection
                iter.remove();

                // Store the adjusted collection back onto the stack
                enchantments.write(stack, SFMEnchantmentCollectionKind.EnchantedLikeATool);

                // Track book consumption
                booksAvailable -= stackSize;
            }
        }

        // Despawn the books
        for (ItemEntity bookEntity : bookItemEntities) {
            bookEntity.kill();
        }

        // Respawn any remaining books
        while (booksAvailable > 0) {
            int toSpawn = Math.min(booksAvailable, 64);
            level.addFreshEntity(new ItemEntity(
                    level,
                    landPosition.getX(),
                    landPosition.getY(),
                    landPosition.getZ(),
                    new ItemStack(Items.BOOK, toSpawn)
            ));
            booksAvailable -= toSpawn;
        }
    }

    private static void crushEnchantedBooksIntoXpShards(List<ItemEntity> items) {

        // For each item
        for (ItemEntity itemEntity : items) {

            // Only proceed if the item is an enchanted book
            if (!itemEntity.getItem().is(Items.ENCHANTED_BOOK)) {
                continue;
            }

            // Get the book stack
            ItemStack stack = itemEntity.getItem();

            // Get the enchantments stored in the book
            SFMEnchantmentCollection enchantments = SFMEnchantmentCollection.fromItemStack(
                    stack,
                    SFMEnchantmentCollectionKind.HoldingLikeABook
            );

            // Determine how many shards the book is worth
            long shardsForEnchantments = getShardCountForEnchantments(enchantments);

            // Determine the total number of shards based on the stack size
            long shardsToSpawn = (long) stack.getCount() * shardsForEnchantments;

            // Kill the book item
            itemEntity.kill();

            // Spawn the shards
            while (shardsToSpawn > 0) {

                // Determine stack size
                int shardStackSize = (int) Math.min(64, shardsToSpawn);

                // Create the stack
                ItemStack shardStack = new ItemStack(SFMItems.EXPERIENCE_SHARD_ITEM.get(), shardStackSize);

                // Spawn the stack
                itemEntity.spawnAtLocation(shardStack);

                // Decrement count
                shardsToSpawn -= 64;
            }
        }
    }

    private static void handlePrintingPressFormCreation(
            Level level,
            BlockPos landPosition
    ) {

        List<PrintingPressRecipe> recipes = level
                .getRecipeManager()
                .getAllRecipesFor(SFMRecipeTypes.PRINTING_PRESS.get());
        List<ItemEntity> items = new ArrayList<>();
        for (ItemEntity e : level.getEntitiesOfClass(ItemEntity.class, new AABB(landPosition))) {
            if (e.isAlive() && !e.getItem().isEmpty()) {
                items.add(e);
            }
        }
        boolean didForm = false;

        for (ItemEntity item : items) {
            for (PrintingPressRecipe recipe : recipes) {
                // check if the item can be turned into a form
                if (recipe.form().test(item.getItem())) {
                    didForm = true;
                    item.setItem(FormItem.createFormFromReference(item.getItem()));
                    break;
                }
            }
        }
        if (didForm) {
            level.setBlockAndUpdate(landPosition.below(), Blocks.AIR.defaultBlockState());
        }
    }

    private static long getShardCountForEnchantments(SFMEnchantmentCollection enchantments) {

        return switch (SFMConfig.SERVER_CONFIG.levelsToShards.get()) {
            case JustOne -> 1;
            case EachOne -> enchantments.size();
            case SumLevels -> {
                int sum = 0;
                for (SFMEnchantmentEntry enchantment : enchantments) {
                    sum += enchantment.level();
                }
                yield sum;
            }
            case SumLevelsScaledExponentially -> {
                int sum = 0;
                for (SFMEnchantmentEntry enchantment : enchantments) {
                    int incr = 1 << Math.max(0, enchantment.level() - 1);
                    if (sum + incr > 0) {
                        sum += incr;
                    } else {
                        sum = Integer.MAX_VALUE; // lol
                    }
                }
                yield sum;
            }
        };
    }

}
