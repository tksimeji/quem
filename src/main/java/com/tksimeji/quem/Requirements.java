package com.tksimeji.quem;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;

public final class Requirements extends HashSet<IQuestType.Requirement> {
    public Requirements(@Nullable JsonObject json) {
        Optional.ofNullable(json).orElse(new JsonObject()).entrySet().stream()
                .filter(entry -> entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isNumber())
                .forEach(entry -> add(new IQuestType.Requirement(entry.getKey(), entry.getValue().getAsInt())));
    }

    public Requirements(@Nullable ConfigurationSection yaml) {
        if (yaml == null) {
            return;
        }

        yaml.getValues(false).entrySet().stream()
                .filter(entry -> entry.getValue() instanceof Integer)
                .forEach(entry -> add(new IQuestType.Requirement(entry.getKey(), (Integer) entry.getValue())));
    }
}
