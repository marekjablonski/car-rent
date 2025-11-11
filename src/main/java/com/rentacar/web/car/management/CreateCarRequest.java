package com.rentacar.web.car.management;

import java.time.LocalDate;
import java.util.UUID;

public record CreateCarRequest(
        UUID carTypeId,
        String numberPlate,
        LocalDate availableFrom
) {
}
