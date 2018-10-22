package org.samberry.bravochallenge.dao

import org.samberry.bravochallenge.api.Room
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
