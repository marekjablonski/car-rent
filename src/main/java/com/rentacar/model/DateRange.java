package com.rentacar.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Inclusive date range helper used to reason about reservations.
 */
public record DateRange(LocalDate start, LocalDate end) {

    public DateRange {
        Objects.requireNonNull(start, "start date is required");
        Objects.requireNonNull(end, "end date is required");
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date must be on or after start date");
        }
    }

    public boolean overlaps(DateRange other) {
        return !(end.isBefore(other.start) || start.isAfter(other.end));
    }
}
