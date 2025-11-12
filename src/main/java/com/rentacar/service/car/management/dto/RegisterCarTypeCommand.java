package com.rentacar.service.car.management.dto;

import com.rentacar.model.CarCategory;

import java.math.BigDecimal;
import java.util.UUID;

public record RegisterCarTypeCommand(
        UUID id,
        CarCategory category,
        String pictureUrl,
        BigDecimal pricePerDay,
        int seats
) {
}
