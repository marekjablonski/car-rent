package com.rentacar.service.car.management;

import com.rentacar.model.CarCategory;
import com.rentacar.service.car.management.dto.CarDto;
import com.rentacar.service.car.management.dto.CarTypeDto;
import com.rentacar.service.car.management.dto.RegisterCarCommand;
import com.rentacar.service.car.management.dto.RegisterCarTypeCommand;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CarManagement {

    CarTypeDto registerCarType(RegisterCarTypeCommand command);

    CarDto registerCar(RegisterCarCommand command);

    CarTypeDto getCarType(UUID carTypeId);

    List<CarTypeDto> getAvailableCarTypesByCategoryAndDates(CarCategory carCategory, LocalDate pickupDate, LocalDate dropOffDate);
}
