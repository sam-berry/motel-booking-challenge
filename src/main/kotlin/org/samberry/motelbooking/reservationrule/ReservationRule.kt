package org.samberry.motelbooking.reservationrule

import org.samberry.motelbooking.api.ReservationRequest

interface ReservationRule {
    fun run(reservationRequest: ReservationRequest)
}