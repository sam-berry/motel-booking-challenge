package org.samberry.bravochallenge

import org.springframework.stereotype.Repository

@Repository
class RoomDAO(
    private val roomDatabase: MutableMap<String, Room> = mutableMapOf()
) {
    fun getAllRooms(): List<Room> {
        return roomDatabase.values
            .sortedBy { it.roomNumber }
            .toList()
    }

    fun saveRoom(room: Room): Room {
        roomDatabase[room.roomNumber] = room
        return room
    }
}
