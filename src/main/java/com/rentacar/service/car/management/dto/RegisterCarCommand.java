package com.rentacar.service.car.management.dto;

import java.time.LocalDate;
import java.util.UUID;

public record RegisterCarCommand(
        UUID carTypeId,
        String numberPlate,
        LocalDate availableFrom
) {
}
