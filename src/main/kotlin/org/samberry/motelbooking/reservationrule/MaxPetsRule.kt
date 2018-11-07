package org.samberry.motelbooking.reservationrule

import org.samberry.motelbooking.api.ReservationRequest
import org.samberry.motelbooking.exception.TooManyPetsException

data class MaxPetsRule(
    val maxAllowedPets: Int
) : ReservationRule {
    override fun run(reservationRequest: ReservationRequest) {
        if (reservationRequest.numberOfPets > maxAllowedPets)
            throw TooManyPetsException(reservationRequest, maxAllowedPets)
    }
}
