package com.rentacar.web.car.management;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateCarRequest(
        @NotNull UUID id,
        @NotEmpty String numberPlate,
        @NotNull LocalDate availableFrom
) {
}
