package com.tksimeji.quem;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.tksimeji.quem.element.*;
import com.tksimeji.quem.util.ComponentUtility;
import com.tksimeji.quem.util.FileUtility;
import com.tksimeji.quem.util.ResourceUtility;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public final class JsonQuestType extends QuestType {
    public JsonQuestType(@NotNull File file) {
        super(file,
                new NamespacedKey(Quem.plugin(), FileUtility.asName(file)),
                ComponentUtility.empty().append(ComponentUtility.deserialize(Optional.ofNullable(ResourceUtility.getJsonResource(file).getAsJsonObject().get("title"))
                                .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isString())
                                .orElseThrow(() -> new QuestSyntaxException("String type property \"title\" must be specified.")).getAsString())),
                new Description(Optional.ofNullable(ResourceUtility.getJsonResource(file).getAsJsonObject().get("description"))
                        .filter(JsonElement::isJsonArray)
                        .orElseThrow(() -> new QuestSyntaxException("Array type property \"description\" must be specified.")).getAsJsonArray()),
                new QuestIcon(Optional.ofNullable(ResourceUtility.getJsonResource(file).getAsJsonObject().get("icon")).orElseThrow(() -> new QuestSyntaxException("String or object type property \"icon\" must be specified."))),
                Arrays.stream(Category.values()).filter(c -> c.toString().equalsIgnoreCase(Optional.ofNullable(ResourceUtility.getJsonResource(file).getAsJsonObject().get("category"))
                        .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isString())
                        .orElseThrow(() -> new QuestSyntaxException("String type property \"category\" must be specified."))
                        .getAsString())).findFirst().orElseThrow(() -> new QuestSyntaxException("Invalid category.")),
                new QuestLocation(Optional.ofNullable(ResourceUtility.getJsonResource(file).getAsJsonObject().get("location"))
                        .filter(JsonElement::isJsonObject)
                        .orElseThrow(() -> new QuestSyntaxException("Object type property \"location\" must be specified."))
                        .getAsJsonObject()),
                new Points(Optional.of(ResourceUtility.getJsonResource(file).getAsJsonObject().get("points"))
                        .filter(JsonElement::isJsonArray)
                        .map(JsonElement::getAsJsonArray)
                        .orElse(null)),
                new Scripts(Optional.of(ResourceUtility.getJsonResource(file).getAsJsonObject().get("scripts"))
                        .filter(JsonElement::isJsonObject)
                        .map(JsonElement::getAsJsonObject)
                        .orElse(null)),
                Optional.ofNullable(ResourceUtility.getJsonResource(file).getAsJsonObject().get("requirement"))
                        .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isNumber())
                        .orElseThrow(() -> new QuestSyntaxException("Numeric type property \"requirement\" must be specified."))
                        .getAsInt(),
                Optional.ofNullable(ResourceUtility.getJsonResource(file).getAsJsonObject().get("play_limit"))
                        .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isNumber())
                        .orElse(new JsonPrimitive(-1))
                        .getAsInt(),
                Optional.ofNullable(ResourceUtility.getJsonResource(file).getAsJsonObject().get("player_limit"))
                        .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isNumber())
                        .orElse(new JsonPrimitive(-1))
                        .getAsInt());
    }
}
