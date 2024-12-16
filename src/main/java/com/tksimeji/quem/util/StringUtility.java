package com.tksimeji.quem.util;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtility {
    public static @NotNull String empty() {
        return "";
    }

    public static @NotNull String spaces(int length) {
        return " ".repeat(Math.max(0, length));
    }

    public static boolean isInteger(String string) {
        Pattern pattern = Pattern.compile("^[+-]?\\d+$");
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    public static boolean isUuid(String s) {
        try {
            UUID.fromString(s);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
