package org.samberry.motelbooking.dao

import org.samberry.motelbooking.pricingrule.PricingRule
import org.springframework.stereotype.Repository

@Repository
class PricingRuleDAO(
    private val pricingRuleDatabase: MutableSet<PricingRule> = mutableSetOf()
) {
    fun getPricingRules(): Set<PricingRule> {
        return pricingRuleDatabase
    }

    fun saveRule(pricingRule: PricingRule): PricingRule {
        pricingRuleDatabase.add(pricingRule)
        return pricingRule
    }
}
