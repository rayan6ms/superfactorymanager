package ca.teamdman.sfm.client.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.text_editor.ISFMTextEditorRegistration;
import ca.teamdman.sfm.client.text_editor.SFMTextEditScreenV1Registration;
import ca.teamdman.sfm.client.text_editor.SFMTextEditScreenV2Registration;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.core.Registry;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SFMTextEditors {
    public static final ResourceLocation REGISTRY_ID = SFMResourceLocation.fromSFMPath("text_editor");
    private static final DeferredRegister<ISFMTextEditorRegistration> REGISTERER = DeferredRegister.create(
            REGISTRY_ID,
            SFM.MOD_ID
    );
    private static final Registry<ISFMTextEditorRegistration> REGISTRY = REGISTERER.makeRegistry(
            registryBuilder -> {});

    public static final DeferredHolder<ISFMTextEditorRegistration, SFMTextEditScreenV1Registration> V1 = REGISTERER.register(
            "v1",
            SFMTextEditScreenV1Registration::new
    );

    public static final DeferredHolder<ISFMTextEditorRegistration, SFMTextEditScreenV2Registration>  V2 = REGISTERER.register(
            "v2",
            SFMTextEditScreenV2Registration::new
    );

    public static void register(IEventBus bus) {
        REGISTERER.register(bus);
    }

    @MCVersionDependentBehaviour
    public static Registry<ISFMTextEditorRegistration> registry() {
        return REGISTRY;
    }
}
