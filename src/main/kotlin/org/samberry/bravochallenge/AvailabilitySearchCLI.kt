package org.samberry.bravochallenge

import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellOption
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private var DATE_FORMAT = DateTimeFormatter.ofPattern("MM/d/yyyy")

@ShellComponent
class AvailabilitySearchCLI(
    private val availabilitySearchService: AvailabilitySearchService
) {
    @ShellMethod("Query motel availability based on reservation criteria")
    fun availabilitySearch(
        checkInDate: String,
        checkOutDate: String,
        numberOfBeds: Int,
        @ShellOption(defaultValue = "0") numberOfPets: Int,
        handicapAccessible: Boolean
    ): String {
        val request = ReservationRequest(
            checkInDate = LocalDate.parse(checkInDate, DATE_FORMAT),
            checkOutDate = LocalDate.parse(checkOutDate, DATE_FORMAT),
            numberOfBeds = numberOfBeds,
            numberOfPets = numberOfPets,
            handicapAccessible = handicapAccessible
        )

        val rooms = availabilitySearchService.findAvailableRooms(request)

        return if (rooms.isEmpty())
            "No rooms available that match that criteria"
        else
            rooms.joinToString(separator = "\n", transform = { it.toString() })
    }
}