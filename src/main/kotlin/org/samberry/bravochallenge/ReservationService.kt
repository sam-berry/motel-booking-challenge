package org.samberry.bravochallenge

import java.util.SortedSet

class ReservationService(
    private val reservationDatabase: MutableMap<String, SortedSet<Reservation>> = mutableMapOf(),
    private val roomService: RoomService
) {
    fun reserveRoom(reservation: Reservation): Reservation {
        val roomNumber = reservation.roomNumber
        validateRoomNumber(roomNumber)

        val reservationsForRoom = reservationDatabase[roomNumber]

        if (reservationsForRoom == null) {
            reservationDatabase[roomNumber] = sortedSetOf(reservation)
        } else {
            reservationsForRoom.add(reservation)
            reservationDatabase[roomNumber] = reservationsForRoom
        }

        return reservation
    }

    private fun validateRoomNumber(roomNumber: String) {
        if (roomService.findRoom(roomNumber) == null) {
            throw RuntimeException("Room number $roomNumber was not found")
        }
    }
}
