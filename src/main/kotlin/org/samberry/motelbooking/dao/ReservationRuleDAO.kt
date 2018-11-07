package org.samberry.motelbooking.dao

import org.samberry.motelbooking.reservationrule.ReservationRule
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
