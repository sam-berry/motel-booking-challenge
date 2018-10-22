package org.samberry.bravochallenge.dao

import org.samberry.bravochallenge.reservationrule.ReservationRule
import org.springframework.stereotype.Repository

@Repository
class ReservationRuleDAO(
    private val reservationRuleDatabase: MutableSet<ReservationRule> = mutableSetOf()
) {
    fun getReservationRules(): Set<ReservationRule> {
        return reservationRuleDatabase
    }

    fun saveRule(reservationRule: ReservationRule): ReservationRule {
        reservationRuleDatabase.add(reservationRule)
        return reservationRule
    }
}
