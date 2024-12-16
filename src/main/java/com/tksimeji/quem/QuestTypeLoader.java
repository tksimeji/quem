package com.tksimeji.quem;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tksimeji.quem.util.ResourceUtility;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class QuestTypeLoader {
    public static void load(@NotNull File file) throws QuestSyntaxException {
        if (! file.exists() || ! file.isFile()) {
            throw new IllegalArgumentException(file.getName() + " is an invalid file.");
        }

        JsonElement json = ResourceUtility.getJsonResource(Quem.directory().toURI().relativize(file.toURI()).getPath());

        if (! (json instanceof JsonObject)) {
            throw new QuestSyntaxException("The root element must be a json object.");
        }

        new QuestType(file);
    }

    public static void unload(@NotNull QuestType type) {
        Quest.getInstances().stream().filter(quest -> quest.getType() == type).forEach(Quest::cancel);
        QuestType.instances.remove(type);
    }

    public static void reload(@NotNull QuestType type) throws QuestSyntaxException {
        unload(type);
        load(type.getFile());
    }
}
