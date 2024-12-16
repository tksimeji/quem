package com.tksimeji.quem;

import com.tksimeji.quem.element.CommandScript;
import com.tksimeji.quem.ui.CLI;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class DebugQuest extends Quest {
    private final @NotNull List<Player> debuggers;

    public DebugQuest(@NotNull QuestType type, @NotNull Party party) {
        super(type, party);
        debuggers = party.getMembers();
        debuggers.forEach(debugger -> debugger.sendMessage(Language.translate("message.debug.announce", debugger).color(NamedTextColor.RED)));
    }

    @Override
    public void setPhase(int phase) {
        int old = this.getPhase();
        super.setPhase(phase);
        debuggers.forEach(debugger -> debugger.sendMessage(CLI.warn.append(Language.translate("message.debug.phase", debugger, "from=" + old, "to=" + phase).color(NamedTextColor.YELLOW))));
    }

    @Override
    public void onEnd(@NotNull EndReason reason) {
        super.onEnd(reason);
        debuggers.forEach(debugger -> debugger.sendMessage(CLI.warn.append(Language.translate("message.debug.end", debugger, "reason=" + reason.toString().toLowerCase()).color(NamedTextColor.YELLOW))));
    }

    public void onScripting(@NotNull CommandScript script) {
        debuggers.forEach(debugger -> debugger.sendMessage(CLI.warn.append(Language.translate("message.debug.script", debugger, "script=" + script.getName()).color(NamedTextColor.YELLOW))));
    }

    @Override
    public void call(CommandScript.@NotNull Trigger trigger) {
        super.call(trigger);
    }
}
