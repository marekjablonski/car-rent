package com.rentacar.web.car.reservation;

import java.util.UUID;

public record PaymentRequest(
        UUID reservationId,
        UUID paymentId
) {
}
