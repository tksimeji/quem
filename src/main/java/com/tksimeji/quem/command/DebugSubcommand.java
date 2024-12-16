package com.tksimeji.quem.command;

import com.tksimeji.quem.DebugQuest;
import com.tksimeji.quem.Party;
import com.tksimeji.quem.QuestType;
import com.tksimeji.quem.ui.CLI;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public final class DebugSubcommand implements Subcommand {
    @Override
    public @NotNull String name() {
        return "debug";
    }

    @Override
    public @NotNull List<String> suggest(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return args.length == 1 ? QuestType.getInstances().stream().map(QuestType::getName).toList() : List.of();
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (! (sender instanceof Player player)) {
            sender.sendMessage(CLI.warn.append(Language.translate("command.game_only", sender)));
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(CLI.warn.append(Language.translate("command.usage", player).color(NamedTextColor.RED)
                    .append(Component.text(String.format(": /%s %s <type>", label, name())))));
            return;
        }

        QuestType type = QuestType.getInstance(args[0]);

        if (type == null) {
            sender.sendMessage(CLI.failed.append(Language.translate("command.debug.not_found", player, "type=" + args[0])));
            return;
        }

        Party party = Party.getInstance(player);

        if (party != null && party.hasQuest()) {
            sender.sendMessage(CLI.warn.append(Language.translate("command.debug.already", player).color(NamedTextColor.RED)));
            return;
        }

        new DebugQuest(type, Optional.ofNullable(party).orElse(new Party(player)));
    }
}
