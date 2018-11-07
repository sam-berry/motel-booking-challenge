package org.samberry.motelbooking.reservationrule

import org.samberry.motelbooking.api.ReservationRequest
import org.samberry.motelbooking.dao.ReservationRuleDAO
import org.springframework.stereotype.Service

@Service
class ReservationRuleChain(private val reservationRuleDAO: ReservationRuleDAO) {
    fun run(reservationRequest: ReservationRequest) {
        reservationRuleDAO.getReservationRules()
            .forEach { it.run(reservationRequest) }
    }
}
