package org.samberry.bravochallenge.reservationrule

import org.samberry.bravochallenge.api.ReservationRequest
import org.samberry.bravochallenge.dao.ReservationRuleDAO
import org.springframework.stereotype.Service

@Service
class ReservationRuleChain(private val reservationRuleDAO: ReservationRuleDAO) {
    fun run(reservationRequest: ReservationRequest) {
        reservationRuleDAO.getReservationRules()
            .forEach { it.run(reservationRequest) }
    }
}
