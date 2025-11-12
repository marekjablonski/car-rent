package com.rentacar.web.car.management;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rentacar.model.CarCategory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record CarTypeAvailabilityResponse(
        UUID id,
        CarCategory category,
        String pictureUrl,
        BigDecimal pricePerDay,
        int seats,
        long availableUnits,
        @JsonProperty("_links") Map<String, String> links
) {
}
