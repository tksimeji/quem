package com.tksimeji.quem.command;

import com.tksimeji.quem.request.Request;
import com.tksimeji.quem.ui.CLI;
import com.tksimeji.quem.util.StringUtility;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class AcceptCommand extends Command {
    public AcceptCommand() {
        super("accept");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (! (sender instanceof Player player)) {
            sender.sendMessage(CLI.warn.append(Language.translate("command.game_only", sender)));
            return true;
        }

        if (args.length != 1 || ! StringUtility.isUuid(args[0])) {
            sender.sendMessage(CLI.warn.append(Language.translate("command.usage", sender)
                    .append(Component.text(": /" + label + " <request>"))));
            return true;
        }

        Request request = Request.getInstance(UUID.fromString(args[0]));

        if (request == null || request.getReceiver() != player) {
            sender.sendMessage(CLI.failed.append(Language.translate("command.accept.permission_error", sender)));
            return true;
        }

        request.onAccept();
        request.onEnd();
        return true;
    }
}
