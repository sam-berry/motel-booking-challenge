package org.samberry.bravochallenge.cli

import org.samberry.bravochallenge.api.Room
import org.samberry.bravochallenge.service.RoomService
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellComponent

@ShellComponent
class RoomCLI(
    private val roomService: RoomService
) {
    @ShellMethod("Create a new motel room")
    fun createRoom(
        roomNumber: String,
        numberOfBeds: Int,
        buildingLevel: Int,
        handicapAccessible: Boolean,
        petFriendly: Boolean
    ): String {
        val result = roomService.addRoom(Room(
            roomNumber = roomNumber,
            numberOfBeds = numberOfBeds,
            buildingLevel = buildingLevel,
            handicapAccessible = handicapAccessible,
            petFriendly = petFriendly
        ))

        return "Successfully created room. Details: $result"
    }

    @ShellMethod("Get a list of all motel rooms")
    fun getAllRooms(): String {
        val rooms = roomService.getAllRooms()

        return if (rooms.isEmpty())
            "No rooms currently configured"
        else
            rooms.joinToString(separator = "\n", transform = { it.toString() })
    }
}