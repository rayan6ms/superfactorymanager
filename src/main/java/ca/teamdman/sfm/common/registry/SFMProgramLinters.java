package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.compat.SFMModCompat;
import ca.teamdman.sfm.common.program.linting.FlowProgramLinter;
import ca.teamdman.sfm.common.program.linting.IProgramLinter;
import ca.teamdman.sfm.common.program.linting.ResourcesProgramLinter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SFMProgramLinters {
    public static final ResourceLocation REGISTRY_ID = new ResourceLocation(SFM.MOD_ID, "program_linters");
    private static final DeferredRegister<IProgramLinter> TYPES = DeferredRegister.create(
            REGISTRY_ID,
            SFM.MOD_ID
    );
    public static final Registry<IProgramLinter> DEFERRED_TYPES = TYPES.makeRegistry(registryBuilder->{});

    public static final Supplier<IProgramLinter> FLOW = TYPES.register(
            "flow",
            FlowProgramLinter::new
    );

    public static final Supplier<IProgramLinter> RESOURCE = TYPES.register(
            "resources",
            ResourcesProgramLinter::new
    );

    static {
        if (SFMModCompat.isMekanismLoaded()) {
//            SFMMekanismCompat.registerProgramLinters(TYPES);
        }
    }

    public static void register(IEventBus bus) {
        TYPES.register(bus);
    }
}
