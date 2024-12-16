package com.tksimeji.quem.util;

import com.tksimeji.quem.Quem;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class FileUtility {
    public static @NotNull Set<File> getFiles(@NotNull File directory) {
        Set<File> set = new HashSet<>();

        if (! directory.isDirectory()) {
            throw new IllegalArgumentException();
        }

        File[] files = Optional.ofNullable(directory.listFiles()).orElse(new File[0]);

        for (File file : files) {
            if (file.isDirectory()) {
                set.addAll(getFiles(file));
            } else if (file.getName().endsWith("." + Quem.extension)) {
                set.add(file);
            }
        }

        return set;
    }

    public static @NotNull String asName(@NotNull File file) {
        String name = Quem.directory().toURI().relativize(file.toURI()).getPath();

        int lastIndexOfDot = name.lastIndexOf('.');

        if (0 < lastIndexOfDot) {
            name = name.substring(0, lastIndexOfDot);
        }

        return name;
    }
}
