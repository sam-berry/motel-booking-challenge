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
    fun `can not reserve a room if it does not exist`() {
        roomService.addRoom(baseRoom)

        val reservation = Reservation(
            roomNumber = baseRoom.roomNumber + "blah",
            startDate = today,
            endDate = today.plusDays(2)
        )
        reservationService.reserveRoom(reservation)
    }

    @Test
    fun `can reserve a room if there are no reservations`() {
        val roomNumber = baseRoom.roomNumber

        roomService.addRoom(baseRoom)

        val reservation = Reservation(
            roomNumber = roomNumber,
            startDate = today,
            endDate = today.plusDays(2)
        )
        val result = reservationService.reserveRoom(reservation)

        assertThat(result).isEqualTo(reservation)
        assertThat(reservationDatabase[roomNumber])
            .containsExactly(reservation)
    }

    /**
     * requested:          |------|
     * existing:                  |-------------|
     *            <------------------------------>
     * days:        0      1      2      3      4
     */
    @Test
    fun `can reserve a room adjacent to the start of another reservation`() {
        val roomNumber = baseRoom.roomNumber

        roomService.addRoom(baseRoom)

        val existingReservation = Reservation(
            roomNumber = roomNumber,
            startDate = today.plusDays(2),
            endDate = today.plusDays(3)
        )
        reservationService.reserveRoom(existingReservation)

        val requestedReservation = Reservation(
            roomNumber = roomNumber,
            startDate = today.plusDays(1),
            endDate = today.plusDays(1)
        )
        reservationService.reserveRoom(requestedReservation)

        assertThat(reservationDatabase[roomNumber])
            .containsExactly(requestedReservation, existingReservation)
    }

    /**
     * requested:                 |-------------|
     * existing:           |------|
     *            <------------------------------>
     * days:        0      1      2      3      4
     */
    @Test
    fun `can reserve a room adjacent to the end of another reservation`() {
        val roomNumber = baseRoom.roomNumber

        roomService.addRoom(baseRoom)

        val existingReservation = Reservation(
            roomNumber = roomNumber,
            startDate = today.plusDays(1),
            endDate = today.plusDays(1)
        )
        reservationService.reserveRoom(existingReservation)

        val requestedReservation = Reservation(
            roomNumber = roomNumber,
            startDate = today.plusDays(2),
            endDate = today.plusDays(3)
        )
        reservationService.reserveRoom(requestedReservation)

        assertThat(reservationDatabase[roomNumber])
            .containsExactly(existingReservation, requestedReservation)
    }

    /**
     * requested:                 |-------------|
     * existing:    |------|
     *            <------------------------------>
     * days:        0      1      2      3      4
     */
    @Test
    fun `can reserve a room after another reservation with a gap between`() {
        val roomNumber = baseRoom.roomNumber

        roomService.addRoom(baseRoom)

        val existingReservation = Reservation(
            roomNumber = roomNumber,
            startDate = today,
            endDate = today
        )
        reservationService.reserveRoom(existingReservation)

        val requestedReservation = Reservation(
            roomNumber = roomNumber,
            startDate = today.plusDays(2),
            endDate = today.plusDays(3)
        )
        reservationService.reserveRoom(requestedReservation)

        assertThat(reservationDatabase[roomNumber])
            .containsExactly(existingReservation, requestedReservation)
    }

    /**
     * requested:   |------|
     * existing:                  |-------------|
     *            <------------------------------>
     * days:        0      1      2      3      4
     */
    @Test
    fun `can reserve a room before another reservation with a gap between`() {
        val roomNumber = baseRoom.roomNumber

        roomService.addRoom(baseRoom)

        val existingReservation = Reservation(
            roomNumber = roomNumber,
            startDate = today.plusDays(2),
            endDate = today.plusDays(3)
        )
        reservationService.reserveRoom(existingReservation)

        val requestedReservation = Reservation(
            roomNumber = roomNumber,
            startDate = today,
            endDate = today
        )
        reservationService.reserveRoom(requestedReservation)

        assertThat(reservationDatabase[roomNumber])
            .containsExactly(requestedReservation, existingReservation)
    }

    /**
     * requested:          |------|
     * existing:    |------|      |-------------|
     *            <------------------------------>
     * days:        0      1      2      3      4
     */
    @Test
    fun `can reserve a room and fill the gap between two existing reservations`() {
        val roomNumber = baseRoom.roomNumber

        roomService.addRoom(baseRoom)

        val existingReservationLeft = Reservation(
            roomNumber = roomNumber,
            startDate = today,
            endDate = today
        )
        reservationService.reserveRoom(existingReservationLeft)

        val existingReservationRight = Reservation(
            roomNumber = roomNumber,
            startDate = today.plusDays(2),
            endDate = today.plusDays(3)
        )
        reservationService.reserveRoom(existingReservationRight)

        val requestedReservation = Reservation(
            roomNumber = roomNumber,
            startDate = today.plusDays(1),
            endDate = today.plusDays(1)
        )
        reservationService.reserveRoom(requestedReservation)

        assertThat(reservationDatabase[roomNumber])
            .containsExactly(existingReservationLeft, requestedReservation, existingReservationRight)
    }
}