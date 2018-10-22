package org.samberry.bravochallenge.reservationrule

import org.samberry.bravochallenge.api.ReservationRequest

interface ReservationRule {
    fun run(reservationRequest: ReservationRequest)
}