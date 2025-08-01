package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.text_editor.ISFMTextEditorRegistration;
import ca.teamdman.sfm.common.text_editor.SFMTextEditScreenRegistration;
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

    public static final RegistryObject<SFMTextEditScreenRegistration> V1 = REGISTERER.register(
            "v1",
            SFMTextEditScreenRegistration::new
    );

    public static void register(IEventBus bus) {
        REGISTERER.register(bus);
    }

    @MCVersionDependentBehaviour
    public static IForgeRegistry<ISFMTextEditorRegistration> registry() {
        return REGISTRY.get();
    }

    /* TODO: add support for new resource types
     * - mekanism heat
     * - botania mana
     * - ars nouveau source
     * - flux plugs
     * - PNC pressure
     * - PNC heat
     * - nature's aura aura
     * - create rotation
     */
}
