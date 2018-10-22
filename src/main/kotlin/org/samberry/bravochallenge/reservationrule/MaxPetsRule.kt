package org.samberry.bravochallenge.reservationrule

import org.samberry.bravochallenge.api.ReservationRequest
import org.samberry.bravochallenge.exception.TooManyPetsException

data class MaxPetsRule(
    val maxAllowedPets: Int
) : ReservationRule {
    override fun run(reservationRequest: ReservationRequest) {
        if (reservationRequest.numberOfPets > maxAllowedPets)
            throw TooManyPetsException(reservationRequest, maxAllowedPets)
    }
}
