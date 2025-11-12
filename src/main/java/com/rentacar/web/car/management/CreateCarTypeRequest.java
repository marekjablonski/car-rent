package com.rentacar.web.car.management;

import com.rentacar.model.CarCategory;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateCarTypeRequest(
        UUID id,
        CarCategory category,
        String pictureUrl,
        BigDecimal pricePerDay,
        int seats
) {
}
