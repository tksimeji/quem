package com.tksimeji.quem.element;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.tksimeji.quem.QuestSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class QuestLocation extends Location {
    private static @NotNull World world(@NotNull JsonObject source) {
        World world = Bukkit.getWorld(Optional.ofNullable(NamespacedKey.fromString(Optional.ofNullable(source.get("world"))
                        .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isString())
                        .orElseThrow(() -> new QuestSyntaxException("String type property \"world\" must be specified.")).getAsString()))
                .orElseThrow(() -> new QuestSyntaxException("\"" + source.get("world").getAsString() + "\" is an invalid namespaced key format.")));

        if (world == null) {
            throw new QuestSyntaxException("\"" + source.get("world").getAsString() + "\" is an invalid namespaced key.");
        }

        return world;
    }

    private static double coordinate(@NotNull JsonObject source, @NotNull String property) {
        return Optional.ofNullable(source.get(property))
                .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isNumber())
                .orElseThrow(() -> new QuestSyntaxException("Numeric type property \"" + property + "\" must be specified."))
                .getAsDouble();
    }

    public static float direction(@NotNull JsonObject source, @NotNull String property) {
        return Optional.ofNullable(source.get(property))
                .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isNumber())
                .orElse(new JsonPrimitive(0.0f))
                .getAsFloat();
    }

    public QuestLocation(@NotNull JsonObject source) {
        super(QuestLocation.world(source), QuestLocation.coordinate(source, "x"), QuestLocation.coordinate(source, "y"),
                QuestLocation.coordinate(source, "z"), QuestLocation.direction(source, "yaw"), QuestLocation.direction(source, "pitch"));
    }
}
