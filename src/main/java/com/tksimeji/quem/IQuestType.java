package com.tksimeji.quem;

import com.tksimeji.quem.element.Category;
import com.tksimeji.quem.script.CommandScript;
import com.tksimeji.quem.element.Point;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IQuestType {
    @NotNull String getName();

    @NotNull NamespacedKey getKey();

    @NotNull Component getTitle();

    @NotNull List<Component> getDescription();

    @NotNull ItemStack getIcon();

    @NotNull Category getCategory();

    @NotNull Location getLocation();

    @NotNull List<Point> getPoints();

    @NotNull List<CommandScript> getScripts();

    int getRequirement();

    int getPlayLimit();

    int getPlayerLimit();
}
