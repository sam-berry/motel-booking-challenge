package org.samberry.bravochallenge.pricing

import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.samberry.bravochallenge.api.ReservationRequest

class PricingRuleChain(private vararg val pricingRules: PricingRule) {
    fun run(reservationRequest: ReservationRequest): Money {
        return pricingRules
            .fold(Money.zero(CurrencyUnit.USD))
            { total, pricingRule -> total.plus(pricingRule.run(reservationRequest)) }
    }
}
