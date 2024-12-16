package com.tksimeji.quem.listener;

import com.tksimeji.quem.Party;
import com.tksimeji.quem.Quest;
import com.tksimeji.quem.ui.PartyMenuUI;
import com.tksimeji.quem.ui.PartyCreateUI;
import com.tksimeji.quem.ui.QuestControllerUI;
import com.tksimeji.quem.ui.QuestUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ItemStack gameMenuStack = new ItemStack(Material.COMPASS);
        ItemMeta gameMenuMeta = gameMenuStack.getItemMeta();
        gameMenuMeta.displayName(Component.text("Game Menu").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false).append(Component.text(" (右クリック)").color(NamedTextColor.GRAY)));
        gameMenuStack.setItemMeta(gameMenuMeta);
        player.getInventory().setItem(6, gameMenuStack);

        ItemStack partyStack = new ItemStack(Material.CHEST_MINECART);
        ItemMeta partyMeta = partyStack.getItemMeta();
        partyMeta.displayName(Component.text("Party").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false).append(Component.text(" (右クリック)").color(NamedTextColor.GRAY)));
        partyMeta.lore(List.of(Component.text("Partyを作成してフレンドと一緒にプレイしよう").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
        partyStack.setItemMeta(partyMeta);
        player.getInventory().setItem(7, partyStack);

        ItemStack questStack = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta questMeta = questStack.getItemMeta();
        questMeta.displayName(Component.text("Quest").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false).append(Component.text(" (右クリック)").color(NamedTextColor.GRAY)));
        questMeta.lore(List.of(Component.text("挑戦可能なクエストの一覧を表示します").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
        questStack.setItemMeta(questMeta);
        player.getInventory().setItem(8, questStack);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Party party = Party.getInstance(player);

        if (party != null) {
            party.removeMember(player);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        int slot = player.getInventory().getHeldItemSlot();

        if (slot == 7) {
            if (Party.getInstance(player) == null) {
                new PartyCreateUI(player);
            } else {
                new PartyMenuUI(player);
            }
        }

        if (slot == 8) {
            if (Quest.getInstance(player) == null) {
                new QuestUI(player);
            } else {
                new QuestControllerUI(player);
            }
        }
    }
}
