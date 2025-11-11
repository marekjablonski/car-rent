package com.rentacar.web.car.management;

import java.time.LocalDate;
import java.util.UUID;

public record CarResponse(
        UUID id,
        UUID carTypeId,
        String numberPlate,
        LocalDate availableFrom
) {
}
