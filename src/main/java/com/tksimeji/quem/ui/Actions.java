package com.tksimeji.quem.ui;

import com.google.common.collect.Lists;
import com.tksimeji.quem.Party;
import com.tksimeji.quem.Quest;
import com.tksimeji.quem.util.ComponentUtility;
import com.tksimeji.visualkit.Visualkit;
import com.tksimeji.visualkit.lang.Language;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;

public final class Actions extends HashMap<Integer, Actions.Action> {
    private static final @NotNull Actions instance = new Actions();

    public static @NotNull Actions instance() {
        return instance;
    }

    public static @Nullable Actions.Action refer(int slot) {
        return instance.get(slot);
    }

    private static <T extends Action> @NotNull T register(@NotNull T button) {
        instance.put(button.slot(), button);
        return button;
    }

    public static @NotNull Actions.Action GAME_MENU = instance.register(new Action() {
        @Override
        public @NotNull Material type() {
            return Material.COMPASS;
        }

        @Override
        public @NotNull String title() {
            return "button.game_menu";
        }

        @Override
        public int slot() {
            return 6;
        }

        @Override
        public void action(@NotNull Player player) {

        }
    });

    public static @NotNull Actions.Action PARTY = instance.register(new Action() {
        @Override
        public @NotNull Material type() {
            return Material.CHEST_MINECART;
        }

        @Override
        public @NotNull String title() {
            return "button.party";
        }

        @Override
        public int slot() {
            return 7;
        }

        @Override
        public void action(@NotNull Player player) {
            if (Party.getInstance(player) != null) {
                new PartyMenuUI(player);
            } else {
                new PartyCreateUI(player);
            }
        }
    });

    public static @NotNull Actions.Action QUEST = instance.register(new Action() {
        @Override
        public @NotNull Material type() {
            return Material.ENCHANTED_BOOK;
        }

        @Override
        public @NotNull String title() {
            return "button.quest";
        }

        @Override
        public int slot() {
            return 8;
        }

        @Override
        public void action(@NotNull Player player) {
            if (Quest.getInstance(player) == null) {
                new QuestUI(player);
            } else {
                new QuestControllerUI(player);
            }
        }
    });

    private Actions() {}

    public interface Action {
        @NotNull Material type();

        @NotNull String title();

        int slot();

        void action(@NotNull Player player);

        default @NotNull ItemStack asItemStack(@NotNull Player player) {
            ItemStack itemStack = new ItemStack(type(), 1);

            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(ComponentUtility.empty().append(Language.translate(title(), player)));

            Arrays.stream(Lists.newArrayList(Registry.ATTRIBUTE).toArray(new Attribute[0])).forEach(attribute -> {
                itemMeta.removeAttributeModifier(attribute);
                itemMeta.addAttributeModifier(attribute, new AttributeModifier(new NamespacedKey(Visualkit.plugin(), attribute.getKey().getKey()), 0, AttributeModifier.Operation.ADD_NUMBER));
            });

            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
    }
}
