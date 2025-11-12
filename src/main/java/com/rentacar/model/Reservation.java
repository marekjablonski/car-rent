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
@Table(name = "reservations")
public class Reservation {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_type_id", nullable = false)
    private CarType carType;

    @Embedded
    private DateRange dateRange;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "lock_expires_at", nullable = false)
    private Instant lockExpiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    protected Reservation() {
        // for JPA
    }

    public Reservation(UUID id,
                       UUID userId,
                       CarType carType,
                       LocalDate dateFrom,
                       LocalDate dateTo,
                       Instant createdAt,
                       Instant lockExpiresAt) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.userId = Objects.requireNonNull(userId, "userId is required");
        this.carType = Objects.requireNonNull(carType, "carType is required");
        this.dateRange = new DateRange(dateFrom, dateTo);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.lockExpiresAt = Objects.requireNonNull(lockExpiresAt, "lockExpiresAt is required");
        this.status = ReservationStatus.PENDING_PAYMENT;
    }

    void assignCarType(CarType carType) {
        this.carType = carType;
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
