package ca.teamdman.sfm.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class SFMComponentUtils {
    public static MutableComponent substring(
            Component component,
            int start,
            int end
    ) {

        var rtn = Component.empty();
        AtomicInteger seen = new AtomicInteger(0);
        component.visit(
                (style, content) -> {
                    int contentStart = Math.max(start - seen.get(), 0);
                    int contentEnd = Math.min(end - seen.get(), content.length());

                    if (contentStart < contentEnd) {
                        rtn.append(Component.literal(content.substring(contentStart, contentEnd)).withStyle(style));
                    }
                    seen.addAndGet(content.length());
                    return Optional.empty();
                },
                Style.EMPTY
        );
        return rtn;
    }

    public static int length(
            Component component
    ) {
        AtomicInteger seen = new AtomicInteger(0);
        component.visit(content -> {
            seen.addAndGet(content.length());
            return Optional.empty();
        });
        return seen.get();
    }

}
