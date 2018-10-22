package org.samberry.bravochallenge.service

import org.samberry.bravochallenge.dao.ReservationDAO
import org.samberry.bravochallenge.api.ReservationRequest
import org.springframework.stereotype.Service

@Service
class ReservationService(
    private val reservationDAO: ReservationDAO,
    private val availabilitySearchService: AvailabilitySearchService
) {
    fun reserveRoom(reservationRequest: ReservationRequest) {
        val room = availabilitySearchService.findAnAvailableRoom(reservationRequest)
        val reservation = reservationRequest.toReservation()
        reservationDAO.saveReservation(room, reservation)
    }
}
