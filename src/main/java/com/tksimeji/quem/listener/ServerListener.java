package com.tksimeji.quem.listener;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.tksimeji.quem.Quest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class ServerListener implements Listener {
    @EventHandler
    public void onServerTickStart(@NotNull ServerTickStartEvent event) {
        Quest.getInstances().forEach(Quest::tick);
    }
}
