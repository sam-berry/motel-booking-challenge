package org.samberry.bravochallenge.pricingrule

import org.joda.money.Money
import org.samberry.bravochallenge.api.ReservationRequest
import java.time.temporal.ChronoUnit

data class BaseRate(
    val rate: Money,
    private val numberOfBeds: Int
) : PricingRule {
    override fun run(reservationRequest: ReservationRequest): Money {
        return if (numberOfBeds != reservationRequest.numberOfBeds)
            Money.zero(PRICING_CURRENCY)
        else {
            val reservationLength = ChronoUnit.DAYS.between(reservationRequest.checkInDate, reservationRequest.checkOutDate)
            rate.multipliedBy(reservationLength.toDouble(), PRICING_ROUNDING_MODE)
        }
    }
}
