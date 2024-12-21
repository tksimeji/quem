package com.tksimeji.quem.element;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.tksimeji.quem.DebugQuest;
import com.tksimeji.quem.Quem;
import com.tksimeji.quem.Quest;
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

    public void run(@NotNull Quest executor) {
        Bukkit.getScheduler().runTaskLater(Quem.plugin(), () -> {
            if (! source.stream().allMatch(command -> execute(executor, command))) {
                throw new RuntimeException();
            }

            if (executor instanceof DebugQuest debugger) {
                debugger.onScripting(this);
            }
        }, delay * 20L);
    }

    private boolean execute(@NotNull Quest executor, @NotNull String command) {
        if (! command.startsWith("$ ")) {
            command = "$ exec " + command;
        }

        String trimmedCommand = command.substring(1).trim();
        String[] tokens = trimmedCommand.split("\\s+", 2);
        String prefix = 1 <= tokens.length ? tokens[0] : StringUtility.empty();
        String statement = tokens.length == 2 ? tokens[1] : StringUtility.empty();

        return switch (prefix) {
            case "exec" -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), statement);
            case "foreach" -> executor.getPlayers().stream().allMatch(player -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), statement.replace("${player}", player.getName())));
            default -> false;
        };
    }

    public enum Trigger {
        START,
        END,
        COMPLETE,
        INCOMPLETE,
        PROGRESS
    }
}
