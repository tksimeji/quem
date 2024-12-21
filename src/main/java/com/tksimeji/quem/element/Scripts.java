package com.tksimeji.quem.element;

import com.google.gson.JsonObject;
import com.tksimeji.quem.script.CommandScript;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Scripts extends ArrayList<CommandScript> {
    public Scripts(@Nullable JsonObject json) {
        Optional.ofNullable(json).orElse(new JsonObject()).entrySet().stream()
                .filter(entry -> entry.getValue().isJsonArray())
                .forEach(entry -> add(new CommandScript(entry.getKey(), entry.getValue())));
    }

    public Scripts(@Nullable ConfigurationSection yaml) {
        if (yaml == null) {
            return;
        }

        yaml.getValues(false).entrySet().stream()
                .filter(entry -> entry.getValue() instanceof ConfigurationSection)
                .forEach(entry -> add(new CommandScript(entry.getKey(), (List<String>) entry.getValue())));
    }
}
