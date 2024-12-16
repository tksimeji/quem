package com.tksimeji.quem.request;

import com.tksimeji.quem.Party;
import com.tksimeji.quem.ui.CLI;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PartyInvite extends Request {
    private final Party party;

    public PartyInvite(@NotNull Player sender, @NotNull Player receiver, @NotNull Party party) {
        super(sender, receiver);

        this.party = party;

        sender.sendMessage(Component.text(CLI.SEPARATOR).color(NamedTextColor.BLUE));
        sender.sendMessage(Language.translate("message.party.invite.sent", sender, "name=" + receiver.getName(), "limit=" + getLimit()));
        sender.sendMessage(Component.text(CLI.SEPARATOR).color(NamedTextColor.BLUE));

        receiver.sendMessage(Component.text(CLI.SEPARATOR).color(NamedTextColor.BLUE));
        receiver.sendMessage(Language.translate("message.party.invite", receiver, "name=" + sender.getName()));
        receiver.sendMessage(Language.translate("message.party.invite.details", receiver, "limit=" + getLimit()));
        receiver.sendMessage(Language.translate("message.click_to_accept", receiver).color(NamedTextColor.GOLD)
                .clickEvent(ClickEvent.runCommand("/quem-commands:accept " + id))
                .hoverEvent(HoverEvent.showText(Language.translate("message.party.invite.click_to_join", receiver))));
        receiver.sendMessage(Component.text(CLI.SEPARATOR).color(NamedTextColor.BLUE));
    }

    @Override
    public long ticks() {
        return 20L * 60;
    }

    @Override
    public void onAccept() {
        party.addMember(receiver);
    }

    @Override
    public void onReject() {

    }

    @Override
    public void onTimeOver() {
        this.sender.sendMessage(Component.text(getLimit() + " 秒が経過したため、" + receiver.getName() + " への Party 招待は無効になりました").color(NamedTextColor.RED));
    }
}
