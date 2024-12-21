package com.tksimeji.quem;

import com.tksimeji.quem.element.*;
import com.tksimeji.quem.element.CommandScript;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public abstract class QuestType implements IQuestType {
    static final @NotNull List<QuestType> instances = new ArrayList<>();

    public static @Nullable QuestType getInstance(@NotNull String name) {
        return QuestType.instances.stream().filter(i -> i.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static @NotNull List<QuestType> getInstances() {
        return new ArrayList<>(QuestType.instances);
    }

    public static boolean exists(@NotNull String name) {
        return getInstance(name) != null;
    }

    protected final @NotNull NamespacedKey key;

    protected final @NotNull Component title;
    protected final @NotNull Description description;
    protected final @NotNull QuestIcon icon;
    protected final @NotNull Category category;
    protected final @NotNull Location location;

    protected final @NotNull Points points;
    protected final @NotNull Scripts scripts;

    protected final int requirement;
    protected final int playLimit;
    protected final int playerLimit;

    private boolean valid = true;

    private final @NotNull File file;

    public QuestType(@NotNull File f, @NotNull NamespacedKey k, @NotNull Component c, @NotNull Description d, @NotNull QuestIcon i, @NotNull Category c2,
                     @NotNull Location l, @NotNull Points p, @NotNull Scripts s, int r, int pl, int pl2) {
        if (getInstance(k.getKey()) != null) {
            throw new IllegalStateException("Identifier \"" + k.getKey() + "\" is already in use.");
        }

        key = k;
        title = c;
        description = d;
        icon = i.title(title).description(description);
        category = c2;
        location = l;
        points = p;
        scripts = s;
        requirement = r;
        playLimit = pl;
        playerLimit = pl2;
        file = f;

        instances.add(this);
    }

    @Override
    public @NotNull String getName() {
        return key.getKey();
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Override
    public @NotNull Component getTitle() {
        return title;
    }

    @Override
    public @NotNull List<Component> getDescription() {
        return new ArrayList<>(description);
    }

    @Override
    public @NotNull ItemStack getIcon() {
        return icon;
    }

    @Override
    public @NotNull Category getCategory() {
        return category;
    }

    @Override
    public @NotNull Location getLocation() {
        return location.clone();
    }

    @Override
    public @NotNull List<Point> getPoints() {
        return new ArrayList<>(points);
    }

    @Override
    public @NotNull List<CommandScript> getScripts() {
        return new ArrayList<>(scripts);
    }

    public @NotNull File getFile() {
        return file;
    }

    @Override
    public int getRequirement() {
        return requirement;
    }

    @Override
    public int getPlayLimit() {
        return playLimit;
    }

    @Override
    public int getPlayerLimit() {
        return playerLimit;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isGranted(@Nullable Player player) {
        if (player == null) {
            return false;
        }

        PersistentDataContainer container = player.getPersistentDataContainer();
        return container.has(key, PersistentDataType.INTEGER);
    }

    public boolean hasPlayLimit() {
        return 0 <= playLimit;
    }

    public boolean hasPlayerLimit() {
        return 0 <= playerLimit;
    }

    public boolean hasPlayRights(@Nullable Player player) {
        if (player == null) {
            return false;
        }

        PersistentDataContainer container = player.getPersistentDataContainer();
        Integer count = container.get(key, PersistentDataType.INTEGER);

        if (count == null) {
            return false;
        }

        return ! hasPlayLimit() || playLimit >= count;
    }

    public void enable() {
        valid = true;
    }

    public void disable() {
        valid = false;
    }

    public void grant(@NotNull Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        container.set(key, PersistentDataType.INTEGER, 0);
    }

    public void revoke(@NotNull Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        container.remove(key);
    }
}
