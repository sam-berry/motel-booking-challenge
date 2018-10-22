package org.samberry.bravochallenge

import org.springframework.stereotype.Service

@Service
class AvailabilitySearchService(
    private val reservationDAO: ReservationDAO,
    private val roomService: RoomService
) {
    fun findAvailableRooms(
        reservationRequest: ReservationRequest
    ): Set<Room> {
        val allRooms = roomService.findRooms(reservationRequest)
        val roomIterator = allRooms.iterator()

        val availableRooms = mutableSetOf<Room>()
        while (roomIterator.hasNext()) {
            val room = roomIterator.next()
            if (isRoomAvailable(room, reservationRequest))
                availableRooms.add(room)
        }

        val roomsExcludingNotNeededAmenities = availableRooms
            .asSequence()
            .filterNot { reservationRequest.numberOfPets == 0 && it.petFriendly }
            .filterNot { !reservationRequest.handicapAccessible && it.handicapAccessible }
            .toSet()

        return if (roomsExcludingNotNeededAmenities.isNotEmpty())
            roomsExcludingNotNeededAmenities
        else
            availableRooms
    }

    fun findAnAvailableRoom(
        reservationRequest: ReservationRequest
    ): Room {
        return findAvailableRooms(reservationRequest).firstOrNull() ?: throw NoAvailableRoomsException()
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
