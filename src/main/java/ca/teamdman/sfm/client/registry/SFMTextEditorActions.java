package ca.teamdman.sfm.client.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.text_editor.action.ITextEditAction;
import ca.teamdman.sfm.client.text_editor.action.SelectAllTextAction;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class SFMTextEditorActions {
    public static final ResourceLocation REGISTRY_ID = new ResourceLocation(SFM.MOD_ID, "text_editor_action");
    private static final DeferredRegister<ITextEditAction> REGISTERER = DeferredRegister.create(
            REGISTRY_ID,
            SFM.MOD_ID
    );
    private static final Supplier<IForgeRegistry<ITextEditAction>> REGISTRY = REGISTERER.makeRegistry(
            () -> new RegistryBuilder<ITextEditAction>().setName(
                    REGISTRY_ID));

    public static final RegistryObject<ITextEditAction> SELECT_ALL_TEXT = REGISTERER.register(
            "select_all_text",
            SelectAllTextAction::new
    );

    public static Stream<ITextEditAction> getTextEditActions() {
        return registry().getValues().stream().sorted((a, b) -> Float.compare(a.priority(), b.priority()));
    }

    public static void register(IEventBus bus) {
        REGISTERER.register(bus);
    }

    @MCVersionDependentBehaviour
    public static IForgeRegistry<ITextEditAction> registry() {
        return REGISTRY.get();
    }
}
