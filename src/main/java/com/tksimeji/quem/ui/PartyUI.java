package com.tksimeji.quem.ui;

import com.tksimeji.visualkit.ChestUI;
import com.tksimeji.visualkit.api.Size;
import com.tksimeji.quem.Party;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PartyUI extends ChestUI {
    protected final Party party;

    public PartyUI(@NotNull Player player, Party party) {
        super(player);
        this.party = party;
    }

    public @NotNull Party party() {
        return party;
    }

    @Override
    public @NotNull Component title() {
        return Language.translate("ui.party.title", player);
    }

    @Override
    public @NotNull Size size() {
        return Size.SIZE_54;
    }
}
