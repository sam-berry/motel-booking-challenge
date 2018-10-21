package org.samberry.bravochallenge

class RoomService(
    private val roomDatabase: MutableMap<String, Room> = mutableMapOf()
) {
    fun addRoom(room: Room): Room {
        roomDatabase[room.roomNumber] = room
        return room
    }

    fun findRoom(roomNumber: String): Room? {
        return roomDatabase[roomNumber]
    }
}
