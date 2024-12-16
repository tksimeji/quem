package com.tksimeji.quem;

import org.jetbrains.annotations.NotNull;

public class QuestSyntaxException extends IllegalStateException {
    @Deprecated
    public QuestSyntaxException() {

    }

    public QuestSyntaxException(@NotNull String message) {
        super(message);
    }
}
