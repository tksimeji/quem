package com.tksimeji.quem.ui;

import com.tksimeji.visualkit.api.Element;
import com.tksimeji.visualkit.api.Handler;
import com.tksimeji.visualkit.element.VisualkitElement;
import com.tksimeji.quem.Party;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PartyCreateUI extends PartyUI {
    @Element(22)
    private final VisualkitElement create = VisualkitElement.create(Material.CRAFTING_TABLE)
            .title(Language.translate("ui.party_create.create", player).color(NamedTextColor.GREEN))
            .lore(Language.translate("ui.party_create.create.description", player).color(NamedTextColor.GRAY));

    @Element(49)
    private final VisualkitElement exit = VisualkitElement.create(Material.ARROW)
            .title(Language.translate("ui.close", player).color(NamedTextColor.RED))
            .lore(Language.translate("ui.close.description", player).color(NamedTextColor.GRAY));

    public PartyCreateUI(@NotNull Player player) {
        super(player, null);
    }

    @Handler(slot = 22)
    public void onCreate() {
        new Party(player);
        new PartyMenuUI(player);
    }

    @Handler(slot = 49)
    public void onExit() {
        close();
    }
}
