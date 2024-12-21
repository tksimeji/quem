package com.tksimeji.quem;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tksimeji.quem.util.ResourceUtility;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class QuestTypeLoader {
    public static void load(@NotNull File file) {
        if (! file.exists() || ! file.isFile()) {
            throw new IllegalArgumentException(file.getName() + " is an invalid file.");
        }

        if (Quem.JSON_EXTENSIONS.stream().anyMatch(e -> file.getName().endsWith(e))) {
            loadJsonFile(file);
        } else if (Quem.YAML_EXTENSIONS.stream().anyMatch(e -> file.getName().endsWith(e))) {
            loadYamlFile(file);
        }
    }

    private static void loadJsonFile(@NotNull File file) {
        JsonElement json = ResourceUtility.getJsonResource(file);

        if (! (json instanceof JsonObject)) {
            throw new QuestSyntaxException("The root element must be a json object.");
        }

        new JsonQuestType(file);
    }

    private static void loadYamlFile(@NotNull File file) {
        new YamlQuestType(file);
    }

    public static void unload(@NotNull QuestType type) {
        Quest.getInstances().stream().filter(quest -> quest.getType() == type).forEach(Quest::cancel);
        QuestType.instances.remove(type);
    }

    public static void reload(@NotNull QuestType type) {
        unload(type);
        load(type.getFile());
    }
}
