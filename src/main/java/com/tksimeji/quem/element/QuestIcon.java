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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public final class QuestIcon extends ItemStack {
    public static @NotNull Material material(@NotNull JsonElement source) {
        String type = source.isJsonPrimitive() && source.getAsJsonPrimitive().isString() ?
                source.getAsString() : (source instanceof JsonObject obj) && obj.has("type") ?
                obj.get("type").getAsString() : null;

        if (type == null) {
            throw new QuestSyntaxException("The item type must be specified as either a string or the type property of an object.");
        }

        return Optional.ofNullable(Material.matchMaterial(type))
                .orElseThrow(() -> new QuestSyntaxException("\"" + type + "\" is an invalid item type."));
    }

    public QuestIcon(@NotNull JsonElement source) {
        super(QuestIcon.material(source), 1);

        if (! (source instanceof JsonObject obj)) {
            return;
        }

        ItemMeta meta = this.getItemMeta();

        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE.getKey(), 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_KNOCKBACK, new AttributeModifier(Attribute.GENERIC_ATTACK_KNOCKBACK.getKey(), 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(Attribute.GENERIC_ATTACK_SPEED.getKey(), 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

        meta.addItemFlags(ItemFlag.values());

        Optional.ofNullable(obj.get("model"))
                .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isNumber())
                .ifPresent(elm -> meta.setCustomModelData(elm.getAsInt()));

        Optional.ofNullable(obj.get("aura"))
                .filter(elm -> elm.isJsonPrimitive() && elm.getAsJsonPrimitive().isBoolean())
                .ifPresent(elm -> meta.addEnchant(Enchantment.INFINITY, 1, false));

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
