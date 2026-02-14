package ca.teamdman.sfm.gametest.tests.cable;

import ca.teamdman.sfm.common.blockentity.IFacadeBlockEntity;
import ca.teamdman.sfm.common.facade.FacadeData;
import ca.teamdman.sfm.common.facade.FacadeTextureMode;
import ca.teamdman.sfm.common.registry.registration.SFMBlocks;
import ca.teamdman.sfm.gametest.SFMGameTestDefinition;
import ca.teamdman.sfm.gametest.SFMGameTestGenerator;
import ca.teamdman.sfm.gametest.SFMGameTestGeneratorBase;
import ca.teamdman.sfm.gametest.SFMGameTestHelper;
import ca.teamdman.sfm.gametest.SFMGameTestMethodHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SFMGameTestGenerator
public class ToughCableDestroyEventGameTestGenerator extends SFMGameTestGeneratorBase {

    private record Scenario(
            String name,
            Supplier<Block> blockSupplier,
            Optional<BlockState> facadeState,
            boolean expectedAllowDestroy
    ) {
    }

    private static final List<Scenario> SCENARIOS = List.of(
            new Scenario(
                    "destroy_event_tough_cable_facaded_as_bedrock_denied",
                    SFMBlocks.TOUGH_CABLE_FACADE::get,
                    Optional.of(Blocks.BEDROCK.defaultBlockState()),
                    false
            ),
            new Scenario(
                    "destroy_event_tough_cable_facaded_as_obsidian_allowed",
                    SFMBlocks.TOUGH_CABLE_FACADE::get,
                    Optional.of(Blocks.OBSIDIAN.defaultBlockState()),
                    true
            ),
            new Scenario(
                    "destroy_event_tough_fancy_cable_facaded_as_bedrock_denied",
                    SFMBlocks.TOUGH_FANCY_CABLE_FACADE::get,
                    Optional.of(Blocks.BEDROCK.defaultBlockState()),
                    false
            ),
            new Scenario(
                    "destroy_event_tough_fancy_cable_facaded_as_obsidian_allowed",
                    SFMBlocks.TOUGH_FANCY_CABLE_FACADE::get,
                    Optional.of(Blocks.OBSIDIAN.defaultBlockState()),
                    true
            ),
                new Scenario(
                    "destroy_event_cable_facaded_as_bedrock_allowed",
                    SFMBlocks.CABLE_FACADE::get,
                    Optional.of(Blocks.BEDROCK.defaultBlockState()),
                    true
                ),
                new Scenario(
                    "destroy_event_cable_facaded_as_obsidian_allowed",
                    SFMBlocks.CABLE_FACADE::get,
                    Optional.of(Blocks.OBSIDIAN.defaultBlockState()),
                    true
                ),
                new Scenario(
                    "destroy_event_fancy_cable_facaded_as_bedrock_allowed",
                    SFMBlocks.FANCY_CABLE_FACADE::get,
                    Optional.of(Blocks.BEDROCK.defaultBlockState()),
                    true
                ),
                new Scenario(
                    "destroy_event_fancy_cable_facaded_as_obsidian_allowed",
                    SFMBlocks.FANCY_CABLE_FACADE::get,
                    Optional.of(Blocks.OBSIDIAN.defaultBlockState()),
                    true
                ),
            new Scenario(
                    "destroy_event_tough_cable_unfacaded_allowed",
                    SFMBlocks.TOUGH_CABLE::get,
                    Optional.empty(),
                    true
            ),
            new Scenario(
                    "destroy_event_cable_unfacaded_allowed",
                    SFMBlocks.CABLE::get,
                    Optional.empty(),
                    true
            )
    );

    @Override
    public void generateTests(Consumer<SFMGameTestDefinition> testConsumer) {

        for (Scenario scenario : SCENARIOS) {
            testConsumer.accept(new ToughCableDestroyEventScenarioGameTest(scenario));
        }
    }

    private static class ToughCableDestroyEventScenarioGameTest extends SFMGameTestDefinition {
        private final Scenario scenario;

        private ToughCableDestroyEventScenarioGameTest(Scenario scenario) {

            this.scenario = scenario;
        }

        @Override
        public String template() {

            return "1x4x1";
        }

        @Override
        public String testName() {

            return scenario.name;
        }

        @Override
        public int maxTicks() {

            return 80;
        }

        @Override
        public void run(SFMGameTestHelper helper) {

            if (helper.getLevel().getDifficulty() == Difficulty.PEACEFUL) {
                helper.fail("Difficulty must not be PEACEFUL to run wither destroy-event scenario");
                return;
            }

            BlockPos localPos = new BlockPos(0, 1, 0);
            BlockPos absolute = helper.absolutePos(localPos);
            helper.getLevel().setBlock(absolute, scenario.blockSupplier.get().defaultBlockState(), 3);
            if (!applyFacadeIfPresent(helper, absolute)) {
                return;
            }

            Vec3 witherSpawn = helper.absoluteVec(new Vec3(0.5, 3.0, 0.5));
            WitherBoss wither = EntityType.WITHER.create(helper.getLevel());
            if (wither == null) {
                helper.fail("Failed to create wither entity");
                return;
            }
            wither.setNoAi(true);
            wither.moveTo(witherSpawn.x, witherSpawn.y, witherSpawn.z, 0, 0);
            helper.getLevel().addFreshEntity(wither);

            BlockState state = helper.getLevel().getBlockState(absolute);
            boolean canEntityDestroy = state.canEntityDestroy(helper.getLevel(), absolute, wither);
                boolean eventAllowsDestroy = ForgeEventFactory.onEntityDestroyBlock(wither, absolute, state);

            SFMGameTestMethodHelpers.assertTrue(
                    eventAllowsDestroy == scenario.expectedAllowDestroy,
                    "Scenario '" + scenario.name + "' had unexpected onEntityDestroyBlock result: "
                            + "expected allowDestroy=" + scenario.expectedAllowDestroy
                            + ", actual allowDestroy=" + eventAllowsDestroy
                            + ", canEntityDestroy=" + canEntityDestroy
            );

            helper.runAfterDelay(60, () -> {
                wither.discard();
                helper.succeed();
            });
        }

        private boolean applyFacadeIfPresent(
                SFMGameTestHelper helper,
                BlockPos absolute
        ) {

            if (scenario.facadeState.isEmpty()) {
                return true;
            }

            var be = helper.getLevel().getBlockEntity(absolute);
            if (be instanceof IFacadeBlockEntity facade) {
                facade.updateFacadeData(new FacadeData(
                        scenario.facadeState.get(),
                        Direction.NORTH,
                        FacadeTextureMode.FILL
                ));
                return true;
            }

            helper.fail("Block entity at test position was not a facade BE");
            return false;
        }
    }
}
