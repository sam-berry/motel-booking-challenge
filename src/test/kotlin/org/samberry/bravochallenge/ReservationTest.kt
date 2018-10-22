package org.samberry.bravochallenge

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.util.SortedSet

class ReservationTest {
    private lateinit var baseRoom: Room
    private lateinit var today: LocalDate

    private lateinit var roomDatabase: MutableMap<String, Room>
    private lateinit var roomDAO: RoomDAO
    private lateinit var roomService: RoomService
    private lateinit var reservationDatabase: MutableMap<String, SortedSet<Reservation>>
    private lateinit var reservationDAO: ReservationDAO
    private lateinit var availabilitySearchService: AvailabilitySearchService
    private lateinit var reservationService: ReservationService

    @Before
    fun setUp() {
        baseRoom = Room(
            roomNumber = "A123",
            numberOfBeds = 1,
            buildingLevel = 1
        )
        today = LocalDate.now()

        roomDatabase = mutableMapOf()
        roomDAO = RoomDAO(roomDatabase)
        roomService = RoomService(roomDAO)
        reservationDatabase = mutableMapOf()
        reservationDAO = ReservationDAO(reservationDatabase)
        availabilitySearchService = AvailabilitySearchService(reservationDAO, roomService)
        reservationService = ReservationService(reservationDAO, availabilitySearchService)
    }

    private fun setUpRoom(room: Room) {
        roomDatabase[room.roomNumber] = room
    }

    private fun setUpReservation(room: Room, reservation: Reservation) {
        val reservations = reservationDatabase[room.roomNumber]
        if (reservations == null)
            reservationDatabase[room.roomNumber] = sortedSetOf(reservation)
        else
            reservations.add(reservation)
    }

    @Test
    fun `can reserve a room if there are no reservations`() {
        val room = baseRoom
        setUpRoom(room)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = room.numberOfBeds
        )
        reservationService.reserveRoom(request)

        assertThat(reservationDatabase[room.roomNumber]).containsExactly(request.toReservation())
    }

    @Test(expected = NoAvailableRoomsException::class)
    fun `cannot reserve if no rooms match the 'number of beds' criteria`() {
        val room = baseRoom.copy(numberOfBeds = 1)
        setUpRoom(room)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(1),
            numberOfBeds = room.numberOfBeds + 1
        )

        reservationService.reserveRoom(request)
    }

    /**
     * requested:   |-------------|
     * existing:
     * existing:    |-------------|
     *            <------------------------------>
     * days:        0      1      2      3      4
     */
    @Test
    fun `can reserve if one room is booked but another is not`() {
        val numberOfBeds = 3
        val bookedRoom = baseRoom.copy(numberOfBeds = numberOfBeds)
        setUpRoom(bookedRoom)
        val availableRoom = baseRoom.copy(roomNumber = "B123", numberOfBeds = numberOfBeds)
        setUpRoom(availableRoom)

        setUpReservation(bookedRoom, Reservation(
            startDate = today,
            endDate = today.plusDays(1)
        ))

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = numberOfBeds
        )
        reservationService.reserveRoom(request)

        assertThat(reservationDatabase[availableRoom.roomNumber]).containsExactly(request.toReservation())
    }

    /**
     * requested (2 bed):   |-------------|
     * existing (1 bed):
     * existing (2 bed):    |-------------|
     *                    <------------------------------>
     * days:                0      1      2      3      4
     */
    @Test(expected = NoAvailableRoomsException::class)
    fun `cannot reserve if one room is booked and a non-matching room is available`() {
        val numberOfBeds = 2
        val bookedRoom = baseRoom.copy(numberOfBeds = numberOfBeds)
        setUpRoom(bookedRoom)
        val availableRoom = baseRoom.copy(roomNumber = "B123", numberOfBeds = numberOfBeds - 1)
        setUpRoom(availableRoom)

        setUpReservation(bookedRoom, Reservation(
            startDate = today,
            endDate = today.plusDays(1)
        ))

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = numberOfBeds
        )
        reservationService.reserveRoom(request)
    }

    /**
     * requested:          |------|
     * existing:                  |-------------|
     *            <------------------------------>
     * days:        0      1      2      3      4
     */
    @Test
    fun `can reserve a room adjacent to the start of another reservation`() {
        val room = baseRoom
        setUpRoom(room)

        val existingReservation = Reservation(
            startDate = today.plusDays(2),
            endDate = today.plusDays(3)
        )
        setUpReservation(room, existingReservation)

        val request = ReservationRequest(
            checkInDate = today.plusDays(1),
            checkOutDate = today.plusDays(2),
            numberOfBeds = room.numberOfBeds
        )
        reservationService.reserveRoom(request)

        assertThat(reservationDatabase[room.roomNumber])
            .containsExactly(request.toReservation(), existingReservation)
    }

    /**
     * requested:                 |-------------|
     * existing:           |------|
     *            <------------------------------>
     * days:        0      1      2      3      4
     */
    @Test
    fun `can reserve a room adjacent to the end of another reservation`() {
        val room = baseRoom
        setUpRoom(room)

        val existingReservation = Reservation(
            startDate = today.plusDays(1),
            endDate = today.plusDays(1)
        )
        setUpReservation(room, existingReservation)

        val request = ReservationRequest(
            checkInDate = today.plusDays(2),
            checkOutDate = today.plusDays(4),
            numberOfBeds = room.numberOfBeds
        )
        reservationService.reserveRoom(request)

        assertThat(reservationDatabase[room.roomNumber])
            .containsExactly(existingReservation, request.toReservation())
    }

    /**
     * requested:                 |-------------|
     * existing:    |------|
     *            <------------------------------>
     * days:        0      1      2      3      4
     */
    @Test
    fun `can reserve a room after another reservation with a gap between`() {
        val room = baseRoom
        setUpRoom(room)

        val existingReservation = Reservation(
            startDate = today,
            endDate = today
        )
        setUpReservation(room, existingReservation)

        val request = ReservationRequest(
            checkInDate = today.plusDays(2),
            checkOutDate = today.plusDays(4),
            numberOfBeds = room.numberOfBeds
        )
        reservationService.reserveRoom(request)

        assertThat(reservationDatabase[room.roomNumber])
            .containsExactly(existingReservation, request.toReservation())
    }

    /**
     * requested:   |------|
     * existing:                  |-------------|
     *            <------------------------------>
     * days:        0      1      2      3      4
     */
    @Test
    fun `can reserve a room before another reservation with a gap between`() {
        val room = baseRoom
        setUpRoom(room)

        val existingReservation = Reservation(
            startDate = today.plusDays(2),
            endDate = today.plusDays(3)
        )
        setUpReservation(room, existingReservation)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(1),
            numberOfBeds = room.numberOfBeds
        )
        reservationService.reserveRoom(request)

        assertThat(reservationDatabase[room.roomNumber])
            .containsExactly(request.toReservation(), existingReservation)
    }

    /**
     * requested:          |------|
     * existing:    |------|      |-------------|
     *            <------------------------------>
     * days:        0      1      2      3      4
     */
    @Test
    fun `can reserve a room and fill the gap between two existing reservations`() {
        val room = baseRoom
        setUpRoom(room)

        val existingReservationLeft = Reservation(
            startDate = today,
            endDate = today
        )
        setUpReservation(room, existingReservationLeft)

        val existingReservationRight = Reservation(
            startDate = today.plusDays(2),
            endDate = today.plusDays(3)
        )
        setUpReservation(room, existingReservationRight)

        val request = ReservationRequest(
            checkInDate = today.plusDays(1),
            checkOutDate = today.plusDays(2),
            numberOfBeds = room.numberOfBeds
        )
        reservationService.reserveRoom(request)

        assertThat(reservationDatabase[room.roomNumber])
            .containsExactly(existingReservationLeft, request.toReservation(), existingReservationRight)
    }

    /**
     * requested:   |-------------|
     * existing:           |-------------|
     *            <------------------------------>
     * days:        0      1      2      3      4
     */
    @Test(expected = NoAvailableRoomsException::class)
    fun `cannot reserve a room that overlaps with the start of another reservation`() {
        val room = baseRoom
        setUpRoom(room)

        val existingReservation = Reservation(
            startDate = today.plusDays(1),
            endDate = today.plusDays(2)
        )
        setUpReservation(room, existingReservation)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = room.numberOfBeds
        )
        reservationService.reserveRoom(request)
    }

    /**
     * requested:          |-------------|
     * existing:    |-------------|
     *            <------------------------------>
     * days:        0      1      2      3      4
     */
    @Test(expected = NoAvailableRoomsException::class)
    fun `cannot reserve a room that overlaps with the end of another reservation`() {
        val room = baseRoom
        setUpRoom(room)

        val existingReservation = Reservation(
            startDate = today,
            endDate = today.plusDays(1)
        )
        setUpReservation(room, existingReservation)

        val request = ReservationRequest(
            checkInDate = today.plusDays(1),
            checkOutDate = today.plusDays(3),
            numberOfBeds = room.numberOfBeds
        )
        reservationService.reserveRoom(request)
    }

    /**
     * requested:          |------|
     * existing:    |--------------------|
     *            <------------------------------>
     * days:        0      1      2      3      4
     */
    @Test(expected = NoAvailableRoomsException::class)
    fun `cannot reserve a room that is inside another reservation`() {
        val room = baseRoom
        setUpRoom(room)

        val existingReservation = Reservation(
            startDate = today,
            endDate = today.plusDays(2)
        )
        setUpReservation(room, existingReservation)

        val request = ReservationRequest(
            checkInDate = today.plusDays(1),
            checkOutDate = today.plusDays(2),
            numberOfBeds = room.numberOfBeds
        )
        reservationService.reserveRoom(request)
    }

    /**
     * requested:   |--------------------|
     * existing:           |------|
     *            <------------------------------>
     * days:        0      1      2      3      4
     */
    @Test(expected = NoAvailableRoomsException::class)
    fun `cannot reserve a room if another reservation is inside of it`() {
        val room = baseRoom
        setUpRoom(room)

        val existingReservation = Reservation(
            startDate = today.plusDays(1),
            endDate = today.plusDays(1)
        )
        setUpReservation(room, existingReservation)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(3),
            numberOfBeds = room.numberOfBeds
        )
        reservationService.reserveRoom(request)
    }

    @Test(expected = NoAvailableRoomsException::class)
    fun `cannot reserve a room with pets if it is not pet friendly`() {
        val room = baseRoom.copy(petFriendly = false)
        setUpRoom(room)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = room.numberOfBeds,
            numberOfPets = 1
        )
        reservationService.reserveRoom(request)
    }

    @Test
    fun `can reserve a room with pets if it is pet friendly`() {
        val room = baseRoom.copy(petFriendly = true)
        setUpRoom(room)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = room.numberOfBeds,
            numberOfPets = 1
        )
        reservationService.reserveRoom(request)

        assertThat(reservationDatabase[room.roomNumber]).containsExactly(request.toReservation())
    }

    @Test(expected = NoAvailableRoomsException::class)
    fun `cannot make a handicap accessible reservation if no handicap accessible rooms are available`() {
        val room = baseRoom.copy(handicapAccessible = false)
        setUpRoom(room)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = room.numberOfBeds,
            handicapAccessible = true
        )
        reservationService.reserveRoom(request)
    }

    @Test
    fun `can make a handicap accessible reservation if a handicap accessible room is available`() {
        val room = baseRoom.copy(handicapAccessible = true)
        setUpRoom(room)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = room.numberOfBeds,
            handicapAccessible = true
        )
        reservationService.reserveRoom(request)

        assertThat(reservationDatabase[room.roomNumber]).containsExactly(request.toReservation())
    }

    @Test
    fun `will prefer booking non-handicap accessible rooms for non-handicap requests`() {
        val nonHandicapRoomNumber = "A4"
        setOf(
            baseRoom.copy(roomNumber = "A1", handicapAccessible = true),
            baseRoom.copy(roomNumber = "A2", handicapAccessible = true),
            baseRoom.copy(roomNumber = "A3", handicapAccessible = true),
            baseRoom.copy(roomNumber = nonHandicapRoomNumber, handicapAccessible = false),
            baseRoom.copy(roomNumber = "A5", handicapAccessible = true),
            baseRoom.copy(roomNumber = "A6", handicapAccessible = true)
        ).forEach { setUpRoom(it) }

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = baseRoom.numberOfBeds
        )
        reservationService.reserveRoom(request)

        assertThat(reservationDatabase[nonHandicapRoomNumber]).containsExactly(request.toReservation())
    }

    @Test
    fun `will book a handicap accessible room for a non-handicap request if it is the only option`() {
        val room = baseRoom.copy(handicapAccessible = true)
        setUpRoom(room)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = room.numberOfBeds
        )
        reservationService.reserveRoom(request)

        assertThat(reservationDatabase[room.roomNumber]).containsExactly(request.toReservation())
    }

    @Test
    fun `will book a handicap accessible room for a non-handicap request if other options are reserved`() {
        val handicapRoom = baseRoom.copy(roomNumber = "A1", handicapAccessible = true)
        setUpRoom(handicapRoom)
        val nonHandicapRoom = baseRoom.copy(roomNumber = "A2", handicapAccessible = false)
        setUpRoom(nonHandicapRoom)

        val existingReservation = Reservation(
            startDate = today,
            endDate = today.plusDays(1)
        )
        setUpReservation(nonHandicapRoom, existingReservation)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = baseRoom.numberOfBeds
        )
        reservationService.reserveRoom(request)

        assertThat(reservationDatabase[handicapRoom.roomNumber]).containsExactly(request.toReservation())
    }

    @Test
    fun `will prefer booking pet non-friendly rooms for requests with no pets`() {
        val petNonFriendlyRoomNumber = "A4"
        setOf(
            baseRoom.copy(roomNumber = "A1", petFriendly = true),
            baseRoom.copy(roomNumber = "A2", petFriendly = true),
            baseRoom.copy(roomNumber = "A3", petFriendly = true),
            baseRoom.copy(roomNumber = petNonFriendlyRoomNumber, petFriendly = false),
            baseRoom.copy(roomNumber = "A5", petFriendly = true),
            baseRoom.copy(roomNumber = "A6", petFriendly = true)
        ).forEach { setUpRoom(it) }

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = baseRoom.numberOfBeds
        )
        reservationService.reserveRoom(request)

        assertThat(reservationDatabase[petNonFriendlyRoomNumber]).containsExactly(request.toReservation())
    }

    @Test
    fun `will book a pet friendly room for a request with no pets if it is the only option`() {
        val room = baseRoom.copy(petFriendly = true)
        setUpRoom(room)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = room.numberOfBeds
        )
        reservationService.reserveRoom(request)

        assertThat(reservationDatabase[room.roomNumber]).containsExactly(request.toReservation())
    }

    @Test
    fun `will book a pet friendly room for a request with no pets if other options are reserved`() {
        val petFriendlyRoom = baseRoom.copy(roomNumber = "A1", petFriendly = true)
        setUpRoom(petFriendlyRoom)
        val petNotFriendlyRoom = baseRoom.copy(roomNumber = "A2", petFriendly = false)
        setUpRoom(petNotFriendlyRoom)

        val existingReservation = Reservation(
            startDate = today,
            endDate = today.plusDays(1)
        )
        setUpReservation(petNotFriendlyRoom, existingReservation)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = baseRoom.numberOfBeds
        )
        reservationService.reserveRoom(request)

        assertThat(reservationDatabase[petFriendlyRoom.roomNumber]).containsExactly(request.toReservation())
    }
}