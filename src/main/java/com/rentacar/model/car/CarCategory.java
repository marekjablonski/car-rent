package com.rentacar.model.car;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum CarCategory {
    SEDAN,
    SUV,
    VAN;

    @JsonCreator
    public static CarCategory fromJson(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new IllegalArgumentException("type must be provided");
        }
        return CarCategory.valueOf(rawValue.trim().toUpperCase(Locale.ROOT));
    }

    @JsonValue
    public String toJson() {
        String lower = name().toLowerCase(Locale.ROOT);
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
