package com.tksimeji.quem.element;

import com.google.gson.JsonArray;
import com.tksimeji.quem.util.ComponentUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Description extends ArrayList<Component> {
    public Description(@Nullable JsonArray json) {
        Optional.ofNullable(json).orElse(new JsonArray()).forEach(line -> add(ComponentUtility.deserialize(line.getAsString())));
    }

    public Description(@Nullable List<String> list) {
        Optional.ofNullable(list).orElse(new ArrayList<>()).forEach(line -> add(ComponentUtility.deserialize(line)));
    }
}
