package com.rentacar.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Accessors(fluent = true)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "cars")
public class Car {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_type_id", nullable = false)
    private CarType carType;

    @Column(name = "number_plate", nullable = false, unique = true, length = 32)
    private String numberPlate;

    @Column(name = "available_from", nullable = false)
    private LocalDate availableFrom;

    protected Car() {
        // for JPA
    }

    public Car(UUID id, CarType carType, String numberPlate, LocalDate availableFrom) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.carType = Objects.requireNonNull(carType, "carType is required");
        this.numberPlate = Objects.requireNonNull(numberPlate, "numberPlate is required");
        this.availableFrom = Objects.requireNonNull(availableFrom, "availableFrom is required");
    }

    void assignCarType(CarType carType) {
        this.carType = carType;
    }
}
