package com.rentacar.model;

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
class Car {

    @EqualsAndHashCode.Include
    @ToString.Include
    private final UUID id;
    private final UUID carTypeId;
    private final String numberPlate;
    private final LocalDate availableFrom;

    Car(UUID id, UUID carTypeId, String numberPlate, LocalDate availableFrom) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.carTypeId = Objects.requireNonNull(carTypeId, "carTypeId is required");
        this.numberPlate = Objects.requireNonNull(numberPlate, "numberPlate is required");
        this.availableFrom = Objects.requireNonNull(availableFrom, "availableFrom is required");
    }
}
