package com.tksimeji.quem.element;

import com.google.gson.JsonObject;
import com.tksimeji.quem.QuestSyntaxException;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class Point extends QuestLocation {
    private final int requirement;

    public Point(@NotNull JsonObject source) {
        super(source);

        requirement = Optional.ofNullable(source.get("requirement"))
                .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isNumber())
                .orElseThrow(() -> new QuestSyntaxException("Numeric type property \"requirement\" must be specified."))
                .getAsInt();
    }

    public int requirement() {
        return requirement;
    }
}
