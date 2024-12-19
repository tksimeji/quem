package com.tksimeji.quem.listener;

import com.tksimeji.quem.ui.Actions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public final class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (! (event.getClickedInventory() instanceof PlayerInventory inventory)) {
            return;
        }

        event.setCancelled((Actions.refer(event.getSlot()) != null || Actions.refer(inventory.getHeldItemSlot()) != null) || event.isCancelled());
    }
}
