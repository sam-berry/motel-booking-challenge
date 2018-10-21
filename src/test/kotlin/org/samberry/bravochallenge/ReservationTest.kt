package org.samberry.bravochallenge

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.util.SortedSet

class ReservationTest {
    private lateinit var baseRoom: Room
    private lateinit var today: LocalDate

    private lateinit var roomService: RoomService
    private lateinit var reservationDatabase: MutableMap<String, SortedSet<Reservation>>
    private lateinit var reservationService: ReservationService

    @Before
    fun setUp() {
        baseRoom = Room(
            roomNumber = "A123",
            numberOfBeds = 1,
            buildingLevel = 1
        )
        today = LocalDate.now()

        roomService = RoomService()
        reservationDatabase = mutableMapOf()
        reservationService = ReservationService(reservationDatabase, roomService)
    }

    @Test(expected = RuntimeException::class)
    fun `can not book a room if it does not exist`() {
        roomService.addRoom(baseRoom)

        val reservation = Reservation(
            roomNumber = baseRoom.roomNumber + "blah",
            startDate = today,
            endDate = today.plusDays(2)
        )
        reservationService.reserveRoom(reservation)
    }

    @Test
    fun `can book a room if there are no reservations`() {
        roomService.addRoom(baseRoom)

        val reservation = Reservation(
            roomNumber = baseRoom.roomNumber,
            startDate = today,
            endDate = today.plusDays(2)
        )
        val result = reservationService.reserveRoom(reservation)

        assertThat(result).isEqualTo(reservation)
        assertThat(reservationDatabase[reservation.roomNumber])
            .containsExactly(reservation)
    }

    /**
     * requested:          |------|
     * existing:                  |-------------|
     *            <------------------------------>
     * days:        0      1      2      3      4
     */
    @Test
    fun `can book a room adjacent to the start of another reservation`() {
        val roomNumber = baseRoom.roomNumber

        roomService.addRoom(baseRoom)

        val existingReservation = Reservation(
            roomNumber = roomNumber,
            startDate = today.plusDays(2),
            endDate = today.plusDays(3)
        )
        reservationService.reserveRoom(existingReservation)

        val newReservation = Reservation(
            roomNumber = roomNumber,
            startDate = today.plusDays(1),
            endDate = today.plusDays(1)
        )
        reservationService.reserveRoom(newReservation)

        assertThat(reservationDatabase[roomNumber])
            .containsExactly(newReservation, existingReservation)
    }
}