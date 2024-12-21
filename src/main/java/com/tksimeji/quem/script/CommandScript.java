package com.tksimeji.quem.script;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.tksimeji.quem.DebugQuest;
import com.tksimeji.quem.Quem;
import com.tksimeji.quem.QuestSyntaxException;
import com.tksimeji.quem.util.StringUtility;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class CommandScript {
    private static final @NotNull Pattern pattern = Pattern.compile("(" + Arrays.stream(Trigger.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.joining("|")) + ")(?:\\+(\\d)+s)?");

    private final @NotNull String name;
    private final @NotNull Trigger trigger;
    private final int delay;

    private final List<String> source;

    public CommandScript(@NotNull String name, @NotNull JsonElement json) {
        this.name = name;

        Matcher matcher = CommandScript.pattern.matcher(name);

        if (! matcher.matches()) {
            throw new QuestSyntaxException("Invalid script header: " + name);
        }

        trigger = Trigger.valueOf(matcher.group(1).toUpperCase());
        delay = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;

        if (! (json instanceof JsonArray array)) {
            throw new QuestSyntaxException("The script must defined in a string array.");
        }

        source = array.asList().stream()
                .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isString())
                .map(JsonElement::getAsString)
                .toList();
    }

    public CommandScript(@NotNull String name, @NotNull List<String> yaml) {
        this.name = name;

        Matcher matcher = CommandScript.pattern.matcher(name);

        if (! matcher.matches()) {
            throw new QuestSyntaxException("Invalid script header: " + name);
        }

        trigger = Trigger.valueOf(matcher.group(1).toUpperCase());
        delay = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;

        source = new ArrayList<>(yaml);
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull Trigger getTrigger() {
        return trigger;
    }

    public int getDelay() {
        return delay;
    }

    public void run(@NotNull ScriptRuntime runtime) {
        Bukkit.getScheduler().runTaskLater(Quem.plugin(), () -> {
            source.forEach(line -> execute(runtime, line));

            if (runtime.getQuest() instanceof DebugQuest debugger) {
                debugger.onScripting(this);
            }
        }, delay * 20L);
    }

    private void execute(@NotNull ScriptRuntime runtime, @NotNull String command) {
        if (! command.startsWith("$ ")) {
            command = "$ run " + command;
        }

        command = command.substring(1).trim();
        String[] tokens = command.split("\\s+", 2);

        String syntax = 1 <= tokens.length ? tokens[0] : StringUtility.empty();
        String statement = tokens.length == 2 ? tokens[1] : StringUtility.empty();

        ScriptRuntime.syntaxes.stream()
                .filter(s -> s.name().equals(syntax))
                .findFirst()
                .ifPresent(s -> s.use(runtime, statement.split(" ")));
    }

    public enum Trigger {
        START,
        END,
        COMPLETE,
        INCOMPLETE,
        PROGRESS
    }
}
