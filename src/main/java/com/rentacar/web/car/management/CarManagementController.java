package com.rentacar.web.car.management;

import com.rentacar.model.CarCategory;
import com.rentacar.service.car.management.CarManagement;
import com.rentacar.service.car.management.dto.CarDto;
import com.rentacar.service.car.management.dto.CarTypeDto;
import com.rentacar.service.car.management.dto.RegisterCarCommand;
import com.rentacar.service.car.management.dto.RegisterCarTypeCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CarManagementController {

    private final CarManagement carManagement;
    private final CarAvailabilityService carAvailabilityService;

    @PostMapping("/carType")
    public ResponseEntity<CarTypeResponse> createCarType(@RequestBody @Valid CreateCarTypeRequest request) {
        CarTypeResponse body = toCarTypeResponse(
                carManagement.registerCarType(new RegisterCarTypeCommand(
                        request.id(),
                        request.category(),
                        request.pictureUrl(),
                        request.pricePerDay(),
                        request.seats()
                ))
        );
        return ResponseEntity.created(URI.create("/carType/" + body.id())).body(body);
    }

    @GetMapping("/carType/{carTypeId}")
    public CarTypeResponse getCarType(@PathVariable UUID carTypeId) {
        CarTypeDto dto = carManagement.getCarType(carTypeId);
        return toCarTypeResponse(dto);
    }

    @GetMapping("/carType")
    public List<CarTypeResponse> getAvailableCarTypes(
            @RequestParam(name = "type", required = false) CarCategory carCategory,
            @RequestParam(name = "pickup-date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
            @RequestParam(name = "drop-off-date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dropOffDate
    ) {
        if (dropOffDate.isBefore(pickupDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "drop-off-date must be after pickup-date");
        }
        return carManagement.getAvailableCarTypesByCategoryAndDates(carCategory, pickupDate, dropOffDate)
                .stream()
                .map(CarManagementController::toCarTypeResponse)
                .toList();
    }

    @PostMapping("/carType/{carTypeId}/car")
    public ResponseEntity<CarResponse> registerCar(@PathVariable(required = false) UUID carTypeId,
                                                   @RequestBody @Valid CreateCarRequest request) {

        CarResponse body = toCarResponse(carManagement.registerCar(new RegisterCarCommand(
                carTypeId,
                request.numberPlate(),
                request.availableFrom()
        )));
        return ResponseEntity.created(URI.create("/car/" + body.id())).body(body);
    }


    private static CarTypeResponse toCarTypeResponse(CarTypeDto dto) {
        return new CarTypeResponse(
                dto.id(),
                dto.category(),
                dto.pictureUrl(),
                dto.pricePerDay(),
                dto.seats(),
                carTypeLinks(dto.id())
        );
    }

    private static CarResponse toCarResponse(CarDto dto) {
        return new CarResponse(
                dto.id(),
                dto.carTypeId(),
                dto.numberPlate(),
                dto.availableFrom(),
                Map.of("carType", "/carType/" + dto.carTypeId())
        );
    }

    private static List<CarCategory> parseCategories(String filter) {
        if (filter == null || filter.isBlank()) {
            return List.of();
        }
        String trimmed = filter.replace("[", "").replace("]", "");
        if (trimmed.isBlank()) {
            return List.of();
        }
        EnumSet<CarCategory> results = EnumSet.noneOf(CarCategory.class);
        for (String token : trimmed.split(",")) {
            if (token.isBlank()) {
                continue;
            }
            try {
                results.add(CarCategory.valueOf(token.trim().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown car category: " + token);
            }
        }
        return List.copyOf(results);
    }

    private static Map<String, String> carTypeLinks(UUID carTypeId) {
        return Map.of(
                "self", "/carType/" + carTypeId,
                "reserve", "/reservation"
        );
    }
}
