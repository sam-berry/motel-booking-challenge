package org.samberry.motelbooking.service

import org.samberry.motelbooking.api.ReservationRequest
import org.samberry.motelbooking.api.Room
import org.samberry.motelbooking.dao.ReservationDAO
import org.samberry.motelbooking.exception.NoAvailableRoomsException
import org.samberry.motelbooking.reservationrule.ReservationRuleChain
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

        val availableRooms = roomService.findRooms(reservationRequest)
            .filter { isRoomAvailable(it, reservationRequest) }
            .toSet()

        val roomsExcludingNotNeededAmenities = availableRooms
            .asSequence()
            .filterNot { reservationRequest.numberOfPets == 0 && it.petFriendly }
            .filterNot { !reservationRequest.handicapAccessible && it.handicapAccessible }
            .toSet()

        return if (roomsExcludingNotNeededAmenities.isEmpty())
            availableRooms
        else
            roomsExcludingNotNeededAmenities
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

        return !reservationDAO.findReservationsForRoom(room)
            .any { !startDate.isAfter(it.endDate) && !endDate.isBefore(it.startDate) }
    }
}
