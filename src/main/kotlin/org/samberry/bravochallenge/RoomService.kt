package org.samberry.bravochallenge

class RoomService(
    private val roomDatabase: MutableMap<String, Room>
) {
    fun addRoom(room: Room): Room {
        roomDatabase[room.roomNumber] = room
        return room
    }
}
