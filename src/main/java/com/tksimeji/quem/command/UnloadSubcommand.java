package com.tksimeji.quem.command;

import com.tksimeji.quem.QuestType;
import com.tksimeji.quem.QuestTypeLoader;
import com.tksimeji.quem.ui.CLI;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class UnloadSubcommand implements Subcommand {
    @Override
    public @NotNull String name() {
        return "unload";
    }

    @Override
    public @NotNull List<String> suggest(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return args.length == 1 ? QuestType.getInstances().stream().map(QuestType::getName).toList() : List.of();
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage(CLI.warn.append(Language.translate("command.usage", sender)
                    .append(Component.text(String.format(": /%s %s <type>", label, name())))));
            return;
        }

        QuestType type = QuestType.getInstance(args[0]);

        if (type == null) {
            sender.sendMessage(CLI.failed.append(Language.translate("command.unload.not_found", sender, "type=" + args[0])));
            return;
        }

        QuestTypeLoader.unload(type);

        sender.sendMessage(CLI.successful.append(Language.translate("command.unload.complete", sender, "type=" + args[0])));
    }
}
