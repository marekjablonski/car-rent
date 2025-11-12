package com.rentacar.web.car.management;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rentacar.model.CarCategory;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateCarTypeRequest(
        UUID id,
        @JsonProperty("type") CarCategory category,
        String pictureUrl,
        BigDecimal pricePerDay,
        int seats
) {
}
