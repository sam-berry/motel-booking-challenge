package org.samberry.bravochallenge.service

import org.samberry.bravochallenge.dao.RoomDAO
import org.samberry.bravochallenge.api.ReservationRequest
import org.samberry.bravochallenge.api.Room
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
        return getAllRooms()
            .asSequence()
            .filter { it.numberOfBeds == reservationRequest.numberOfBeds }
            .filter { if (reservationRequest.numberOfPets > 0) it.petFriendly else true }
            .filter { if (reservationRequest.handicapAccessible) it.handicapAccessible else true }
            .toSet()
    }
}
