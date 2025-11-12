package com.rentacar.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Getter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "car_types")
public class CarType {

    @EqualsAndHashCode.Include
    @Id
    private UUID id;

    private CarCategory category;

    private String pictureUrl;

    private BigDecimal pricePerDay;

    private int seats;

    @OneToMany(mappedBy = "car_types", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Car> cars = new HashSet<>();

    @OneToMany(mappedBy = "car_types", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reservation> reservations = new HashSet<>();

    protected CarType() {
        // for JPA
    }

    public CarType(UUID id,
                   CarCategory category,
                   String pictureUrl,
                   BigDecimal pricePerDay,
                   int seats) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.category = Objects.requireNonNull(category, "category is required");
        this.pictureUrl = Objects.requireNonNull(pictureUrl, "pictureUrl is required");
        this.pricePerDay = Objects.requireNonNull(pricePerDay, "pricePerDay is required");
        if (seats <= 0) {
            throw new IllegalArgumentException("seats must be positive");
        }
        if (pricePerDay.signum() <= 0) {
            throw new IllegalArgumentException("pricePerDay must be positive");
        }
        this.seats = seats;
    }

    public Set<Car> cars() {
        return Collections.unmodifiableSet(cars);
    }

    public Set<Reservation> reservations() {
        return Collections.unmodifiableSet(reservations);
    }

    public void addCar(Car car) {
        if (!car.carTypeId().equals(id)) {
            throw new IllegalArgumentException("Car does not belong to this car type");
        }
        cars.add(car);
    }

    public void addReservation(Reservation reservation) {
        if (!reservation.carTypeId().equals(id)) {
            throw new IllegalArgumentException("Reservation does not belong to this car type");
        }
        reservations.add(reservation);
    }

    public Optional<Reservation> findReservation(UUID reservationId) {
        return reservations.stream()
                .filter(reservation -> reservation.id().equals(reservationId))
                .findFirst();
    }

    public long countCarsAvailableFrom(LocalDate pickupDate) {
        return cars.stream()
                .filter(car -> !car.availableFrom().isAfter(pickupDate))
                .count();
    }

    public long countActiveReservations(DateRange range, Instant now) {
        return reservations.stream()
                .filter(reservation -> reservation.overlaps(range))
                .filter(reservation -> reservation.isActive(now))
                .count();
    }
}
