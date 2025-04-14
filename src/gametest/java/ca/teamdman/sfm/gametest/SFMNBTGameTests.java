package ca.teamdman.sfm.gametest;

import ca.teamdman.sfm.SFM;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.gametest.GameTestHolder;

import java.util.Arrays;

@SuppressWarnings({
        "RedundantSuppression",
        "DataFlowIssue",
        "deprecation",
        "OptionalGetWithoutIsPresent",
        "DuplicatedCode",
        "ArraysAsListWithZeroOrOneArgument"
})
@GameTestHolder(SFM.MOD_ID)
public class SFMNBTGameTests extends SFMGameTestBase {
    @GameTest(template = "3x2x1")
    public static void nbt_damage(GameTestHelper helper) {
        var pickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
        var pickaxeWithDamage = pickaxe.copy();
        pickaxeWithDamage.setDamageValue(12);

        new LeftRightManagerTest(helper)
                .setProgram("""
                                    EVERY 20 TICKS DO
                                      INPUT WITH NBT {Damage: 12} FROM left
                                      OUTPUT TO right
                                    END
                                    """)
                .preContents("left", Arrays.asList(
                        pickaxe.copy(),
                        pickaxeWithDamage.copy()
                ))
                .postContents("left", Arrays.asList(
                        pickaxe
                ))
                .postContents("right", Arrays.asList(
                        pickaxeWithDamage
                ))
                .run();
    }

    @GameTest(template = "3x2x1")
    public static void nbt_name(GameTestHelper helper) {
        var stick = new ItemStack(Items.STICK, 64);
        var stickWithName = stick.copy();
        stickWithName.setHoverName(Component.literal("brah"));

        new LeftRightManagerTest(helper)
                .setProgram("""
                                    EVERY 20 TICKS DO
                                      INPUT WITH NBT {display:{Name:'[{"text":"bruh"}]'}} FROM left
                                      OUTPUT TO right
                                    END
                                    """)
                .preContents("left", Arrays.asList(
                        stickWithName.copy(),
                        stick.copy()
                ))
                .postContents("left", Arrays.asList(
                        stick
                ))
                .postContents("right", Arrays.asList(
                        stickWithName
                ))
                .run();
    }
}
