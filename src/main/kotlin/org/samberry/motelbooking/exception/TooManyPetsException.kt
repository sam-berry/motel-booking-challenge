package org.samberry.motelbooking.exception

import org.samberry.motelbooking.api.ReservationRequest

class TooManyPetsException(
    reservationRequest: ReservationRequest,
    maxAllowedPets: Int
) : RuntimeException("${reservationRequest.numberOfPets} is too many pets. Only $maxAllowedPets are allowed.")

