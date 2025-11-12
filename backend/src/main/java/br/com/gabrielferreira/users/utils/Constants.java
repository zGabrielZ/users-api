package br.com.gabrielferreira.users.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class Constants {

    private Constants() {}

    public static  String trimIfNotBlank(String value) {
        if (StringUtils.isNotBlank(value)) {
            return value.trim();
        }
        return value;
    }

    public static OffsetDateTime now() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

    public static boolean hasSpecialCharacter(String value) {
        String specialCharacters = "!@#$%^&*()-+";
        for (char c : value.toCharArray()) {
            if (specialCharacters.indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasUppercaseCharacter(String value) {
        for (char c : value.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasLowercaseCharacter(String value) {
        for (char c : value.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasDigitCharacter(String value) {
        for (char c : value.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }
}
