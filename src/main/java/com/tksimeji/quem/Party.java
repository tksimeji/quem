package com.tksimeji.quem;

import com.tksimeji.quem.ui.PartyCreateUI;
import com.tksimeji.quem.ui.PartyInviteUI;
import com.tksimeji.quem.ui.PartyMenuUI;
import com.tksimeji.quem.ui.PartyUI;
import com.tksimeji.visualkit.Visualkit;
import com.tksimeji.visualkit.lang.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class Party implements Iterable<Player> {
    private static final Set<Party> instances = new HashSet<>();

    public static final int MAX_PLAYERS = 6;

    public static Party getInstance(@NotNull Player player) {
        return Party.instances.stream().filter(i -> i.isMember(player)).findFirst().orElse(null);
    }

    private static @NotNull Component getPrefix() {
        return Component.text("Party > ").color(NamedTextColor.BLUE);
    }

    private Player leader;
    private Quest quest;

    private final List<Player> members = new ArrayList<>();

    public Party(@NotNull Player leader) {
        members.add(this.leader = leader);
        instances.add(this);
    }

    public @NotNull Player getLeader() {
        return leader;
    }

    public void setLeader(@NotNull Player leader) {
        addMember(this.leader = leader);
    }

    public @Nullable Quest getQuest() {
        return quest;
    }

    public void setQuest(@Nullable Quest quest) {
        this.quest = quest;
        applyToUI();
    }

    public @NotNull List<Player> getMembers() {
        return new ArrayList<>(members);
    }

    public void addMember(@NotNull Player member) {
        if (isMember(member)) {
            return;
        }

        if (quest != null) {
            throw new UnsupportedOperationException();
        }

        Party old = Party.getInstance(member);

        if (old != null) {
            old.removeMember(member);
        }

        members.add(member);
        members.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

        applyToUI();

        forEach(player -> player.sendMessage(getPrefix()
                .append(Language.translate("message.party.join", player, "name=" + member.getName()))));
    }

    public void removeMember(@NotNull Player member) {
        members.remove(member);

        if (quest != null) {
            quest.getPanel().removeAudience(member);
        }

        if (members.isEmpty() || member == leader) {
            disband();
            return;
        }

        applyToUI();

        forEach(player -> player.sendMessage(getPrefix()
                .append(Language.translate("message.party.quit", player, "name=" + member.getName()))));
    }

    public boolean isMember(@Nullable Player player) {
        return members.contains(player);
    }

    public boolean hasQuest() {
        return quest != null;
    }

    private void applyToUI() {
        Visualkit.sessions().stream()
                .filter(ui -> ui instanceof PartyCreateUI ui2 && isMember(ui2.getPlayer()))
                .forEach(ui -> new PartyMenuUI(((PartyCreateUI) ui).getPlayer()));

        Visualkit.sessions().stream()
                .filter(ui -> ui instanceof PartyMenuUI ui2 && ui2.party() == this)
                .forEach(ui -> {
                    if (isMember(((PartyMenuUI) ui).getPlayer())) {
                        new PartyMenuUI(((PartyMenuUI) ui).getPlayer());
                    } else {
                        ((PartyMenuUI) ui).close();
                    }
                });

        Visualkit.sessions().stream()
                .filter(ui -> ui instanceof PartyInviteUI ui2 && ui2.party() == this)
                .forEach(ui -> {
                    if (isMember(((PartyInviteUI) ui).getPlayer())) {
                        new PartyMenuUI(((PartyInviteUI) ui).getPlayer());
                    } else {
                        ((PartyInviteUI) ui).close();
                    }
                });
    }

    public int size() {
        return members.size();
    }

    @Override
    public @NotNull Iterator<Player> iterator() {
        return members.iterator();
    }

    public void disband() {
        if (quest != null) {
            quest.cancel();
        }

        forEach(player -> player.sendMessage(Language.translate("message.party.disbanded", player).color(NamedTextColor.RED)));

        getMembers().forEach(this::removeMember);

        Visualkit.sessions().stream()
                .filter(ui -> ui instanceof PartyUI ui2 && ui2.party() == this)
                .forEach(ui -> ((PartyUI) ui).close());

        Party.instances.remove(this);
    }
}
