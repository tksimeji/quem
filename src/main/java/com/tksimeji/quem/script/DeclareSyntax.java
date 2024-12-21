package com.tksimeji.quem.script;

import org.jetbrains.annotations.NotNull;

public final class DeclareSyntax implements ScriptSyntax {
    @Override
    public @NotNull String name() {
        return "declare";
    }

    @Override
    public void use(@NotNull ScriptRuntime runtime, @NotNull String[] args) {
        if (args.length != 3) {
            return;
        }

        String name = args[0];
        String operator = args[1];

        if (! operator.equals("=")) {
            return;
        }

        String value = args[2];

        runtime.setVariable(name, value);
    }
}
