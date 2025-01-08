package com.tksimeji.quem.element;

import com.google.gson.JsonArray;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.StreamSupport;

public final class Points extends ArrayList<Point> {
    public Points(@Nullable JsonArray json) {
        StreamSupport.stream(Optional.ofNullable(json).orElse(new JsonArray()).spliterator(), false)
                .map(point -> new Point(point.getAsJsonObject()))
                .forEach(this::add);
    }

    public Points(@Nullable ConfigurationSection yaml) {
        if (yaml == null) {
            return;
        }

        yaml.getValues(false).values().stream()
                .filter(object -> object instanceof ConfigurationSection)
                .map(object -> (ConfigurationSection) object)
                .forEach(point -> add(new Point(point)));
    }
}
