package com.tksimeji.quem.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public final class ComponentUtility {
    public static int length(@NotNull Component component) {
        return ComponentUtility.serialize(component).length();
    }

    public static @NotNull Component empty() {
        TextComponent.Builder empty = Component.text();
        Arrays.stream(TextDecoration.values()).forEach((decoration) -> empty.decoration(decoration, false));
        return empty.build().color(NamedTextColor.WHITE);
    }

    public static @NotNull Component connect(@NotNull List<Component> components) {
        TextComponent.Builder builder = Component.text();

        for (int i = 0; i < components.size(); i ++) {
            builder.append(components.get(i));

            if (i + 1 < components.size()) {
                builder.appendNewline();
            }
        }

        return builder.build();
    }

    public static @NotNull String serialize(@NotNull Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component).replace('§', '&');
    }

    public static @NotNull Component deserialize(@NotNull String string) {
        return LegacyComponentSerializer.legacySection().deserialize(string.replace('&', '§'));
    }
}
