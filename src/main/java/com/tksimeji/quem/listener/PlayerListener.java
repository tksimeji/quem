package com.tksimeji.quem.listener;

import com.tksimeji.quem.Party;
import com.tksimeji.quem.Quest;
import com.tksimeji.quem.ui.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Actions.instance().forEach((slot, button) -> player.getInventory().setItem(slot, button.asItemStack(player)));
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Party party = Party.getInstance(player);

        if (party != null) {
            party.removeMember(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Quest quest = Quest.getInstance(event.getPlayer());

        if (quest == null) {
            return;
        }

        quest.getParty().removeMember(player);
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (! event.getAction().isRightClick()) {
            return;
        }

        Player player = event.getPlayer();
        int slot = player.getInventory().getHeldItemSlot();

        Optional.ofNullable(Actions.refer(slot)).ifPresent(action -> action.action(player));
    }

    @EventHandler
    public void onPlayerDropItem(@NotNull PlayerDropItemEvent event) {
        event.setCancelled(Actions.refer(event.getPlayer().getInventory().getHeldItemSlot()) != null || event.isCancelled());
    }
}
