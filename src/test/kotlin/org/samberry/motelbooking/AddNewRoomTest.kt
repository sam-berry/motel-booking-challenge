package org.samberry.motelbooking

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.samberry.motelbooking.api.Room
import org.samberry.motelbooking.dao.RoomDAO
import org.samberry.motelbooking.service.RoomService

class AddNewRoomTest {
    private lateinit var baseRoom: Room

    private lateinit var roomDatabase: MutableMap<String, Room>
    private lateinit var roomDAO: RoomDAO
    private lateinit var roomService: RoomService

    @Before
    fun setUp() {
        baseRoom = Room(
            roomNumber = "A123",
            numberOfBeds = 1,
            buildingLevel = 1
        )

        roomDatabase = mutableMapOf()
        roomDAO = RoomDAO(roomDatabase)
        roomService = RoomService(roomDAO)
    }

    private fun verifyRoom(actual: Room, expected: Room) {
        assertThat(actual).isEqualTo(expected)
        assertThat(roomDatabase[actual.roomNumber]).isEqualTo(expected)
    }

    @Test
    fun `can add a single bed room`() {
        val room = baseRoom.copy(numberOfBeds = 1)
        val result = roomService.addRoom(room)
        verifyRoom(result, room)
    }

    @Test
    fun `can add a double bed room`() {
        val room = baseRoom.copy(numberOfBeds = 2)
        val result = roomService.addRoom(room)
        verifyRoom(result, room)
    }

    @Test
    fun `can add a triple bed room`() {
        val room = baseRoom.copy(numberOfBeds = 3)
        val result = roomService.addRoom(room)
        verifyRoom(result, room)
    }

    @Test
    fun `can add a room on the second floor`() {
        val room = baseRoom.copy(buildingLevel = 2)
        val result = roomService.addRoom(room)
        verifyRoom(result, room)
    }

    @Test
    fun `can add a handicap accessible room`() {
        val room = baseRoom.copy(handicapAccessible = true)
        val result = roomService.addRoom(room)
        verifyRoom(result, room)
    }

    @Test
    fun `can add a pet friendly room`() {
        val room = baseRoom.copy(petFriendly = true)
        val result = roomService.addRoom(room)
        verifyRoom(result, room)
    }

    @Test
    fun `can add multiple rooms`() {
        val rooms = setOf(
            baseRoom.copy(
                roomNumber = "A345",
                numberOfBeds = 3
            ),
            baseRoom.copy(
                roomNumber = "B123",
                buildingLevel = 2
            ),
            baseRoom.copy(
                roomNumber = "C987",
                numberOfBeds = 2,
                handicapAccessible = true
            ),
            baseRoom.copy(
                roomNumber = "D543",
                petFriendly = true
            )
        )

        rooms.forEach { roomService.addRoom(it) }
        rooms.forEach { assertThat(roomDatabase[it.roomNumber]).isEqualTo(it) }
    }
}