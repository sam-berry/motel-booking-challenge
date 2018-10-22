package org.samberry.bravochallenge.pricing

import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.samberry.bravochallenge.api.ReservationRequest

class PetFee(
    val fee: Money
) : PricingRule {
    override fun run(reservationRequest: ReservationRequest): Money {
        return if (reservationRequest.numberOfPets == 0)
            Money.zero(CurrencyUnit.USD)
        else
            fee.multipliedBy(reservationRequest.numberOfPets.toDouble(), PRICING_ROUNDING_MODE)
    }
}
