package com.tksimeji.quem.element;

import org.jetbrains.annotations.NotNull;

public enum Category {
    GENERAL("general"),
    STORY("story"),
    DAILY("daily"),
    EVENT("event");

    private final String literal;

    Category(@NotNull String literal) {
        this.literal = literal;
    }

    public @NotNull String literal() {
        return literal;
    }
}
