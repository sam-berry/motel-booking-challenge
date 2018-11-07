package org.samberry.motelbooking.exception

import org.samberry.motelbooking.api.ReservationRequest

class InvalidCheckOutDateException(
    request: ReservationRequest
) : RuntimeException("Check out date (${request.checkOutDate}) must be after check in date (${request.checkInDate})")
