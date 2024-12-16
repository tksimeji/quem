package com.tksimeji.quem.ui;

import com.tksimeji.quem.request.Request;
import com.tksimeji.visualkit.api.Asm;
import com.tksimeji.visualkit.api.Element;
import com.tksimeji.visualkit.api.Handler;
import com.tksimeji.visualkit.element.VisualkitElement;
import com.tksimeji.quem.Party;
import com.tksimeji.quem.request.PartyInvite;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PartyInviteUI extends PartyUI {
    private static final int[] slots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};

    @Element(45)
    private final VisualkitElement previous = VisualkitElement.create(Material.ARROW)
            .title(Language.translate("ui.previous", player).color(NamedTextColor.GREEN));

    @Element(49)
    private final VisualkitElement back = VisualkitElement.create(Material.ARROW)
            .title(Language.translate("ui.back", player).color(NamedTextColor.GREEN))
            .lore(Language.translate("ui.back.description", player).color(NamedTextColor.GRAY));

    @Element(53)
    private final VisualkitElement next = VisualkitElement.create(Material.ARROW)
            .title(Language.translate("ui.next", player).color(NamedTextColor.GREEN));

    private final int page;

    private final List<Player> players;
    private final Map<Integer, Player> map = new HashMap<>();

    public PartyInviteUI(@NotNull Player player) {
        this(player, 0);
    }

    public PartyInviteUI(@NotNull Player player, int page) {
        super(player, Party.getInstance(player));

        this.page = page;
        players = new ArrayList<>(Bukkit.getOnlinePlayers().stream().filter(p -> ! party.isMember(p)).toList());

        if (party == null || party.getLeader() != player) {
            throw new IllegalArgumentException();
        }

        int i = 0;

        for (Player p : players.subList(page * PartyInviteUI.slots.length, Math.min((page + 1) * PartyInviteUI.slots.length, players.size()))) {
            map.put(PartyInviteUI.slots[i], p);
            setElement(PartyInviteUI.slots[i ++], VisualkitElement.head(p).title(Component.text(p.getName()).color(NamedTextColor.WHITE)));
        }
    }

    @Handler(slot = 45)
    public void onBack() {
        new PartyInviteUI(player, Math.max(page - 1, 0));
    }

    @Handler(slot = 49)
    public void onReturn() {
        new PartyMenuUI(player);
    }

    @Handler(slot = 53)
    public void onNext() {
        new PartyInviteUI(player, Math.min(page + 1, players.size() / slots.length));
    }

    @Handler(asm = {@Asm(from = 10, to = 16), @Asm(from = 19, to = 25), @Asm(from = 28, to = 34)})
    public void onInvite(int slot) {
        if (! map.containsKey(slot)) {
            return;
        }

        Player receiver = map.get(slot);

        if (Request.getInstance(PartyInvite.class, player, receiver) != null) {
            player.sendMessage(Language.translate("message.request.already", player).color(NamedTextColor.RED));
            return;
        }

        new ConfirmUI(player, new BukkitRunnable() {
            @Override
            public void run() {
                new PartyInvite(player, receiver, PartyInviteUI.this.party);
            }
        }, null);
    }
}
