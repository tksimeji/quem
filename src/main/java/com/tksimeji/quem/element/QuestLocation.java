package com.tksimeji.quem.element;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.tksimeji.quem.QuestSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class QuestLocation extends Location {
    private static @NotNull World world(@NotNull JsonObject json) {
        World world = Bukkit.getWorld(Optional.ofNullable(NamespacedKey.fromString(Optional.ofNullable(json.get("world"))
                        .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isString())
                        .orElseThrow(() -> new QuestSyntaxException("String type property \"world\" must be specified.")).getAsString()))
                .orElseThrow(() -> new QuestSyntaxException("\"" + json.get("world").getAsString() + "\" is an invalid namespaced key format.")));

        if (world == null) {
            throw new QuestSyntaxException("\"" + json.get("world").getAsString() + "\" is an invalid namespaced key.");
        }

        return world;
    }

    private static @NotNull World world(@NotNull ConfigurationSection yaml) {
        World world = Bukkit.getWorld(Optional.ofNullable(NamespacedKey.fromString(Optional.ofNullable(yaml.getString("world"))
                        .orElseThrow(() -> new QuestSyntaxException("String type property \"world\" must be specified."))))
                .orElseThrow(() -> new QuestSyntaxException("\"" + yaml.getString("world") + "\" is an invalid namespaced key format.")));

        if (world == null) {
            throw new QuestSyntaxException("\"" + yaml.getString("world") + "\" is an invalid namespaced key.");
        }

        return world;
    }

    private static double coordinate(@NotNull JsonObject json, @NotNull String property) {
        return Optional.ofNullable(json.get(property))
                .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isNumber())
                .orElseThrow(() -> new QuestSyntaxException("Numeric type property \"" + property + "\" must be specified."))
                .getAsDouble();
    }

    private static double coordinate(@NotNull ConfigurationSection yaml, @NotNull String property) {
        if (! (yaml.isInt(property) || yaml.isDouble(property))) {
            throw new QuestSyntaxException("Numeric type property \"" + property + "\" must be specified.");
        }

        return yaml.getDouble(property);
    }

    public static float direction(@NotNull JsonObject json, @NotNull String property) {
        return Optional.ofNullable(json.get(property))
                .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isNumber())
                .orElse(new JsonPrimitive(0.0f))
                .getAsFloat();
    }


    private static float direction(@NotNull ConfigurationSection yaml, @NotNull String property) {
        try {
            return (float) coordinate(yaml, property);
        } catch (QuestSyntaxException e) {
            return 0.0f;
        }
    }

    public QuestLocation(@NotNull JsonObject json) {
        super(world(json), coordinate(json, "x"), coordinate(json, "y"), coordinate(json, "z"), direction(json, "yaw"), direction(json, "pitch"));
    }

    public QuestLocation(@NotNull ConfigurationSection yaml) {
        super(world(yaml), coordinate(yaml, "x"), coordinate(yaml, "y"), coordinate(yaml, "z"), direction(yaml, "yaw"), direction(yaml, "pitch"));
    }
}
