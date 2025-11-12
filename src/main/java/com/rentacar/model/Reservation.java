package com.rentacar.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Accessors(fluent = true)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reservation {

    @EqualsAndHashCode.Include
    @ToString.Include
    private final UUID id;
    private final UUID userId;
    private final UUID carTypeId;
    private final DateRange dateRange;
    private final Instant createdAt;
    private final Instant lockExpiresAt;
    private ReservationStatus status;
    private String paymentId;
    private Instant confirmedAt;

    public Reservation(UUID id,
                       UUID userId,
                       UUID carTypeId,
                       LocalDate dateFrom,
                       LocalDate dateTo,
                       Instant createdAt,
                       Instant lockExpiresAt) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.userId = Objects.requireNonNull(userId, "userId is required");
        this.carTypeId = Objects.requireNonNull(carTypeId, "carTypeId is required");
        this.dateRange = new DateRange(dateFrom, dateTo);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.lockExpiresAt = Objects.requireNonNull(lockExpiresAt, "lockExpiresAt is required");
        this.status = ReservationStatus.PENDING_PAYMENT;
    }

    public boolean overlaps(DateRange range) {
        return dateRange.overlaps(range);
    }

    public boolean isActive(Instant now) {
        return status == ReservationStatus.CONFIRMED || now.isBefore(lockExpiresAt);
    }

    public void confirm(String paymentId, Instant confirmationTime) {
        if (status == ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Reservation already confirmed");
        }
        this.status = ReservationStatus.CONFIRMED;
        this.paymentId = Objects.requireNonNull(paymentId, "paymentId is required");
        this.confirmedAt = Objects.requireNonNull(confirmationTime, "confirmationTime is required");
    }
}
