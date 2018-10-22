package org.samberry.bravochallenge.service

import org.samberry.bravochallenge.api.ReservationRequest
import org.samberry.bravochallenge.dao.ReservationDAO
import org.springframework.stereotype.Service

@Service
class ReservationService(
    private val reservationDAO: ReservationDAO,
    private val availabilitySearchService: AvailabilitySearchService
) {
    fun makeReservation(reservationRequest: ReservationRequest) {
        val room = availabilitySearchService.findAnAvailableRoom(reservationRequest)
        val reservation = reservationRequest.toReservation()
        reservationDAO.saveReservation(room, reservation)
    }
}
