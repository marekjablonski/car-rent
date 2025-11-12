package com.rentacar.model;

import jakarta.persistence.*;
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
@Entity
public class Reservation {
    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private final UUID id;
    private final UUID userId;
    @ManyToOne
    @JoinColumn(name = "car_type_id")
    private final CarType carType;
    @Embedded
    private final DateRange dateRange;
    private final Instant createdAt;
    private final Instant lockExpiresAt;
    private ReservationStatus status;
    private String paymentId;
    private Instant confirmedAt;

    public Reservation(UUID id,
                       UUID userId,
                       CarType carType,
                       LocalDate dateFrom,
                       LocalDate dateTo,
                       Instant createdAt,
                       Instant lockExpiresAt) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.userId = Objects.requireNonNull(userId, "userId is required");
        this.carType = Objects.requireNonNull(carType, "carTypeId is required");
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
