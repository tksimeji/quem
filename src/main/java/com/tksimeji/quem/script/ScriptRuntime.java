package com.tksimeji.quem.script;

import com.tksimeji.quem.Quest;
import com.tksimeji.quem.util.StringUtility;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ScriptRuntime {
    public static final Set<ScriptSyntax> syntaxes = Set.of(new ForeachSyntax(), new DeclareSyntax(), new RunSyntax());

    private final @NotNull Quest quest;

    private final @NotNull Map<String, String> variables = new HashMap<>();

    public ScriptRuntime(@NotNull Quest quest) {
        this.quest = quest;
    }

    public @NotNull Quest getQuest() {
        return quest;
    }

    public @NotNull String getVariable(@NotNull String name) {
        return Optional.ofNullable(variables.get(name)).orElse(StringUtility.empty());
    }

    public void setVariable(@NotNull String name, @NotNull String value) {
        variables.put(name, value);
    }

    public @NotNull String replace(@NotNull String command) {
        for (Map.Entry<String, String> variable : variables.entrySet()) {
            command = command.replace("${" + variable.getKey() + "}", variable.getValue());
        }

        return command;
    }

    public void call(@NotNull CommandScript.Trigger trigger) {
        quest.getType().getScripts().stream()
                .filter(script -> script.getTrigger() == trigger)
                .forEach(script -> script.run(this));
    }
}
