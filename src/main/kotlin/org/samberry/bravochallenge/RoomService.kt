package org.samberry.bravochallenge

import org.springframework.stereotype.Service

@Service
class RoomService(
    private val roomDAO: RoomDAO
) {
    fun getAllRooms(): List<Room> {
        return roomDAO.getAllRooms()
    }

    fun addRoom(room: Room): Room {
        return roomDAO.saveRoom(room)
    }

    fun findRooms(reservationRequest: ReservationRequest): Set<Room> {
        val rooms = queryRooms(reservationRequest)
        if (rooms.isNotEmpty()) return rooms
        return queryRooms(reservationRequest, true)
    }

    private fun queryRooms(
        reservationRequest: ReservationRequest,
        allowRoomsWithAmenities: Boolean = false
    ): Set<Room> {
        return getAllRooms()
            .asSequence()
            .filter { it.numberOfBeds == reservationRequest.numberOfBeds }
            .filter {
                when {
                    reservationRequest.numberOfPets > 0 -> it.petFriendly
                    allowRoomsWithAmenities -> true
                    else -> !it.petFriendly
                }
            }
            .filter {
                when {
                    reservationRequest.handicapAccessible -> it.handicapAccessible
                    allowRoomsWithAmenities -> true
                    else -> !it.handicapAccessible
                }
            }
            .toSet()
    }
}
