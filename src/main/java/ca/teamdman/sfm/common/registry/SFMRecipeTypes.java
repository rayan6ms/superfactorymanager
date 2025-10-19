package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.recipe.PrintingPressRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SFMRecipeTypes {
    private static final SFMDeferredRegister<RecipeType<?>> RECIPE_TYPES
            = SFMDeferredRegister.createForExistingRegistry(
            SFMWellKnownRegistries.RECIPE_TYPES,
            SFM.MOD_ID
    );

    public static final SFMRegistryObject<RecipeType<PrintingPressRecipe>> PRINTING_PRESS
            = RECIPE_TYPES.register(
            "printing_press",
            () -> RecipeType.simple(SFMResourceLocation.fromSFMPath("printing_press"))
    );

    public static void register(IEventBus bus) {
        RECIPE_TYPES.register(bus);
    }
}
