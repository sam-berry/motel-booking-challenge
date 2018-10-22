package org.samberry.bravochallenge.cli

import org.samberry.bravochallenge.api.ReservationRequest
import org.samberry.bravochallenge.pricingrule.PricingRuleChain
import org.samberry.bravochallenge.service.AvailabilitySearchService
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@ShellComponent
class AvailabilitySearchCLI(
    private val availabilitySearchService: AvailabilitySearchService,
    private val pricingRuleChain: PricingRuleChain
) {
    @ShellMethod("Query motel availability based on reservation criteria")
    fun searchAvailability(
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
        val pricing = pricingRuleChain.run(request)

        return if (rooms.isEmpty())
            "No rooms available that match that criteria."
        else {
            val numberOfRooms = rooms.size
            val roomDetails = rooms.joinToString(separator = "\n", transform = { it.toString() })

            "There are $numberOfRooms rooms available for $pricing. See room details below.\n$roomDetails"
        }
    }

    companion object {
        private var DATE_FORMAT = DateTimeFormatter.ofPattern("MM/d/yyyy")
    }
}