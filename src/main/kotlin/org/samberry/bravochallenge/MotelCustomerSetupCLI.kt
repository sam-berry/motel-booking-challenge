package org.samberry.bravochallenge

import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellComponent

@ShellComponent
class MotelCustomerSetupCLI(
    private val roomService: RoomService
) {
    @ShellMethod("Setup rooms for the motel customer")
    fun setupCustomerMotel(): String {
        val firstFloor = setOf(
            Room(
                roomNumber = "A1",
                numberOfBeds = 1,
                buildingLevel = 1,
                handicapAccessible = true,
                petFriendly = true
            ),
            Room(
                roomNumber = "A2",
                numberOfBeds = 1,
                buildingLevel = 1,
                handicapAccessible = true,
                petFriendly = true
            ),
            Room(
                roomNumber = "A3",
                numberOfBeds = 2,
                buildingLevel = 1,
                handicapAccessible = true,
                petFriendly = true
            ),
            Room(
                roomNumber = "A4",
                numberOfBeds = 2,
                buildingLevel = 1,
                handicapAccessible = true,
                petFriendly = true
            ),
            Room(
                roomNumber = "A5",
                numberOfBeds = 3,
                buildingLevel = 1,
                handicapAccessible = true,
                petFriendly = true
            ),
            Room(
                roomNumber = "A6",
                numberOfBeds = 3,
                buildingLevel = 1,
                handicapAccessible = true,
                petFriendly = true
            )
        )

        val secondFloor = setOf(
            Room(
                roomNumber = "B1",
                numberOfBeds = 1,
                buildingLevel = 2,
                handicapAccessible = false,
                petFriendly = false
            ),
            Room(
                roomNumber = "B2",
                numberOfBeds = 1,
                buildingLevel = 2,
                handicapAccessible = false,
                petFriendly = false
            ),
            Room(
                roomNumber = "B3",
                numberOfBeds = 2,
                buildingLevel = 2,
                handicapAccessible = false,
                petFriendly = false
            ),
            Room(
                roomNumber = "B4",
                numberOfBeds = 2,
                buildingLevel = 2,
                handicapAccessible = false,
                petFriendly = false
            ),
            Room(
                roomNumber = "B5",
                numberOfBeds = 3,
                buildingLevel = 2,
                handicapAccessible = false,
                petFriendly = false
            ),
            Room(
                roomNumber = "B6",
                numberOfBeds = 3,
                buildingLevel = 2,
                handicapAccessible = false,
                petFriendly = false
            )
        )

        val results = firstFloor.union(secondFloor)
            .asSequence()
            .map { roomService.addRoom(it) }
            .joinToString(separator = "\n", transform = { it.toString() })

        return "Successfully setup customer. Results:\n $results"
    }
}