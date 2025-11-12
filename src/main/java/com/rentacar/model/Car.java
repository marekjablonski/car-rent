package com.rentacar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
class Car {
    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private final UUID id;
    @ManyToOne
    @JoinColumn(name = "car_type_id")
    private final CarType carType;
    private final String numberPlate;
    private final LocalDate availableFrom;

    Car(UUID id, CarType carType, String numberPlate, LocalDate availableFrom) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.carType = Objects.requireNonNull(carType, "carTypeId is required");
        this.numberPlate = Objects.requireNonNull(numberPlate, "numberPlate is required");
        this.availableFrom = Objects.requireNonNull(availableFrom, "availableFrom is required");
    }
}
