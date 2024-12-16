package com.tksimeji.quem.ui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public final class CLI {
    public static final @NotNull String SEPARATOR = "===================================================";
    public static final @NotNull String SHORT_SEPARATOR = "-----";

    public static @NotNull Component info = Component.text()
            .append(Component.text("[").color(NamedTextColor.GRAY))
            .append(Component.text("📝").color(NamedTextColor.BLUE))
            .append(Component.text("]").color(NamedTextColor.GRAY))
            .appendSpace()
            .build();

    public static @NotNull Component successful = Component.text()
            .append(Component.text("[").color(NamedTextColor.GRAY))
            .append(Component.text("✔").color(NamedTextColor.GREEN))
            .append(Component.text("]").color(NamedTextColor.GRAY))
            .appendSpace()
            .build();

    public static @NotNull Component failed = Component.text()
            .append(Component.text("[").color(NamedTextColor.GRAY))
            .append(Component.text("✘").color(NamedTextColor.RED))
            .append(Component.text("]").color(NamedTextColor.GRAY))
            .appendSpace()
            .build();

    public static @NotNull Component warn = Component.text()
            .append(Component.text("[").color(NamedTextColor.GRAY))
            .append(Component.text("⚠").color(NamedTextColor.YELLOW))
            .append(Component.text("]").color(NamedTextColor.GRAY))
            .appendSpace()
            .build();

    public static int[] around(int i, int min, int max) {
        int start = Math.max(i - 2, min);
        int end = Math.min(i + 2, max);

        int length = end - start + 1;
        int[] around = new int[length];

        for (int j = 0; j < length; j ++) {
            around[j] = start + j;
        }

        return around;
    }
}
