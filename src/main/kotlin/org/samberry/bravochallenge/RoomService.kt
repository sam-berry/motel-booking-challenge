package org.samberry.bravochallenge

class RoomService(
    private val roomDatabase: MutableMap<String, Room>
) {
    fun addRoom(room: Room): Room {
        roomDatabase[room.roomNumber] = room
        return room
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
        return roomDatabase.values
            .filter { it.numberOfBeds == reservationRequest.numberOfBeds }
            .filter { if (reservationRequest.numberOfPets == 0) true else it.petFriendly }
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
