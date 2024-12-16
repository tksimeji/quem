package com.tksimeji.quem;

import com.tksimeji.quem.element.CommandScript;
import com.tksimeji.quem.element.Point;
import com.tksimeji.quem.ui.CLI;
import com.tksimeji.quem.ui.QuestPanel;
import com.tksimeji.quem.util.ComponentUtility;
import com.tksimeji.quem.util.StringUtility;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Quest implements IQuest {
    private static final Set<Quest> instances = new HashSet<>();

    public static Quest getInstance(@Nullable Player player) {
        return instances.stream().filter(i -> i.getParty().isMember(player)).findFirst().orElse(null);
    }

    public static @NotNull Set<Quest> getInstances() {
        return new HashSet<>(instances);
    }

    private final QuestType type;
    private final Party party;
    private final QuestPanel panel;

    private final Map<Player, Location> bases = new HashMap<>();

    private int phase;

    public Quest(@NotNull QuestType type, @NotNull Party party) {
        this.type = type;
        this.party = party;

        party.setQuest(this);
        panel = new QuestPanel(this);

        party.forEach(player -> {
            player.sendMessage(Component.text(CLI.SEPARATOR).color(NamedTextColor.GREEN));
            player.sendMessage(Component.text().build());
            player.sendMessage(Component.text(StringUtility.spaces(28 - ComponentUtility.length(type.getTitle()))).append(type.getTitle().decorate(TextDecoration.BOLD)));

            for (Component line : type.getDescription()) {
                player.sendMessage(Component.text(StringUtility.spaces(28 - ComponentUtility.length(line))).append(line));
            }

            player.sendMessage(Component.text().build());
            player.sendMessage(Component.text(CLI.SEPARATOR).color(NamedTextColor.GREEN));

            panel.addAudience(player);
            bases.put(player, player.getLocation());
            player.teleport(type.getLocation());
        });

        call(CommandScript.Trigger.START);

        instances.add(this);
    }

    public @NotNull QuestType getType() {
        return type;
    }

    public @NotNull Party getParty() {
        return party;
    }

    public @NotNull QuestPanel getPanel() {
        return panel;
    }

    public @NotNull List<Player> getPlayers() {
        return party.getMembers();
    }

    @Override
    public int getPhase() {
        return phase;
    }

    @Override
    public void setPhase(int phase) {
        if (this.phase < phase) {
            call(CommandScript.Trigger.PROGRESS);
        }

        this.phase = phase;

        if (type.getRequirement() <= phase) {
            onEnd(EndReason.COMPLETE);
        }
    }

    @Override
    public void onEnd(@NotNull EndReason reason) {
        if (! instances.contains(this)) {
            return;
        }

        call(CommandScript.Trigger.END);

        if (reason == EndReason.COMPLETE) {
            onComplete();
        } else {
            onIncomplete();
        }

        instances.remove(this);

        party.forEach(player -> {
            PersistentDataContainer container = player.getPersistentDataContainer();
            container.set(type.getKey(), PersistentDataType.INTEGER, Optional.ofNullable(container.get(type.getKey(), PersistentDataType.INTEGER)).orElse(0) + 1);

            panel.removeAudience(player);
            player.setHealth(20);
            player.teleport(bases.get(player));
        });

        party.setQuest(null);
    }

    @Override
    public void onComplete() {
        call(CommandScript.Trigger.COMPLETE);
    }

    @Override
    public void onIncomplete() {
        call(CommandScript.Trigger.INCOMPLETE);
    }

    @Override
    public void cancel() {
        party.forEach(player -> player.sendMessage(Language.translate("message.quest.canceled", player).color(NamedTextColor.RED)));
        onEnd(EndReason.CANCEL);
    }

    @Override
    public void call(@NotNull CommandScript.Trigger trigger) {
        type.getScripts().stream()
                .filter(script -> script.getTrigger() == trigger)
                .forEach(script -> script.run(this));
    }

    @Override
    public void tick() {
        for (Player player : getPlayers()) {
            if (player.getInventory().getHeldItemSlot() == 8) {
                navigate(player);
                continue;
            }

            navigating.remove(player);
        }
    }

    private final Set<Player> navigating = new HashSet<>();

    private void navigate(@NotNull Player player) {
        navigating.add(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (! Quest.this.navigating.contains(player)) {
                    cancel();
                    return;
                }

                Location departure = player.getLocation();

                Point destination = Quest.this.type.getPoints().stream()
                        .filter(p -> p.requirement() == Quest.this.phase)
                        .findFirst()
                        .orElse(null);

                if (destination == null) {
                    return;
                }

                double distance = departure.distance(destination);

                for (double i = 0; i <= distance; i += 0.4) {
                    double x = departure.getX() + (destination.getX() - departure.getX()) * (i / distance);
                    double y = departure.getY() + (destination.getY() - departure.getY()) * (i / distance);
                    double z = departure.getZ() + (destination.getZ() - departure.getZ()) * (i / distance);

                    Location location = new Location(departure.getWorld(), x, y, z);

                    if (8 <= departure.distance(location)) {
                        break;
                    }

                    player.spawnParticle(Particle.DUST, location, 1, new Particle.DustOptions(Color.AQUA, 1));
                }
            }
        }.runTaskTimerAsynchronously(Quem.plugin(), 0L, 20L);
    }
}
