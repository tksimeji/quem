package com.tksimeji.quem.ui;

import com.tksimeji.visualkit.api.*;
import com.tksimeji.visualkit.element.VisualkitElement;
import com.tksimeji.quem.Party;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class PartyMenuUI extends PartyUI {
    private static final int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33};

    @Element(49)
    private final VisualkitElement exit = VisualkitElement.create(Material.BARRIER)
            .title(Language.translate("ui.close", player).color(NamedTextColor.RED))
            .lore(Language.translate("ui.close.description", player).color(NamedTextColor.GRAY));

    private int invite = -1;

    private final Map<Integer, Player> members = new HashMap<>();

    public PartyMenuUI(@NotNull Player player) {
        super(player, Party.getInstance(player));

        if (party == null) {
            throw new IllegalArgumentException();
        }

        Arrays.stream(PartyMenuUI.slots).forEach(slot -> setElement(slot, VisualkitElement.head("http://textures.minecraft.net/texture/ffccfe5096a335b9ab78ab4f778ae499f4ccab4e2c95fa349227fd060759baaf")));

        if (party.hasQuest()) {
            setElement(48, VisualkitElement.create(Material.ENDER_CHEST)
                    .title(Language.translate("ui.party_menu.quest", player).color(NamedTextColor.AQUA))
                    .lore(Language.translate("ui.party_menu.quest.description", player).color(NamedTextColor.GRAY), party.getQuest().getType().getTitle()));
        }

        if (player == party.getLeader()) {
            setElement(50, VisualkitElement.create(Material.TNT_MINECART)
                    .title(Language.translate("ui.party_menu.delete", player).color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
                    .lore(Language.translate("ui.party_menu.delete.description", player).color(NamedTextColor.GRAY)));

            if (party.size() < Party.MAX_PLAYERS && party.getQuest() == null) {
                invite = PartyMenuUI.slots[party.size()];
                setElement(invite, VisualkitElement.head("http://textures.minecraft.net/texture/dd1500e5b04c8053d40c7968330887d24b073daf1e273faf4db8b62ebd99da83")
                        .title(Language.translate("ui.party_menu.invite", player).color(NamedTextColor.GREEN))
                        .lore(Language.translate("ui.party_menu.invite.description", player).color(NamedTextColor.GRAY)));
            }
        } else {
            setElement(50, VisualkitElement.create(Material.TNT_MINECART)
                    .title(Language.translate("ui.party_menu.quit", player).color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
                    .lore(Language.translate("ui.party_menu.quit.description", player)));
        }

        for (int i = 0; i < party.size(); i++) {
            Player member = party.getMembers().get(i);

            VisualkitElement memberElement = VisualkitElement.head(member);

            if (member == party.getLeader()) {
                memberElement.title(Component.text("👑").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)
                        .appendSpace()
                        .append(Component.text(member.getName()).color(NamedTextColor.WHITE)));
            } else {
                memberElement.title(Component.text(member.getName()));
            }

            if (player == party.getLeader() && member != party.getLeader()) {
                memberElement.lore(Language.translate("ui.party_menu.member", player).color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false),
                        Language.translate("ui.left_click", player).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).append(Language.translate("ui.party_menu.member.kick", player).color(NamedTextColor.GREEN)),
                        Language.translate("ui.right_click", player).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).append(Language.translate("ui.party_menu.member.transfer", player).color(NamedTextColor.GREEN)));
            }

            members.put(PartyMenuUI.slots[i], member);
            setElement(PartyMenuUI.slots[i], memberElement);
        }
    }

    @Handler(slot = 49)
    public void onExit() {
        close();
    }

    @Handler(slot = 50)
    public void onLeave() {
        new ConfirmUI(player, new BukkitRunnable() {
            @Override
            public void run() {
                Party party = PartyMenuUI.this.party;

                if (player == party.getLeader()) {
                    party.disband();
                } else {
                    party.removeMember(player);
                }
            }
        }, null);
    }

    @Handler(asm = {@Asm(from = 11, to = 15), @Asm(from = 20, to = 24), @Asm(from = 29, to = 33)}, click = Click.SHIFT, mouse = Mouse.LEFT)
    public void onKick(int slot) {
        if (player != party.getLeader()) {
            return;
        }

        Player member = members.get(slot);

        if (member == player) {
            return;
        }

        party.removeMember(member);
    }

    @Handler(asm = {@Asm(from = 11, to = 15), @Asm(from = 20, to = 24), @Asm(from = 29, to = 33)}, click = Click.SHIFT, mouse = Mouse.LEFT)
    public void onTransfer(int slot) {
        if (player != party.getLeader()) {
            return;
        }

        Player member = members.get(slot);

        if (member == player) {
            return;
        }

        party.setLeader(member);
        new PartyMenuUI(player);
    }

    @Handler(asm = {@Asm(from = 11, to = 15), @Asm(from = 20, to = 24), @Asm(from = 29, to = 33)})
    public void onInvite(int slot) {
        if (slot != invite) {
            return;
        }

        new PartyInviteUI(player);
    }
}
