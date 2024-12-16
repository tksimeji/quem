package com.tksimeji.quem.ui;

import com.tksimeji.visualkit.ChestUI;
import com.tksimeji.visualkit.api.Element;
import com.tksimeji.visualkit.api.Handler;
import com.tksimeji.visualkit.api.Mouse;
import com.tksimeji.visualkit.api.Size;
import com.tksimeji.visualkit.element.VisualkitElement;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ConfirmUI extends ChestUI {
    @Element(11)
    private final VisualkitElement accept = VisualkitElement.create(Material.GREEN_TERRACOTTA)
            .title(Language.translate("ui.confirm.accept", player).color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));

    @Element(15)
    private final VisualkitElement reject = VisualkitElement.create(Material.RED_TERRACOTTA)
            .title(Language.translate("ui.confirm.reject", player).color(NamedTextColor.RED).decorate(TextDecoration.BOLD));

    private final BukkitRunnable acceptRunnable;
    private final BukkitRunnable rejectRunnable;

    public ConfirmUI(@NotNull Player player, @Nullable BukkitRunnable onAccept, @Nullable BukkitRunnable onReject) {
        super(player);

        acceptRunnable = onAccept;
        rejectRunnable = onReject;
    }

    @Override
    public @NotNull Component title() {
        return Language.translate("ui.confirm.title", player);
    }

    @Override
    public @NotNull Size size() {
        return Size.SIZE_27;
    }

    @Handler(slot = 11, mouse = Mouse.LEFT)
    public void onAccept() {
        if (acceptRunnable == null) {
            return;
        }

        acceptRunnable.run();
    }

    @Handler(slot = 15, mouse = Mouse.LEFT)
    public void onReject() {
        if (rejectRunnable == null) {
            return;
        }

        rejectRunnable.run();
    }

    @Handler(slot = {11, 15})
    public void onInteract() {
        close();
    }
}
