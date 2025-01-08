package com.tksimeji.quem.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tksimeji.quem.QuestSyntaxException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class Point extends QuestLocation {
    private final @NotNull Map<String, Integer> requirements;

    public Point(@NotNull JsonObject json) {
        super(json);

        requirements = Optional.ofNullable(json.get("requirements"))
                .filter(JsonElement::isJsonObject)
                .orElseThrow(() -> new QuestSyntaxException("Object type property \"requirements\" must be specified."))
                .getAsJsonObject()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getAsInt()
                ));
    }

    public Point(@NotNull ConfigurationSection yaml) {
        super(yaml);

        if (! yaml.isInt("requirement")) {
            throw new QuestSyntaxException("Numeric type property \"requirement\" must be specified.");
        }

        requirements = Optional.ofNullable(yaml.getConfigurationSection("requirements"))
                .orElseThrow(() -> new QuestSyntaxException("Object type property \"requirements\" must be specified."))
                .getValues(false)
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() instanceof Integer)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (Integer) entry.getValue()
                ));
    }

    public @NotNull Map<String, Integer> getRequirements() {
        return new HashMap<>(requirements);
    }
}
