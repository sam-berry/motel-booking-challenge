package org.samberry.bravochallenge

class AvailabilitySearchService(
    private val reservationDAO: ReservationDAO,
    private val roomService: RoomService
) {
    fun findAnAvailableRoom(
        reservationRequest: ReservationRequest
    ): Room {
        val rooms = roomService.findRooms(reservationRequest)

        val roomIterator = rooms.iterator()
        var availableRoom: Room? = null
        while (availableRoom == null && roomIterator.hasNext()) {
            val room = roomIterator.next()
            if (isRoomAvailable(room, reservationRequest))
                availableRoom = room
        }
        return availableRoom ?: throw NoAvailableRoomsException()
    }

    private fun isRoomAvailable(
        room: Room,
        reservationRequest: ReservationRequest
    ): Boolean {
        val requestedReservation = reservationRequest.toReservation()
        val startDate = requestedReservation.startDate
        val endDate = requestedReservation.endDate

        val reservationsForRoom = reservationDAO.findReservationsForRoom(room)
        if (reservationsForRoom.isEmpty()) return true

        val reservationIterator = reservationsForRoom.iterator()
        var conflictingReservation: Reservation? = null
        while (conflictingReservation == null && reservationIterator.hasNext()) {
            val reservation = reservationIterator.next()
            if (!reservation.endDate.isBefore(startDate) && !reservation.startDate.isAfter(endDate))
                conflictingReservation = reservation
        }

        return conflictingReservation == null
    }
}
