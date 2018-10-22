package org.samberry.bravochallenge.dao

import org.samberry.bravochallenge.api.Reservation
import org.samberry.bravochallenge.api.Room
import org.springframework.stereotype.Repository
import java.util.SortedSet

@Repository
class ReservationDAO(
    private val reservationDatabase: MutableMap<String, SortedSet<Reservation>> = mutableMapOf()
) {
    fun findReservationsForRoom(room: Room): SortedSet<Reservation> {
        return reservationDatabase[room.roomNumber] ?: sortedSetOf()
    }

    fun saveReservation(room: Room, reservation: Reservation) {
        val reservations = findReservationsForRoom(room)
        reservations.add(reservation)
        reservationDatabase[room.roomNumber] = reservations
    }
}