package com.tksimeji.quem.command;

import com.tksimeji.quem.IQuestType;
import com.tksimeji.quem.Quest;
import com.tksimeji.quem.QuestType;
import com.tksimeji.quem.ui.CLI;
import com.tksimeji.quem.util.StringUtility;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class PhaseSubcommand implements Subcommand {
    private static final @NotNull String operators = "+-*/%=";

    @Override
    public @NotNull String name() {
        return "phase";
    }

    @Override
    public @NotNull List<String> suggest(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        } else if (args.length == 2) {
            Quest quest = Quest.getInstance(Bukkit.getPlayer(args[1]));

            if (quest != null) {
                return quest.getType().getRequirements().stream().map(IQuestType.Requirement::name).toList();
            }
        } else if (args.length == 3) {
            return operators.chars()
                    .mapToObj(c -> String.valueOf((char) c))
                    .toList();
        }

        return List.of();
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 3) {
            sender.sendMessage(CLI.warn.append(Language.translate("command.usage", sender)
                    .append(Component.text(String.format(": /%s %s <player> <requirement> <phase>", label, name())))));
            return;
        }

        Quest quest = Quest.getInstance(Bukkit.getPlayer(args[0]));

        if (quest == null) {
            sender.sendMessage(CLI.failed.append(Language.translate("command.phase.quest_not_found", sender)));
            return;
        }

        IQuestType.Requirement requirement = quest.getType().getRequirements().stream()
                .filter(r -> r.name().equals(args[1]))
                .findFirst()
                .orElse(null);

        if (requirement == null) {
            sender.sendMessage(CLI.failed.append(Language.translate("command.phase.requirement_not_found", sender, "requirement=" + args[1])));
            return;
        }

        boolean formula = operators.contains(String.valueOf(args[2].charAt(0)));
        char operator = formula ? args[2].charAt(0) : '=';

        if (! operators.contains(String.valueOf(operator)) || ! StringUtility.isInteger(formula ? args[2].substring(1) : args[2])) {
            sender.sendMessage(CLI.failed.append(Language.translate("command.phase.invalid_formula", sender, "formula=" + args[2])));
            return;
        }

        int i = Integer.parseInt(args[2].substring(1));

        int phase = switch (operator) {
            case '+' -> quest.getPhase(requirement) + i;
            case '-' -> quest.getPhase(requirement) - i;
            case '*' -> quest.getPhase(requirement) * i;
            case '/' -> quest.getPhase(requirement) / Math.max(i, 1);
            case '%' -> quest.getPhase(requirement) % i;
            case '=' -> i;
            default -> throw new IllegalStateException("Unexpected value: " + operator);
        };

        quest.setPhase(requirement, phase);

        sender.sendMessage(CLI.successful.append(Language.translate("command.phase.complete", sender, "requirement=" + requirement.name(), "phase=" + phase)));
    }
}
