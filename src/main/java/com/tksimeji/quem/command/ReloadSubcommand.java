package com.tksimeji.quem.command;

import com.tksimeji.quem.Quem;
import com.tksimeji.quem.QuestType;
import com.tksimeji.quem.QuestTypeLoader;
import com.tksimeji.quem.ui.CLI;
import com.tksimeji.quem.util.FileUtility;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public final class ReloadSubcommand implements Subcommand {
    @Override
    public @NotNull String name() {
        return "reload";
    }

    @Override
    public @NotNull List<String> suggest(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return args.length == 1 ? QuestType.getInstances().stream().map(QuestType::getName).toList() : List.of();
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (1 < args.length) {
            sender.sendMessage(CLI.warn.append(Language.translate("command.usage", sender)
                    .append(Component.text(String.format(": /%s %s [type]", label, name())))));
            return;
        }

        if (args.length == 1 && QuestType.getInstances().stream().noneMatch(type -> type.getName().equals(args[0]))) {
            sender.sendMessage(CLI.failed.append(Language.translate("command.reload.not_found", sender, "type=" + args[0])));
            return;
        }

        long start = System.nanoTime();

        List<File> targets = args.length == 1 ? List.of(Objects.requireNonNull(QuestType.getInstance(args[0])).getFile()) : FileUtility.getFiles(Quem.directory()).stream().toList();

        if (args.length == 1) {
            QuestType.getInstances().stream()
                    .filter(type -> targets.stream().anyMatch(target -> target.equals(type.getFile())))
                    .forEach(QuestTypeLoader::unload);
        } else {
            QuestType.getInstances().forEach(QuestTypeLoader::unload);
        }

        for (File target : targets) {
            try {
                QuestTypeLoader.load(target);
            } catch (RuntimeException e) {
                sender.sendMessage(CLI.failed
                        .append(Component.text(e.getLocalizedMessage()))
                        .appendSpace()
                        .append(Component.text("(" + target.getName() + ")").color(NamedTextColor.GRAY)));
            }
        }

        long end = System.nanoTime();
        double seconds = (end - start) / 1_000_000_000.0;

        sender.sendMessage(CLI.successful.append(Language.translate("command.reload.complete", sender, "seconds=" + Quem.df1.format(seconds))));
    }
}
