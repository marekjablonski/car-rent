package com.rentacar.service.car.management;

import com.rentacar.model.Car;
import com.rentacar.model.CarCategory;
import com.rentacar.model.CarType;
import com.rentacar.service.car.management.dto.CarDto;
import com.rentacar.service.car.management.dto.CarTypeDto;
import com.rentacar.service.car.management.dto.RegisterCarCommand;
import com.rentacar.service.car.management.dto.RegisterCarTypeCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class CarManagementImpl implements CarManagement {

    private final CarManagementRepository carManagementRepository;

    @Override
    public CarTypeDto registerCarType(RegisterCarTypeCommand command) {
        CarType carType = new CarType(
                command.id(),
                command.category(),
                command.pictureUrl(),
                command.pricePerDay(),
                command.seats()
        );
        CarType saved = carManagementRepository.saveCarType(carType);
        return toCarTypeDto(saved);
    }

    @Override
    public CarDto registerCar(RegisterCarCommand command) {

        CarType type = carManagementRepository.findCarType(command.carTypeId()).get();
        Car car = new Car(
                UUID.randomUUID(),
                type,
                command.numberPlate().toUpperCase(Locale.ROOT),
                command.availableFrom()
        );
        type.addCar(car);

        return new CarDto(
                car.id(),
                car.carType().id(),
                car.numberPlate(),
                car.availableFrom()
        );
    }

    @Override
    public CarTypeDto getCarType(UUID carTypeId) {
        return carManagementRepository.findCarType(carTypeId)
                .map(CarManagementImpl::toCarTypeDto)
                .orElseThrow(() -> new IllegalArgumentException("Car type not found: " + carTypeId));
    }

    @Override
    public List<CarTypeDto> getAvailableCarTypesByCategoryAndDates(CarCategory carCategory, LocalDate pickupDate, LocalDate dropOffDate) {
        return carManagementRepository.findCarTypesByCategoryAndDates(carCategory, pickupDate, dropOffDate)
                .stream()
                .map(CarManagementImpl::toCarTypeDto)
                .toList();
    }

    private static CarTypeDto toCarTypeDto(CarType carType) {
        return new CarTypeDto(
                carType.id(),
                carType.category(),
                carType.pictureUrl(),
                carType.pricePerDay(),
                carType.seats()
        );
    }
}
