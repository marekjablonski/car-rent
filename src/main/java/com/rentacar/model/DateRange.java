package com.rentacar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Inclusive date range helper used to reason about reservations.
 */
@Embeddable
public class DateRange {

    @Column(name = "date_from", nullable = false)
    private LocalDate startDate;

    @Column(name = "date_to", nullable = false)
    private LocalDate endDate;

    protected DateRange() {
        // for JPA
    }

    public DateRange(LocalDate start, LocalDate end) {
        this.startDate = Objects.requireNonNull(start, "start date is required");
        this.endDate = Objects.requireNonNull(end, "end date is required");
        if (this.endDate.isBefore(this.startDate)) {
            throw new IllegalArgumentException("End date must be on or after start date");
        }
    }

    public LocalDate start() {
        return startDate;
    }

    public LocalDate end() {
        return endDate;
    }

    public boolean overlaps(DateRange other) {
        return !(endDate.isBefore(other.startDate) || startDate.isAfter(other.endDate));
    }
}
