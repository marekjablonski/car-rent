package com.rentacar.service.car.management.dto;

import java.time.LocalDate;
import java.util.UUID;

public record CarDto(
        UUID id,
        UUID carTypeId,
        String numberPlate,
        LocalDate availableFrom
) {
}
