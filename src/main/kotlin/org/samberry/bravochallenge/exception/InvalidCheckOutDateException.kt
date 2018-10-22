package org.samberry.bravochallenge.exception

import org.samberry.bravochallenge.api.ReservationRequest
import java.lang.RuntimeException

class InvalidCheckOutDateException(
    request: ReservationRequest
) : RuntimeException("Check out date (${request.checkOutDate} must be after check in date (${request.checkInDate})")
