package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.recipe.PrintingPressRecipe;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;

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
