package com.tksimeji.quem.request;

import com.tksimeji.quem.Quem;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class Request implements IRequest {
    private static final Set<Request> instances = new HashSet<>();

    public static @Nullable Request getInstance(@NotNull UUID id) {
        return instances.stream().filter(i -> i.getUniqueId().equals(id)).findFirst().orElse(null);
    }

    public static @Nullable Request getInstance(@NotNull Class<? extends Request> clazz, @NotNull Player sender, @NotNull Player receiver) {
        return instances.stream().filter(i -> clazz.isAssignableFrom(i.getClass()) && i.getSender() == sender && i.getReceiver() == receiver).findFirst().orElse(null);
    }

    protected final @NotNull UUID id = UUID.randomUUID();

    protected final @NotNull Player sender;
    protected final @NotNull Player receiver;

    protected final @NotNull BukkitRunnable runnable = new BukkitRunnable() {
        @Override
        public void run() {
            Request.this.onTimeOver();
            Request.this.onEnd();
        }
    };

    public Request(@NotNull Player sender, @NotNull Player receiver) {
        if (sender == receiver) {
            throw new IllegalArgumentException();
        }

        if (getInstance(this.getClass(), sender, receiver) != null) {
            throw new IllegalStateException();
        }

        this.sender = sender;
        this.receiver = receiver;

        runnable.runTaskLater(Quem.plugin(), ticks());

        Request.instances.add(this);
    }

    public @NotNull UUID getUniqueId() {
        return id;
    }

    @Override
    public @NotNull Player getSender() {
        return sender;
    }

    @Override
    public @NotNull Player getReceiver() {
        return receiver;
    }

    public int getLimit() {
        return (int) (ticks() / 20);
    }

    @Override
    public final void onEnd() {
        runnable.cancel();
        Request.instances.remove(this);
    }
}
