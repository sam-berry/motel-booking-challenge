package org.samberry.motelbooking.cli

import org.samberry.motelbooking.api.ReservationRequest
import org.samberry.motelbooking.pricingrule.PricingRuleChain
import org.samberry.motelbooking.service.ReservationService
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import java.time.LocalDate

@ShellComponent
class ReservationCLI(
    private val reservationService: ReservationService,
    private val pricingRuleChain: PricingRuleChain
) {
    @ShellMethod("Book a new reservation")
    fun makeReservation(
        checkInDate: String,
        checkOutDate: String,
        numberOfBeds: Int,
        @ShellOption(defaultValue = "0") numberOfPets: Int,
        handicapAccessible: Boolean
    ): String {
        val request = ReservationRequest(
            checkInDate = LocalDate.parse(checkInDate, CLI_DATE_FORMAT),
            checkOutDate = LocalDate.parse(checkOutDate, CLI_DATE_FORMAT),
            numberOfBeds = numberOfBeds,
            numberOfPets = numberOfPets,
            handicapAccessible = handicapAccessible
        )

        val pricing = pricingRuleChain.run(request)
        reservationService.makeReservation(request)

        return "Room successfully reserved. $pricing will be due at the time of arrival."
    }
}