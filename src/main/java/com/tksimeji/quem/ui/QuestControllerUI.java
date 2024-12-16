package com.tksimeji.quem.ui;

import com.tksimeji.visualkit.ChestUI;
import com.tksimeji.visualkit.api.Element;
import com.tksimeji.visualkit.api.Handler;
import com.tksimeji.visualkit.api.Size;
import com.tksimeji.visualkit.element.VisualkitElement;
import com.tksimeji.quem.Party;
import com.tksimeji.quem.Quest;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class QuestControllerUI extends ChestUI {
    private final @NotNull Quest quest;

    @Element(11)
    private final ItemStack icon;

    @Element(13)
    private final VisualkitElement quit;

    @Element(15)
    private final VisualkitElement exit = VisualkitElement.create(Material.ARROW)
            .title(Language.translate("ui.close", player).color(NamedTextColor.GREEN))
            .lore(Language.translate("ui.close.description", player).color(NamedTextColor.GRAY));

    public QuestControllerUI(@NotNull Player player) {
        super(player);

        quest = Optional.ofNullable(Quest.getInstance(player)).orElseThrow(IllegalArgumentException::new);

        icon = quest.getType().getIcon();

        quit = VisualkitElement.create(Material.TNT_MINECART)
                .title(Language.translate(player == quest.getParty().getLeader() ? "ui.quest_controller.disband" : "ui.quest_controller.quit", player).color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
                .lore(Language.translate(player == quest.getParty().getLeader() ? "ui.quest_controller.disband.description" : "ui.quest_controller.quit.description", player).color(NamedTextColor.GRAY));
    }

    @Override
    public @NotNull Component title() {
        return Language.translate("ui.quest.title", player);
    }

    @Override
    public @NotNull Size size() {
        return Size.SIZE_27;
    }

    @Handler(slot = 13)
    public void onLeave() {
        new ConfirmUI(player, new BukkitRunnable() {
            @Override
            public void run() {
                Party party = QuestControllerUI.this.quest.getParty();

                if (player == party.getLeader()) {
                    party.disband();
                } else {
                    party.removeMember(player);
                }
            }
        }, null);
    }

    @Handler(slot = 15)
    public void onExit() {
        close();
    }
}
