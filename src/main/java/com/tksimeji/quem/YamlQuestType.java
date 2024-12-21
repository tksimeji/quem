package com.tksimeji.quem;


import com.tksimeji.quem.element.*;
import com.tksimeji.quem.util.ComponentUtility;
import com.tksimeji.quem.util.FileUtility;
import com.tksimeji.quem.util.ResourceUtility;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public final class YamlQuestType extends QuestType {
    private static <T> T exception(@NotNull String message, @NotNull Class<T> type) {
        throw new QuestSyntaxException(message);
    }

    public YamlQuestType(@NotNull File file) {
        super(file,
                new NamespacedKey(Quem.plugin(), FileUtility.asName(file)),
                ComponentUtility.empty().append(ComponentUtility.deserialize(Optional.ofNullable(ResourceUtility.getYamlResource(file).getString(("title")))
                        .orElseThrow(() -> new QuestSyntaxException("String type property \"title\" must be specified.")))),
                new Description(ResourceUtility.getYamlResource(file).getStringList("description")),
                new QuestIcon(ResourceUtility.getYamlResource(file)),
                Arrays.stream(Category.values()).filter(c -> c.toString().equalsIgnoreCase(Optional.ofNullable(ResourceUtility.getYamlResource(file).getString("category"))
                        .orElseThrow(() -> new QuestSyntaxException("String type property \"category\" must be specified.")))).findFirst().orElseThrow(() -> new QuestSyntaxException("Invalid category.")),
                new QuestLocation(Optional.ofNullable(ResourceUtility.getYamlResource(file).getConfigurationSection("location"))
                        .orElseThrow(() -> new QuestSyntaxException("Object type property \"location\" must be specified."))),
                new Points(ResourceUtility.getYamlResource(file).getConfigurationSection("points")),
                new Scripts(ResourceUtility.getYamlResource(file).getConfigurationSection("scripts")),
                ResourceUtility.getYamlResource(file).isInt("requirement") ? ResourceUtility.getYamlResource(file).getInt("requirement") : exception("Numeric type property \"requirement\" must be specified.", Integer.class),
                ResourceUtility.getYamlResource(file).isInt("play_limit") ? ResourceUtility.getYamlResource(file).getInt("play_limit") : -1,
                ResourceUtility.getYamlResource(file).isInt("player_limit") ? ResourceUtility.getYamlResource(file).getInt("player_limit") : -1);
    }
}
