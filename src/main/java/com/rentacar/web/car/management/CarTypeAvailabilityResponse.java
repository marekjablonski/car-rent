package com.rentacar.web.car.management;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rentacar.model.car.CarCategory;

import java.math.BigDecimal;
import java.util.UUID;

public record CarTypeAvailabilityResponse(
        UUID id,
        @JsonProperty("type") CarCategory category,
        String pictureUrl,
        BigDecimal pricePerDay,
        int seats,
        long availableUnits
) {
}
