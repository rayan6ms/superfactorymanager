package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.net.ServerboundManagerLogDesireUpdatePacket;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.registry.SFMPackets;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SFMScreenChangeHelpers {
    public static void setOrPushScreen(Screen screen) {
        if (Minecraft.getInstance().screen == null) {
            Minecraft
                    .getInstance()
                    .setScreen(screen);
        } else {
            Minecraft
                    .getInstance()
                    .pushGuiLayer(screen);
        }
    }

    public static void popScreen() {
        Minecraft.getInstance().popGuiLayer();
    }

    public static void showLabelGunScreen(
            ItemStack stack,
            InteractionHand hand
    ) {
        setOrPushScreen(new LabelGunScreen(stack, hand));
    }

    public static void showProgramEditScreen(
            ProgramEditScreenOpenContext context
    ) {
        ProgramEditorScreen screen = new ProgramEditorScreen(context);
        setOrPushScreen(screen);
        screen.scrollToTop();
    }

    public static void showTomlEditScreen(
            TomlEditScreenOpenContext context
    ) {
        ProgramEditorScreen screen = new TomlEditScreen(context);
        setOrPushScreen(screen);
        screen.scrollToTop();
    }

    public static void showProgramEditScreen(String initialContent) {
        ProgramEditScreenOpenContext openContext = new ProgramEditScreenOpenContext(
                initialContent,
                LabelPositionHolder.empty(),
                (x) -> {
                }
        );
        showProgramEditScreen(openContext);
    }

    public static void showExampleListScreen(
            String diskProgramString,
            LabelPositionHolder labelPositionHolder,
            Consumer<String> saveCallback
    ) {
        setOrPushScreen(new ExamplesScreen((chosenTemplate, templates) -> {
            ProgramEditorScreen screen = new ExampleEditScreen(new ExampleEditScreenOpenContext(
                    chosenTemplate,
                    diskProgramString,
                    templates,
                    labelPositionHolder,
                    saveCallback
            ));
            setOrPushScreen(screen);
            screen.scrollToTop();
        }));
    }

    public static void showLogsScreen(ManagerContainerMenu menu) {
        LogsScreen screen = new LogsScreen(menu);
        setOrPushScreen(screen);
        screen.scrollToBottom();
        SFMPackets.sendToServer(new ServerboundManagerLogDesireUpdatePacket(
                menu.containerId,
                menu.MANAGER_POSITION,
                true
        ));
    }

    // TODO: copy item id, not just NBT
    // TODO: replace with showing a screen with the data
    public static void showItemInspectorScreen(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            String content = tag.toString();
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.keyboardHandler.setClipboard(content);
            SFM.LOGGER.info("Copied {} characters to clipboard", content.length());
            assert minecraft.player != null;
            minecraft.player.sendSystemMessage(
                    LocalizationKeys.ITEM_INSPECTOR_COPIED_TO_CLIPBOARD.getComponent(
                            Component.literal(String.valueOf(content.length())).withStyle(ChatFormatting.AQUA)
                    )
            );
        }
    }

    public static void showChangelog() {
        String changelog = null;
        var irm = Minecraft.getInstance().getResourceManager();
        Map<ResourceLocation, Resource> found = irm.listResources(
                "template_programs",
                (path) -> path.getPath().endsWith(".sfml") || path.getPath().endsWith(".sfm")
        );
        for (var entry : found.entrySet()) {
            if (entry.getKey().getPath().equals("template_programs/changelog.sfml")) {
                try (var reader = entry.getValue().openAsReader()) {
                    changelog = reader.lines().collect(Collectors.joining("\n"));
                    break;
                } catch (Exception e) {
                    SFM.LOGGER.error("Failed to read changelog", e);
                }
            }
        }
        if (changelog == null) {
            SFM.LOGGER.error("Failed to find changelog");
            return;
        }
        ProgramEditorScreen screen = new ExampleEditScreen(new ExampleEditScreenOpenContext(
                changelog,
                changelog,
                Map.of("changelog.sfml", changelog),
                LabelPositionHolder.empty(),
                newContent -> {
                }
        ));
        setOrPushScreen(screen);
        screen.scrollToTop();
    }
}
