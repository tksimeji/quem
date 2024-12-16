package com.tksimeji.quem.command;

import com.tksimeji.quem.QuestType;
import com.tksimeji.quem.ui.CLI;
import com.tksimeji.quem.util.ComponentUtility;
import com.tksimeji.quem.util.StringUtility;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public final class ListSubcommand implements Subcommand {
    private static final int i = 8;

    @Override
    public @NotNull String name() {
        return "list";
    }

    @Override
    public @NotNull List<String> suggest(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (1 < args.length || (args.length == 1 && ! StringUtility.isInteger(args[0]))) {
            sender.sendMessage(CLI.warn.append(Language.translate("command.usage", sender)
                    .append(Component.text(String.format(": /%s %s [page]", label, name())))));
            return;
        }

        List<QuestType> quests = QuestType.getInstances().stream()
                .sorted(Comparator.comparing(QuestType::getName))
                .toList();

        int page = args.length == 1 ? Integer.parseInt(args[0]) : 1;
        int pages = quests.size() / i + (quests.size() % i != 0 ? 1 : 0);

        if (page <= 0 || pages < page) {
            sender.sendMessage(CLI.warn.append(Language.translate("command.list.invalid", sender, "page=" + page)));
            return;
        }

        sender.sendMessage(Component.empty());

        for (QuestType quest : quests.subList((page - 1) * i, Math.min(page * i, quests.size()))) {
            sender.sendMessage(Component.text()
                    .append(quest.getTitle().hoverEvent(HoverEvent.showText(ComponentUtility.connect(quest.getDescription()))))
                    .appendSpace()
                    .append(Component.text("(" + quest.getName() + ")").color(NamedTextColor.GRAY)));
        }

        TextComponent.Builder footer = Component.text();

        if (1 < page) {
            footer.append(Component.text("◀ ").color(NamedTextColor.WHITE)
                    .appendSpace()
                    .clickEvent(ClickEvent.runCommand(String.format("/%s %s %s", label, name(), page - 1))));
        }

        footer.append(Component.text("[").color(NamedTextColor.GRAY));

        int[] around = CLI.around(page, 1, pages);

        for (int i = 0; i < around.length; i ++) {
            int j = around[i];

            TextComponent.Builder c = Component.text().append(Component.text(j));

            if (j == page) {
                c.decorate(TextDecoration.UNDERLINED);
            } else {
                c.clickEvent(ClickEvent.runCommand(String.format("/%s %s %s", label, name(), j)));
            }

            if (i + 1 < around.length) {
                c.append(Component.text(" | ").color(NamedTextColor.GRAY));
            }

            footer.append(c);
        }

        footer.append(Component.text("]").color(NamedTextColor.GRAY));

        if (page < pages) {
            footer.append(Component.text(" ▶").color(NamedTextColor.WHITE)
                    .appendSpace()
                    .clickEvent(ClickEvent.runCommand(String.format("/%s %s %s", label, name(), page + 1))));
        }

        sender.sendMessage(Component.empty());
        sender.sendMessage(CLI.info.append(Language.translate("command.list.loads", sender, "count=" + quests.size())));
        sender.sendMessage(footer);
    }
}
