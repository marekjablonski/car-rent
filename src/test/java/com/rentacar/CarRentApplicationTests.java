package com.rentacar;

import com.rentacar.model.car.CarCategory;
import com.rentacar.model.car.CarType;
import com.rentacar.service.car.management.CarAvailabilityService;
import com.rentacar.service.car.management.CarManagementService;
import com.rentacar.service.car.reservation.CreateReservationCommand;
import com.rentacar.service.car.reservation.ReservationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CarRentApplicationTests {

    private static final ZoneId UTC = ZoneId.of("UTC");

    private final ReservationService reservationService;
    private final CarManagementService carManagementService;
    private final CarAvailabilityService carAvailabilityService;
    private final MutableClock clock;

    private UUID carTypeId;

    CarRentApplicationTests(ReservationService reservationService,
                            CarManagementService carManagementService,
                            CarAvailabilityService carAvailabilityService,
                            MutableClock clock) {
        this.reservationService = reservationService;
        this.carManagementService = carManagementService;
        this.carAvailabilityService = carAvailabilityService;
        this.clock = clock;
    }

    @BeforeEach
    void setUpFleet() {
        carTypeId = UUID.randomUUID();
        carManagementService.registerCarType(new CarType(
                carTypeId,
                CarCategory.SEDAN,
                "https://example.com/sedan.png",
                BigDecimal.valueOf(100),
                5
        ));
        carManagementService.registerCar(carTypeId, "ABC123", currentDate());
    }

    @Test
    void createsReservationWhenInventoryIsAvailable() {
        LocalDate pickup = currentDate().plusDays(1);
        LocalDate dropOff = pickup.plusDays(2);
        UUID reservationId = UUID.randomUUID();

        reservationService.createReservation(new CreateReservationCommand(
                reservationId,
                UUID.randomUUID(),
                carTypeId,
                pickup,
                dropOff
        ));

        var available = carAvailabilityService.findAvailableTypes(List.of(CarCategory.SEDAN), pickup, dropOff);
        assertThat(available).isEmpty();
    }

    @Test
    void preventsOverbookingWhenFleetIsFull() {
        LocalDate pickup = currentDate().plusDays(1);
        LocalDate dropOff = pickup.plusDays(1);

        reservationService.createReservation(new CreateReservationCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                carTypeId,
                pickup,
                dropOff
        ));

        assertThatThrownBy(() -> reservationService.createReservation(new CreateReservationCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                carTypeId,
                pickup,
                dropOff
        ))).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("fully booked");
    }

    @Test
    void paymentWindowExpiresAfterTenMinutes() {
        LocalDate pickup = currentDate().plusDays(1);
        LocalDate dropOff = pickup.plusDays(1);
        UUID reservationId = UUID.randomUUID();
        reservationService.createReservation(new CreateReservationCommand(
                reservationId,
                UUID.randomUUID(),
                carTypeId,
                pickup,
                dropOff
        ));

        clock.advance(Duration.ofMinutes(11));

        assertThatThrownBy(() -> reservationService.confirmPayment(reservationId, "payment-1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Payment window expired");
    }

    private LocalDate currentDate() {
        return LocalDate.ofInstant(clock.instant(), UTC);
    }

    @TestConfiguration
    static class TestClockConfiguration {
        @Bean
        MutableClock mutableClock() {
            return new MutableClock(Instant.parse("2026-01-01T00:00:00Z"), UTC);
        }

        @Bean
        Clock testClock(MutableClock mutableClock) {
            return mutableClock;
        }
    }

    static class MutableClock extends Clock {
        private Instant instant;
        private final ZoneId zoneId;

        MutableClock(Instant instant, ZoneId zoneId) {
            this.instant = instant;
            this.zoneId = zoneId;
        }

        @Override
        public ZoneId getZone() {
            return zoneId;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return new MutableClock(instant, zone);
        }

        @Override
        public Instant instant() {
            return instant;
        }

        void advance(Duration duration) {
            this.instant = this.instant.plus(duration);
        }
    }
}
