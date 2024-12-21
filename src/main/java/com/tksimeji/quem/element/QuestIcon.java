package com.tksimeji.quem.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tksimeji.quem.QuestSyntaxException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public final class QuestIcon extends ItemStack {
    private static @NotNull Material getType(@NotNull JsonElement json) {
        return getType(json.isJsonPrimitive() && json.getAsJsonPrimitive().isString() ?
                json.getAsString() : (json instanceof JsonObject obj) && obj.has("type") ?
                obj.get("type").getAsString() : null);
    }

    private static @NotNull Material getType(@NotNull YamlConfiguration yaml) {
        return getType(yaml.isString("icon") ?
                yaml.getString("icon") : yaml.isConfigurationSection("icon") && yaml.getConfigurationSection("icon").isString("type") ?
                yaml.getConfigurationSection("icon").getString("type") : null);
    }

    private static @NotNull Material getType(@Nullable String string) {
        if (string == null) {
            throw new QuestSyntaxException("The item type must be specified as either a string or the type property of an object.");
        }

        return Optional.ofNullable(Material.matchMaterial(string))
                .orElseThrow(() -> new QuestSyntaxException("\"" + string + "\" is an invalid item type."));
    }

    public QuestIcon(@NotNull JsonElement json) {
        super(QuestIcon.getType(json), 1);

        ItemMeta meta = this.getItemMeta();

        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE.getKey(), 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_KNOCKBACK, new AttributeModifier(Attribute.GENERIC_ATTACK_KNOCKBACK.getKey(), 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(Attribute.GENERIC_ATTACK_SPEED.getKey(), 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

        meta.addItemFlags(ItemFlag.values());

        if (json instanceof JsonObject obj) {
            Optional.ofNullable(obj.get("model"))
                    .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isNumber())
                    .ifPresent(elm -> meta.setCustomModelData(elm.getAsInt()));

            Optional.ofNullable(obj.get("aura"))
                    .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isBoolean())
                    .ifPresent(elm -> meta.addEnchant(Enchantment.INFINITY, 1, false));
        }

        setItemMeta(meta);
    }

    public QuestIcon(@NotNull YamlConfiguration yaml) {
        super(QuestIcon.getType(yaml), 1);

        ItemMeta meta = this.getItemMeta();

        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE.getKey(), 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_KNOCKBACK, new AttributeModifier(Attribute.GENERIC_ATTACK_KNOCKBACK.getKey(), 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(Attribute.GENERIC_ATTACK_SPEED.getKey(), 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

        meta.addItemFlags(ItemFlag.values());

        if (! yaml.isString("icon")) {
            ConfigurationSection icon = yaml.getConfigurationSection("icon");

            if (icon != null && icon.isInt("model")) {
                meta.setCustomModelData(yaml.getInt("model"));
            }

            if (icon != null && icon.isBoolean("aura") && icon.getBoolean("aura")) {
                meta.addEnchant(Enchantment.INFINITY, 1, false);
            }
        }

        setItemMeta(meta);
    }

    public @NotNull QuestIcon title(@NotNull Component title) {
        ItemMeta meta = getItemMeta();
        meta.displayName(Component.text().decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false).append(title).build());
        setItemMeta(meta);
        return this;
    }

    public @NotNull QuestIcon description(@Nullable List<? extends Component> components) {
        super.lore(components == null || components.isEmpty() ? List.of(Component.text().build()) : components.stream()
                .map(line -> Component.text().color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).append(line).build())
                .toList());
        return this;
    }
}
