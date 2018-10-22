package org.samberry.bravochallenge.dao

import org.samberry.bravochallenge.pricing.PricingRule
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
