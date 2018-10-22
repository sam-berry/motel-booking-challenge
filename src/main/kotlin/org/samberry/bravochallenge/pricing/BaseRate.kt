package org.samberry.bravochallenge.pricing

import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.samberry.bravochallenge.api.ReservationRequest

class BaseRate(
    val rate: Money,
    private val numberOfBeds: Int
) : PricingRule {
    override fun run(reservationRequest: ReservationRequest): Money {
        return if (numberOfBeds != reservationRequest.numberOfBeds)
            Money.zero(CurrencyUnit.USD)
        else
            rate
    }
}
