package org.samberry.bravochallenge

import java.lang.RuntimeException
import java.time.LocalDate

class InvalidCheckOutDateException(
    request: ReservationRequest
) : RuntimeException("Check out date (${request.checkOutDate} must be after check in date (${request.checkInDate})")
