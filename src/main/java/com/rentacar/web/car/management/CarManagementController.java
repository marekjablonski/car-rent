package com.rentacar.web.car.management;

import com.rentacar.model.CarCategory;
import com.rentacar.service.car.management.CarAvailabilityService;
import com.rentacar.service.car.management.CarManagementService;
import com.rentacar.service.car.management.dto.CarDto;
import com.rentacar.service.car.management.dto.CarTypeDto;
import com.rentacar.service.car.management.dto.RegisterCarCommand;
import com.rentacar.service.car.management.dto.RegisterCarTypeCommand;
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

    private final CarManagementService carManagementService;
    private final CarAvailabilityService carAvailabilityService;

    @PostMapping("/carType")
    public ResponseEntity<CarTypeResponse> createCarType(@RequestBody CreateCarTypeRequest request) {
        validateCarTypeRequest(request);
        CarTypeResponse body = toCarTypeResponse(
                carManagementService.registerCarType(new RegisterCarTypeCommand(
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
        CarTypeDto dto = carManagementService.getCarType(carTypeId);
        return toCarTypeResponse(dto);
    }

    @PostMapping("/car")
    public ResponseEntity<CarResponse> registerCar(@RequestBody CreateCarRequest request) {
        if (request.carTypeId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "carTypeId is required");
        }
        if (request.numberPlate() == null || request.numberPlate().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "numberPlate is required");
        }
        if (request.availableFrom() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "availableFrom is required");
        }
        CarResponse body = toCarResponse(carManagementService.registerCar(new RegisterCarCommand(
                request.carTypeId(),
                request.numberPlate(),
                request.availableFrom()
        )));
        return ResponseEntity.created(URI.create("/car/" + body.id())).body(body);
    }

    @GetMapping("/carType")
    public List<CarTypeAvailabilityResponse> getAvailableCarTypes(
            @RequestParam(name = "type", required = false) String typeFilter,
            @RequestParam(name = "pickup-date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
            @RequestParam(name = "drop-off-date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dropOffDate
    ) {
        if (dropOffDate.isBefore(pickupDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "drop-off-date must be after pickup-date");
        }
        List<CarCategory> categories = parseCategories(typeFilter);
        return carAvailabilityService.findAvailableTypes(categories, pickupDate, dropOffDate)
                .stream()
                .map(availability -> new CarTypeAvailabilityResponse(
                        availability.id(),
                        availability.category(),
                        availability.pictureUrl(),
                        availability.pricePerDay(),
                        availability.seats(),
                        availability.availableUnits(),
                        carTypeLinks(availability.id())
                ))
                .toList();
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

    private static void validateCarTypeRequest(CreateCarTypeRequest request) {
        if (request.id() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id is required");
        }
        if (request.category() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type is required");
        }
        if (request.pictureUrl() == null || request.pictureUrl().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pictureUrl is required");
        }
        if (request.pricePerDay() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pricePerDay is required");
        }
        if (request.seats() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "seats must be positive");
        }
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
