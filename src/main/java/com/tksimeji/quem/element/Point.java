package com.tksimeji.quem.element;

import com.google.gson.JsonObject;
import com.tksimeji.quem.QuestSyntaxException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class Point extends QuestLocation {
    private final int requirement;

    public Point(@NotNull JsonObject json) {
        super(json);

        requirement = Optional.ofNullable(json.get("requirement"))
                .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isNumber())
                .orElseThrow(() -> new QuestSyntaxException("Numeric type property \"requirement\" must be specified."))
                .getAsInt();
    }

    public Point(@NotNull ConfigurationSection yaml) {
        super(yaml);

        if (! yaml.isInt("requirement")) {
            throw new QuestSyntaxException("Numeric type property \"requirement\" must be specified.");
        }

        requirement = yaml.getInt("requirement");
    }

    public int requirement() {
        return requirement;
    }
}
