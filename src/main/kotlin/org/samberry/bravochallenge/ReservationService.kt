package org.samberry.bravochallenge

import java.time.LocalDate
import java.util.SortedSet

class ReservationService(
    private val reservationDatabase: MutableMap<String, SortedSet<Reservation>>,
    private val roomService: RoomService
) {
    fun reserveRoom(reservationRequest: ReservationRequest) {
        val startDate = reservationRequest.checkInDate
        val endDate = reservationRequest.checkOutDate.minusDays(1)

        val rooms = roomService.findRooms(reservationRequest)
        val availableRoom = findAvailableRoom(startDate, endDate, rooms)

        val reservation = Reservation(startDate, endDate)
        val reservationsForRoom = reservationDatabase[availableRoom.roomNumber]
        if (reservationsForRoom == null || reservationsForRoom.isEmpty())
            reservationDatabase[availableRoom.roomNumber] = sortedSetOf(reservation)
        else
            reservationsForRoom.add(reservation)
    }

    private fun findAvailableRoom(
        startDate: LocalDate,
        endDate: LocalDate,
        rooms: Set<Room>
    ): Room {
        val roomIterator = rooms.iterator()
        var availableRoom: Room? = null
        while (availableRoom == null && roomIterator.hasNext()) {
            val room = roomIterator.next()
            if (isRoomAvailable(room, startDate, endDate)) {
                availableRoom = room
            }
        }
        return availableRoom ?: throw NoAvailableRoomsException()
    }

    private fun isRoomAvailable(
        room: Room,
        startDate: LocalDate,
        endDate: LocalDate
    ): Boolean {
        val reservationsForRoom = reservationDatabase[room.roomNumber]

        if (reservationsForRoom == null || reservationsForRoom.isEmpty())
            return true

        val reservationIterator = reservationsForRoom.iterator()
        var conflictingReservation: Reservation? = null
        while (conflictingReservation == null && reservationIterator.hasNext()) {
            val reservation = reservationIterator.next()
            val reservationEndsOnOrAfterRequestStart = !reservation.endDate.isBefore(startDate)
            val reservationStartsOnOrBeforeRequestEnd = !reservation.startDate.isAfter(endDate)
            if (reservationEndsOnOrAfterRequestStart && reservationStartsOnOrBeforeRequestEnd) {
                conflictingReservation = reservation
            }
        }

        return conflictingReservation == null
    }
}
