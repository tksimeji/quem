package com.tksimeji.quem.ui;

import com.tksimeji.quem.Party;
import com.tksimeji.quem.Quest;
import com.tksimeji.quem.QuestType;
import com.tksimeji.quem.element.Category;
import com.tksimeji.visualkit.ChestUI;
import com.tksimeji.visualkit.api.*;
import com.tksimeji.visualkit.element.VisualkitElement;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class QuestUI extends ChestUI {
    private static final int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};

    @Element(45)
    private final VisualkitElement previous = VisualkitElement.create(Material.ARROW)
            .title(Language.translate("ui.previous", player).color(NamedTextColor.GREEN));

    @Element(48)
    private final VisualkitElement search = VisualkitElement.create(Material.OAK_SIGN)
            .title(Language.translate("ui.quest.search", player).color(NamedTextColor.GREEN))
            .lore(Language.translate("ui.quest.search.description", player).color(NamedTextColor.GRAY));

    @Element(49)
    private final VisualkitElement exit = VisualkitElement.create(Material.BARRIER)
            .title(Language.translate("ui.close", player).color(NamedTextColor.RED))
            .lore(Language.translate("ui.close.description", player).color(NamedTextColor.GRAY));

    @Element(50)
    private final VisualkitElement filter = VisualkitElement.create(Material.HOPPER)
            .title(Language.translate("ui.quest.filter", player).color(NamedTextColor.GREEN));

    @Element(53)
    private final VisualkitElement next = VisualkitElement.create(Material.ARROW)
            .title(Language.translate("ui.next", player).color(NamedTextColor.GREEN));

    private final int page;

    private final String query;
    private final Category category;

    private final List<QuestType> quests = QuestType.getInstances().stream().toList();
    private final Map<Integer, QuestType> map = new HashMap<>();

    public QuestUI(@NotNull Player player) {
        this(player, null, null, 0);
    }

    public QuestUI(@NotNull Player player, String query, Category category) {
        this(player, query, category, 0);
    }

    public QuestUI(@NotNull Player player, @Nullable String query, @Nullable Category category, int page) {
        super(player);

        this.query = query == null ? "" : query;
        this.category = category;

        this.page = page;

        Arrays.stream(QuestUI.slots).forEach(slot -> setElement(slot, VisualkitElement.create(Material.GRAY_DYE)
                .title(Component.text("???").color(NamedTextColor.RED))
                .lore(Language.translate("ui.quest.empty.description", player).color(NamedTextColor.GRAY))));

        filter.lore(Language.translate("ui.quest.filter.description", player),
                Component.empty(),
                Language.translate("ui.quest.all", player).color(category == null ? NamedTextColor.GREEN: NamedTextColor.DARK_GRAY),
                Language.translate("ui.quest.general", player).color(category == Category.GENERAL ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY),
                Language.translate("ui.quest.story", player).color(category == Category.STORY ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY),
                Language.translate("ui.quest.daily", player).color(category == Category.DAILY ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY),
                Language.translate("ui.quest.event", player).color(category == Category.EVENT ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY));

        int i = 0;

        List<QuestType> quests = this.quests.stream()
                .filter(q -> q.hasPlayRights(player))
                .filter(q -> PlainTextComponentSerializer.plainText().serialize(q.getTitle()).toLowerCase().contains(this.query.toLowerCase()))
                .filter(q -> category == null || q.getCategory() == category)
                .toList();

        for (QuestType quest : quests.subList(page * QuestUI.slots.length, Math.min((page + 1) * QuestUI.slots.length, quests.size()))) {
            int slot = QuestUI.slots[i++];
            map.put(slot, quest);
            setElement(slot, quest.getIcon());
        }
    }

    @Override
    public @NotNull Component title() {
        return Language.translate("ui.quest.title", player);
    }

    @Override
    public @NotNull Size size() {
        return Size.SIZE_54;
    }

    @Handler(slot = 45)
    public void onBack() {
        new QuestUI(player, query, category, Math.max(page - 1, 0));
    }

    @Handler(slot = 49)
    public void onExit() {
        close();
    }

    @Handler(slot = 50)
    public void onFilter(@NotNull Mouse mouse) {
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1.0f, 1.4f);

        Category category;

        if (mouse == Mouse.LEFT) {
            category = switch (this.category) {
                case null -> Category.GENERAL;
                case Category.GENERAL -> Category.STORY;
                case Category.STORY -> Category.DAILY;
                case Category.DAILY -> Category.EVENT;
                case Category.EVENT -> null;
            };
        } else {
            category = switch (this.category) {
                case null -> Category.EVENT;
                case Category.EVENT -> Category.DAILY;
                case Category.DAILY -> Category.STORY;
                case Category.STORY -> Category.GENERAL;
                case Category.GENERAL -> null;
            };
        }

        new QuestUI(player, query, category);
    }

    @Handler(slot = 53)
    public void onNext() {
        new QuestUI(player, query, category, Math.min(page + 1, quests.size() / QuestUI.slots.length));
    }

    @Handler(asm = {@Asm(from = 10, to = 16), @Asm(from = 19, to = 25), @Asm(from = 28, to = 34)})
    public void onPlay(int slot) {
        QuestType type = map.get(slot);

        if (type == null) {
            return;
        }

        Party party = Party.getInstance(player);

        if (party != null && player != party.getLeader()) {
            player.playSound(player, Sound.ENTITY_PLAYER_TELEPORT, 1.0f, 0.1f);
            player.sendMessage(Language.translate("message.quest.permission_error", player).color(NamedTextColor.RED));
            return;
        }

        if (party != null && party.hasQuest()) {
            player.playSound(player, Sound.ENTITY_PLAYER_TELEPORT, 1.0f, 0.1f);
            player.sendMessage(Language.translate("message.quest.already", player).color(NamedTextColor.RED));
            return;
        }

        if (party != null && ! party.getMembers().stream().allMatch(type::hasPlayRights)) {
            player.playSound(player, Sound.ENTITY_PLAYER_TELEPORT, 1.0f, 0.1f);
            player.sendMessage(Language.translate("message.quest.conditions_not_met", player).color(NamedTextColor.RED));
            return;
        }

        if (party != null && party.size() < type.getPlayerLimit()) {
            player.playSound(player, Sound.ENTITY_PLAYER_TELEPORT, 1.0f, 0.1f);
            player.sendMessage(Language.translate("message.quest.player_limit_over", player).color(NamedTextColor.RED));
            return;
        }

        new ConfirmUI(player, new BukkitRunnable() {
            @Override
            public void run() {
                new Quest(type, Optional.ofNullable(party).orElse(new Party(player)));
            }
        }, new BukkitRunnable() {
            @Override
            public void run() {
                new QuestUI(player);
            }
        });
    }
}
