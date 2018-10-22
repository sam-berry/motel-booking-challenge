package org.samberry.bravochallenge

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
