package com.tksimeji.quem;

import com.tksimeji.quem.command.AcceptCommand;
import com.tksimeji.quem.command.QuemCommand;
import com.tksimeji.quem.listener.InventoryListener;
import com.tksimeji.quem.listener.PlayerListener;
import com.tksimeji.quem.listener.ServerListener;
import com.tksimeji.quem.schedule.DailyShuffleRunnable;
import com.tksimeji.quem.ui.CLI;
import com.tksimeji.quem.util.FileUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public final class Quem extends JavaPlugin {
    private static Quem instance;

    public static final @NotNull String extension = "json";

    public static final @NotNull SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yy");

    public static final @NotNull DecimalFormat df1 = new DecimalFormat("#.##");

    public static @NotNull Quem plugin() {
        return Quem.instance;
    }

    public static @NotNull String version() {
        return plugin().getPluginMeta().getVersion();
    }

    public static @NotNull File directory() {
        return plugin().getDataFolder();
    }

    public static @NotNull ComponentLogger logger() {
        return plugin().getComponentLogger();
    }

    @Override
    public void onEnable() {
        Quem.instance = this;

        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new ServerListener(), this);

        Bukkit.getCommandMap().register("accept", "quem-commands", new AcceptCommand());

        getCommand("quem").setExecutor(new QuemCommand());

        new DailyShuffleRunnable();

        logger().info(Component.text(" _____       ").color(TextColor.color(69, 104, 219)));
        logger().info(Component.text("|     |_____ ").color(TextColor.color(96, 105, 210)).append(Component.text("    Quem - " + version()).color(NamedTextColor.WHITE)));
        logger().info(Component.text("|  |  |     |").color(TextColor.color(120, 105, 200)));
        logger().info(Component.text("|__  _|_|_|_|").color(TextColor.color(146, 106, 191)).append(Component.text("    Help poor children in Uganda!").color(NamedTextColor.GRAY)));
        logger().info(Component.text("   |__|      ").color(TextColor.color(175, 106, 179)).append(Component.text("    ").append(Component.text("https://iccf-holland.org/").color(NamedTextColor.BLUE).decorate(TextDecoration.UNDERLINED).clickEvent(ClickEvent.openUrl("https://iccf-holland.org/")))));
        logger().info(Component.text().build());

        if (! directory().exists() && ! directory().mkdir()) {
            logger().error(CLI.failed.append(Component.text("Failed to create plugin directory.").color(NamedTextColor.RED)));
        }

        FileUtility.getFiles(directory()).forEach(file -> {
            try {
                QuestTypeLoader.load(file);
            } catch (RuntimeException e) {
                logger().error(CLI.failed
                        .append(Component.text(e.getLocalizedMessage()))
                        .appendSpace()
                        .append(Component.text("(" + file.getName() + ")").color(NamedTextColor.GRAY)));
            }
        });
    }
}
