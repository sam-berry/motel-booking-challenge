package org.samberry.bravochallenge.pricing

import org.joda.money.Money
import org.samberry.bravochallenge.api.ReservationRequest

interface PricingRule {
    fun run(reservationRequest: ReservationRequest): Money
}