package com.tksimeji.quem.script;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class RunSyntax implements ScriptSyntax {
    @Override
    public @NotNull String name() {
        return "run";
    }

    @Override
    public void use(@NotNull ScriptRuntime runtime, @NotNull String[] args) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), runtime.replace(String.join(" ", args)));
    }
}
