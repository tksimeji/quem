package com.tksimeji.quem.command;

import com.tksimeji.quem.QuestType;
import com.tksimeji.quem.ui.CLI;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class RevokeSubcommand implements Subcommand {
    @Override
    public @NotNull String name() {
        return "revoke";
    }

    @Override
    public @NotNull List<String> suggest(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        } else if (args.length == 2) {
            Player player = Bukkit.getPlayer(args[0]);

            if (player != null) {
                return QuestType.getInstances().stream().filter(type -> type.isGranted(player)).map(QuestType::getName).toList();
            }
        }

        return List.of();
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 2) {
            sender.sendMessage(CLI.warn.append(Language.translate("command.usage", sender)
                    .append(Component.text(String.format(": /%s %s <player> <type>", label, name())))));
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            sender.sendMessage(CLI.failed.append(Language.translate("command.offline", sender, "name=" + args[0])));
            return;
        }

        QuestType type = QuestType.getInstance(args[1]);

        if (type == null) {
            sender.sendMessage(CLI.failed.append(Language.translate("command.revoke.not_found", sender, "type=" + args[1])));
            return;
        }

        if (! type.isGranted(player)) {
            sender.sendMessage(CLI.warn.append(Language.translate("command.revoke.already", sender, "player=" + args[0], "type=" + args[1])));
            return;
        }

        type.revoke(player);
        sender.sendMessage(CLI.successful.append(Language.translate("command.revoke.complete", sender, "player=" + args[0], "type=" + args[1])));
    }
}
