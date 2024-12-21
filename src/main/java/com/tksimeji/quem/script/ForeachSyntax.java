package com.tksimeji.quem.script;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.regex.Pattern;

public final class ForeachSyntax implements ScriptSyntax {
    @Override
    public @NotNull String name() {
        return "foreach";
    }

    @Override
    public void use(@NotNull ScriptRuntime runtime, @NotNull String[] args) {
        if (args.length < 4) {
            return;
        }

        if (! args[1].equals("in")) {
            return;
        }

        String temp = args[0];
        String variable = runtime.getVariable(args[2]);
        String command = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

        Pattern pattern = Pattern.compile("(?<!\\\\),");
        String[] array = pattern.split(variable.startsWith("[") && variable.endsWith("]") ? variable.substring(1, variable.length() - 1) : variable);

        for (String s : array) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), runtime.replace(command).replace("${" + temp + "}", s));
        }
    }
}
