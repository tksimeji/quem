package com.tksimeji.quem.command;

import com.tksimeji.quem.Quem;
import com.tksimeji.quem.QuestType;
import com.tksimeji.quem.QuestTypeLoader;
import com.tksimeji.quem.ui.CLI;
import com.tksimeji.quem.util.FileUtility;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public final class LoadSubcommand implements Subcommand {
    @Override
    public @NotNull String name() {
        return "load";
    }

    @Override
    public @NotNull List<String> suggest(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return FileUtility.getFiles(Quem.directory()).stream()
                .filter(file -> QuestType.getInstances().stream().noneMatch(type -> type.getFile().equals(file)))
                .map(file -> Quem.directory().toPath().relativize(file.toPath()).toString())
                .toList();
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage(CLI.warn.append(Language.translate("command.usage", sender)
                    .append(Component.text(String.format(": /%s %s <file>", label, name())))));
            return;
        }

        File file = new File(Quem.directory(), args[0]);

        try {
            QuestTypeLoader.load(file);
        } catch (RuntimeException e) {
            sender.sendMessage(CLI.failed.append(Component.text(e.getLocalizedMessage())));
            return;
        }

        sender.sendMessage(CLI.successful.append(Language.translate("command.load.complete", sender, "file=" + file.getName())));
    }
}
