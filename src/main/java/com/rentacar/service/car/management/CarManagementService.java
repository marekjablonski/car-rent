package com.rentacar.service.car.management;

import com.rentacar.service.car.management.dto.CarDto;
import com.rentacar.service.car.management.dto.CarTypeDto;
import com.rentacar.service.car.management.dto.RegisterCarCommand;
import com.rentacar.service.car.management.dto.RegisterCarTypeCommand;

import java.util.UUID;

public interface CarManagementService {

    CarTypeDto registerCarType(RegisterCarTypeCommand command);

    CarDto registerCar(RegisterCarCommand command);

    CarTypeDto getCarType(UUID carTypeId);
}
