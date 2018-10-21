package org.samberry.bravochallenge

class RoomService(
    private val roomDatabase: MutableMap<String, Room>
) {
    fun addRoom(room: Room): Room {
        roomDatabase[room.roomNumber] = room
        return room
    }

    fun findRooms(reservationRequest: ReservationRequest): Set<Room> {
        return roomDatabase.values
            .filter { it.numberOfBeds == reservationRequest.numberOfBeds }
            .toSet()
    }
}
