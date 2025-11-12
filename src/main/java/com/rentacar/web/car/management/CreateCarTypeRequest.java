package com.rentacar.web.car.management;

import com.rentacar.model.CarCategory;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateCarTypeRequest(
        @NotNull UUID id,
        @NotNull CarCategory category,
        @NotEmpty String pictureUrl,
        @NotNull BigDecimal pricePerDay,
        @NotNull Integer seats
) {
}
