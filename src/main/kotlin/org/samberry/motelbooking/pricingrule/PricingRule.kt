package org.samberry.motelbooking.pricingrule

import org.joda.money.Money
import org.samberry.motelbooking.api.ReservationRequest

interface PricingRule {
    fun run(reservationRequest: ReservationRequest): Money
}