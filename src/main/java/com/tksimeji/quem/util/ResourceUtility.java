package com.tksimeji.quem.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tksimeji.quem.Quem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public final class ResourceUtility {
    public static @NotNull File getResource(@NotNull String path) {
        return new File(Quem.plugin().getDataFolder(), path);
    }

    public static @NotNull JsonElement getJsonResource(@NotNull File file) {
        try (FileReader reader = new FileReader(file)) {
            return JsonParser.parseReader(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull YamlConfiguration getYamlResource(@NotNull File file) {
        return YamlConfiguration.loadConfiguration(file);
    }
}
