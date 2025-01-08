package com.tksimeji.quem.ui;

import com.tksimeji.quem.IQuestType;
import com.tksimeji.visualkit.SharedPanelUI;
import com.tksimeji.quem.Quem;
import com.tksimeji.quem.Quest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

public final class QuestPanel extends SharedPanelUI {
    private final Quest quest;

    private final int requirement;

    private int phase;
    private int members;

    private final List<Player> players;

    public QuestPanel(@NotNull Quest quest) {
        this.quest = quest;

        requirement = quest.getType().getRequirements().stream().mapToInt(IQuestType.Requirement::amount).sum();
        phase = 0;
        members = quest.getPlayers().size();
        players = quest.getPlayers();

        setTitle(Component.text("Q").color(TextColor.color(69, 104, 219)).decorate(TextDecoration.BOLD)
                .append(Component.text("u").color(TextColor.color(96, 105, 210)))
                .append(Component.text("e").color(TextColor.color(146, 106, 191)))
                .append(Component.text("m").color(TextColor.color(175, 106, 179))));

        setLine(0, Component.text(Quem.sdf1.format(new Date())).color(NamedTextColor.GRAY));
        setLine(2, Component.text("Quest: ").append(quest.getType().getTitle()));
        setLine(3, Component.text("Progress: ")
                .append(Component.text("${phase}").color(NamedTextColor.YELLOW))
                .append(Component.text("/").color(NamedTextColor.DARK_GRAY))
                .append(Component.text("${requirement}").color(NamedTextColor.GREEN)));
        setLine(5, Component.text("Party (${members}):"));
        setLine(7 + members, Component.text("iccf-holland.org").color(NamedTextColor.BLUE).decorate(TextDecoration.UNDERLINED));
    }

    @Override
    public void onTick() {
        phase = quest.getType().getRequirements().stream().mapToInt(quest::getPhase).sum();
        members = quest.getPlayers().size();

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            setLine(6 + i, Component.text().appendSpace()
                    .append(Component.text(player.getName()).color(quest.getPlayers().contains(player) ? NamedTextColor.AQUA : NamedTextColor.RED))
                    .appendSpace()
                    .append(quest.getPlayers().contains(player) ? Component.text((int) player.getHealth()).color(NamedTextColor.GREEN).append(Component.text("♥").color(NamedTextColor.RED)) :
                            Component.text("Disconnected").color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
                    .build());
        }
    }
}
