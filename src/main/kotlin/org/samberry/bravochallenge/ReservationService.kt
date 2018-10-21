package org.samberry.bravochallenge

import java.util.SortedSet

class ReservationService(
    private val reservationDatabase: MutableMap<String, SortedSet<Reservation>> = mutableMapOf(),
    private val roomService: RoomService
) {
    fun reserveRoom(reservationRequest: Reservation): Reservation {
        val roomNumber = reservationRequest.roomNumber
        validateRoomNumber(roomNumber)

        val reservationsForRoom = reservationDatabase[roomNumber]

        if (reservationsForRoom == null || reservationsForRoom.isEmpty()) {
            reservationDatabase[roomNumber] = sortedSetOf(reservationRequest)
        } else {
            verifyAvailability(reservationRequest, reservationsForRoom)
            reservationsForRoom.add(reservationRequest)
        }

        return reservationRequest
    }

    private fun verifyAvailability(
        reservationRequest: Reservation,
        reservationsForRoom: SortedSet<Reservation>
    ) {
        val reservationIterator = reservationsForRoom.iterator()
        while (reservationIterator.hasNext()) {
            val reservation = reservationIterator.next()
            val reservationEndsOnOrAfterRequestStart = !reservation.endDate.isBefore(reservationRequest.startDate)
            val reservationStartsOnOrBeforeRequestEnd = !reservation.startDate.isAfter(reservationRequest.endDate)
            if (reservationEndsOnOrAfterRequestStart && reservationStartsOnOrBeforeRequestEnd) {
                throw ReservationUnavailableException(reservationRequest)
            }
        }
    }

    private fun validateRoomNumber(roomNumber: String) {
        if (roomService.findRoom(roomNumber) == null) {
            throw RuntimeException("Room number $roomNumber was not found")
        }
    }
}
