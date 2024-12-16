package com.tksimeji.quem.request;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

interface IRequest {
    @NotNull Player getSender();

    @NotNull Player getReceiver();

    long ticks();

    void onEnd();

    void onAccept();

    void onReject();

    void onTimeOver();
}
