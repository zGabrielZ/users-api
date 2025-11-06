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
}
