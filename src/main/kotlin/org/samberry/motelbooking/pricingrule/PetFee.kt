package org.samberry.motelbooking.pricingrule

import org.joda.money.Money
import org.samberry.motelbooking.api.ReservationRequest

data class PetFee(
    val fee: Money
) : PricingRule {
    override fun run(reservationRequest: ReservationRequest): Money {
        return if (reservationRequest.numberOfPets == 0)
            Money.zero(PRICING_CURRENCY)
        else
            fee.multipliedBy(reservationRequest.numberOfPets.toDouble(), PRICING_ROUNDING_MODE)
    }
}
