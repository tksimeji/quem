package com.tksimeji.quem;

import com.tksimeji.quem.element.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.tksimeji.quem.element.CommandScript;
import com.tksimeji.quem.util.ComponentUtility;
import com.tksimeji.quem.util.FileUtility;
import com.tksimeji.quem.util.ResourceUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.StreamSupport;

public final class QuestType implements IQuestType {
    static final @NotNull List<QuestType> instances = new ArrayList<>();

    public static @Nullable QuestType getInstance(@NotNull String name) {
        return QuestType.instances.stream().filter(i -> i.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static @NotNull List<QuestType> getInstances() {
        return new ArrayList<>(QuestType.instances);
    }

    public static boolean exists(@NotNull String name) {
        return getInstance(name) != null;
    }

    private final @NotNull String name;
    private final @NotNull NamespacedKey key;

    private final @NotNull Component title;
    private final @NotNull List<Component> description;
    private final @NotNull QuestIcon icon;
    private final @NotNull Category category;
    private final @NotNull Location location;

    private final @NotNull List<Point> points;
    private final @NotNull List<CommandScript> scripts;

    private final int requirement;
    private final int playLimit;
    private final int playerLimit;

    private boolean valid = true;

    private final @NotNull File file;

    public QuestType(@NotNull File f) throws QuestSyntaxException {
        JsonObject source = ResourceUtility.getJsonResource(Quem.directory().toURI().relativize(f.toURI()).getPath()).getAsJsonObject();

        name = FileUtility.asName(f);

        key = new NamespacedKey(Quem.plugin(), name);

        title = Component.text().decoration(TextDecoration.ITALIC, false)
                .append(ComponentUtility.deserialize(Optional.ofNullable(source.get("title"))
                        .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isString())
                        .orElseThrow(() -> new QuestSyntaxException("String type property \"title\" must be specified.")).getAsString())).build();

        description = StreamSupport.stream(Optional.ofNullable(source.get("description"))
                        .filter(JsonElement::isJsonArray)
                        .orElse(new JsonArray()).getAsJsonArray().spliterator(), false)
                .map(line -> Component.text().color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).append(ComponentUtility.deserialize(line.getAsString())).build().asComponent())
                .toList();

        icon = new QuestIcon(Optional.ofNullable(source.get("icon")).orElseThrow(() -> new QuestSyntaxException("String or object type property \"icon\" must be specified.")))
                .title(title)
                .description(description);

        category = Arrays.stream(Category.values()).filter(c -> c.toString().equalsIgnoreCase(Optional.ofNullable(source.get("category"))
                .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isString())
                .orElseThrow(() -> new QuestSyntaxException("String type property \"category\" must be specified."))
                .getAsString())).findFirst().orElseThrow(() -> new QuestSyntaxException("Invalid category."));

        location = new QuestLocation(Optional.ofNullable(source.get("location"))
                .filter(JsonElement::isJsonObject)
                .orElseThrow(() -> new QuestSyntaxException("Object type property \"location\" must be specified."))
                .getAsJsonObject());

        points = StreamSupport.stream(Optional.ofNullable(source.get("points"))
                        .filter(JsonElement::isJsonArray)
                        .orElse(new JsonArray()).getAsJsonArray().spliterator(), false)
                .map(point -> new Point(point.getAsJsonObject()))
                .sorted(Comparator.comparingInt(Point::requirement))
                .toList();

        scripts = Optional.ofNullable(source.get("scripts"))
                .filter(JsonElement::isJsonObject)
                .orElse(new JsonObject())
                .getAsJsonObject()
                .entrySet()
                .stream()
                .filter(script -> script.getValue().isJsonArray())
                .map(script -> new CommandScript(script.getKey(), script.getValue()))
                .toList();

        requirement = Optional.ofNullable(source.get("requirement"))
                .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isNumber())
                .orElseThrow(() -> new QuestSyntaxException("Numeric type property \"requirement\" must be specified."))
                .getAsInt();

        playLimit = Optional.ofNullable(source.get("play_limit"))
                .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isNumber())
                .orElse(new JsonPrimitive(-1))
                .getAsInt();

        playerLimit = Optional.ofNullable(source.get("player_limit"))
                .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isNumber())
                .orElse(new JsonPrimitive(-1))
                .getAsInt();

        file = f;

        instances.add(this);
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Override
    public @NotNull Component getTitle() {
        return title;
    }

    @Override
    public @NotNull List<Component> getDescription() {
        return new ArrayList<>(description);
    }

    @Override
    public @NotNull ItemStack getIcon() {
        return icon;
    }

    @Override
    public @NotNull Category getCategory() {
        return category;
    }

    @Override
    public @NotNull Location getLocation() {
        return location.clone();
    }

    @Override
    public @NotNull List<Point> getPoints() {
        return new ArrayList<>(points);
    }

    @Override
    public @NotNull List<CommandScript> getScripts() {
        return new ArrayList<>(scripts);
    }

    public @NotNull File getFile() {
        return file;
    }

    @Override
    public int getRequirement() {
        return requirement;
    }

    @Override
    public int getPlayLimit() {
        return playLimit;
    }

    @Override
    public int getPlayerLimit() {
        return playerLimit;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isGranted(@Nullable Player player) {
        if (player == null) {
            return false;
        }

        PersistentDataContainer container = player.getPersistentDataContainer();
        return container.has(key, PersistentDataType.INTEGER);
    }

    public boolean hasPlayLimit() {
        return 0 <= playLimit;
    }

    public boolean hasPlayerLimit() {
        return 0 <= playerLimit;
    }

    public boolean hasPlayRights(@Nullable Player player) {
        if (player == null) {
            return false;
        }

        PersistentDataContainer container = player.getPersistentDataContainer();
        Integer count = container.get(key, PersistentDataType.INTEGER);

        if (count == null) {
            return false;
        }

        return ! hasPlayLimit() || playLimit >= count;
    }

    public void enable() {
        valid = true;
    }

    public void disable() {
        valid = false;
    }

    public void grant(@NotNull Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        container.set(key, PersistentDataType.INTEGER, 0);
    }

    public void revoke(@NotNull Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        container.remove(key);
    }
}
