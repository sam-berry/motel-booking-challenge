package org.samberry.bravochallenge

import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellComponent

@ShellComponent
class ReservationCLI(
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
}