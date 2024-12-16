package com.tksimeji.quem.command;

import com.tksimeji.quem.ui.CLI;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class QuemCommand implements CommandExecutor, TabCompleter {
    private final Map<String, Subcommand> subcommands = new HashMap<>();

    public QuemCommand()
    {
        register(new DebugSubcommand());
        register(new GrantSubcommand());
        register(new ListSubcommand());
        register(new LoadSubcommand());
        register(new PhaseSubcommand());
        register(new ReloadSubcommand());
        register(new RevokeSubcommand());
        register(new UnloadSubcommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(CLI.warn.append(Language.translate("command.usage", sender)
                    .append(Component.text(": /" + label + " <subcommand> [args...]"))));
            return true;
        }

        if (! subcommands.containsKey(args[0])) {
            sender.sendMessage(CLI.failed.append(Language.translate("command.quem.unknown", sender, "command=" + args[0])));
            return true;
        }

        Subcommand subcommand = subcommands.get(args[0]);

        String[] args2 = new String[args.length - 1];
        System.arraycopy(args, 1, args2, 0, args2.length);
        subcommand.execute(sender, command, label, args2);
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> suggest = new ArrayList<>();

        if (args.length == 1) {
            suggest.addAll(subcommands.keySet());
        }

        if (subcommands.containsKey(args[0])) {
            String[] args2 = new String[args.length - 1];
            System.arraycopy(args, 1, args2, 0, args2.length);
            suggest.addAll(subcommands.get(args[0]).suggest(sender, command, args[0], args2));
        }

        return suggest;
    }

    private void register(@NotNull Subcommand subcommand) {
        subcommands.put(subcommand.name(), subcommand);
    }
}
