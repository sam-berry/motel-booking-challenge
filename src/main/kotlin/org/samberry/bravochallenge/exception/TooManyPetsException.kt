package org.samberry.bravochallenge.exception

import org.samberry.bravochallenge.api.ReservationRequest

class TooManyPetsException(
    reservationRequest: ReservationRequest,
    maxAllowedPets: Int
) : RuntimeException("${reservationRequest.numberOfPets} is too many pets. Only $maxAllowedPets are allowed.")

