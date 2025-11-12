package com.rentacar.web.car.management;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public record CarResponse(
        UUID id,
        UUID carTypeId,
        String numberPlate,
        LocalDate availableFrom,
        @JsonProperty("_links") Map<String, String> links
) {
}
