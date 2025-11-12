package com.rentacar.service.car.reservation;

import com.rentacar.model.CarCategory;
import com.rentacar.model.CarType;
import com.rentacar.model.DateRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarAvailability {

    private final CarReservationRepository carReservationRepository;
    private final Clock clock;

    public List<CarTypeAvailability> findAvailableTypes(Collection<CarCategory> categories,
                                                        LocalDate pickupDate,
                                                        LocalDate dropOffDate) {
        DateRange requestedRange = new DateRange(pickupDate, dropOffDate);
        Instant now = clock.instant();
        return carReservationRepository.filterCarTypes(categories)
                .stream()
                .map(carType -> toAvailability(carType, requestedRange, pickupDate, now))
                .filter(availability -> availability.availableUnits() > 0)
                .toList();
    }

    private CarTypeAvailability toAvailability(CarType carType, DateRange range, LocalDate pickupDate, Instant now) {
        long total = carReservationRepository.countCarsAvailableFrom(carType.id(), pickupDate);
        long reserved = carReservationRepository.countActiveReservations(carType.id(), range, now);
        long available = Math.max(total - reserved, 0);
        return new CarTypeAvailability(
                carType.id(),
                carType.category(),
                carType.pictureUrl(),
                carType.pricePerDay(),
                carType.seats(),
                available
        );
    }

    public record CarTypeAvailability(
            UUID id,
            CarCategory category,
            String pictureUrl,
            java.math.BigDecimal pricePerDay,
            int seats,
            long availableUnits
    ) {
    }
}
