package ca.teamdman.sfm.client.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.text_editor.ISFMTextEditorRegistration;
import ca.teamdman.sfm.client.text_editor.SFMTextEditScreenV1Registration;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class SFMTextEditors {
    public static final ResourceLocation REGISTRY_ID = new ResourceLocation(SFM.MOD_ID, "text_editor");
    private static final DeferredRegister<ISFMTextEditorRegistration> REGISTERER = DeferredRegister.create(
            REGISTRY_ID,
            SFM.MOD_ID
    );
    private static final Supplier<IForgeRegistry<ISFMTextEditorRegistration>> REGISTRY = REGISTERER.makeRegistry(
            () -> new RegistryBuilder<ISFMTextEditorRegistration>().setName(
                    REGISTRY_ID));

    public static final RegistryObject<SFMTextEditScreenV1Registration> V1 = REGISTERER.register(
            "v1",
            SFMTextEditScreenV1Registration::new
    );

    public static void register(IEventBus bus) {
        REGISTERER.register(bus);
    }

    @MCVersionDependentBehaviour
    public static IForgeRegistry<ISFMTextEditorRegistration> registry() {
        return REGISTRY.get();
    }
}
