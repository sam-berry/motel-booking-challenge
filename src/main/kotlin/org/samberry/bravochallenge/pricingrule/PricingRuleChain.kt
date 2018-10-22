package org.samberry.bravochallenge.pricingrule

import org.joda.money.Money
import org.samberry.bravochallenge.api.ReservationRequest
import org.samberry.bravochallenge.dao.PricingRuleDAO
import org.springframework.stereotype.Service

@Service
class PricingRuleChain(private val pricingRuleDAO: PricingRuleDAO) {
    fun run(reservationRequest: ReservationRequest): Money {
        return pricingRuleDAO.getPricingRules()
            .fold(Money.zero(PRICING_CURRENCY))
            { total, pricingRule -> total.plus(pricingRule.run(reservationRequest)) }
    }
}
