package com.tksimeji.quem.script;

import org.jetbrains.annotations.NotNull;

public interface ScriptSyntax {
    @NotNull String name();

    void use(@NotNull ScriptRuntime runtime, @NotNull String[] args);
}
