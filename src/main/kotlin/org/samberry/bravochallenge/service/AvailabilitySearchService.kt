package org.samberry.bravochallenge.service

import org.samberry.bravochallenge.api.Reservation
import org.samberry.bravochallenge.api.ReservationRequest
import org.samberry.bravochallenge.api.Room
import org.samberry.bravochallenge.dao.ReservationDAO
import org.samberry.bravochallenge.exception.NoAvailableRoomsException
import org.samberry.bravochallenge.reservationrule.ReservationRuleChain
import org.springframework.stereotype.Service

@Service
class AvailabilitySearchService(
    private val reservationDAO: ReservationDAO,
    private val roomService: RoomService,
    private val reservationRuleChain: ReservationRuleChain
) {
    fun findAvailableRooms(
        reservationRequest: ReservationRequest
    ): Set<Room> {
        reservationRuleChain.run(reservationRequest)

        val allRooms = roomService.findRooms(reservationRequest)
        val roomIterator = allRooms.iterator()

        val availableRooms = mutableSetOf<Room>()
        while (roomIterator.hasNext()) {
            val room = roomIterator.next()
            if (isRoomAvailable(room, reservationRequest))
                availableRooms.add(room)
        }

        // try to save amenity rooms until they are the only thing left
        // e.g., if pets aren't coming, try to book a non-pet room
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
