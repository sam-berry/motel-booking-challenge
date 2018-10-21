package org.samberry.bravochallenge

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class AddNewRoomTest {
    private lateinit var roomDatabase: MutableMap<String, Room>
    private lateinit var roomService: RoomService

    @Before
    fun setUp() {
        roomDatabase = mutableMapOf()
        roomService = RoomService(roomDatabase)
    }

    @Test
    fun `can add a simple room with 1 bed`() {
        val room = Room(
            roomNumber = "A123",
            numberOfBeds = 1,
            buildingLevel = 1
        )

        val result = roomService.addRoom(room)

        assertThat(result).isEqualTo(room)
        assertThat(roomDatabase[result.roomNumber]).isEqualTo(result)
    }

    @Test
    fun `can add a handicap accessible room`() {
        val room = Room(
            roomNumber = "A123",
            numberOfBeds = 1,
            buildingLevel = 1,
            handicapAccessible = true
        )

        val result = roomService.addRoom(room)

        assertThat(result).isEqualTo(room)
        assertThat(roomDatabase[result.roomNumber]).isEqualTo(result)
    }

    @Test
    fun `can add a pet friendly room`() {
        val room = Room(
            roomNumber = "A123",
            numberOfBeds = 1,
            buildingLevel = 1,
            petFriendly = true
        )

        val result = roomService.addRoom(room)

        assertThat(result).isEqualTo(room)
        assertThat(roomDatabase[result.roomNumber]).isEqualTo(result)
    }
}