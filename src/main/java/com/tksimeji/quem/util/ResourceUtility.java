package com.tksimeji.quem.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tksimeji.quem.Quem;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public final class ResourceUtility {
    public static @NotNull File getResource(@NotNull String path) {
        return new File(Quem.plugin().getDataFolder(), path);
    }

    public static @NotNull JsonElement getJsonResource(@NotNull String path) {
        try {
            return JsonParser.parseReader(new FileReader(ResourceUtility.getResource(path)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
