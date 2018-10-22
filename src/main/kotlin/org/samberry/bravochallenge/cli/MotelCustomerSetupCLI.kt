package org.samberry.bravochallenge.cli

import org.joda.money.Money
import org.samberry.bravochallenge.api.Room
import org.samberry.bravochallenge.dao.PricingRuleDAO
import org.samberry.bravochallenge.pricingrule.BaseRate
import org.samberry.bravochallenge.pricingrule.PRICING_CURRENCY
import org.samberry.bravochallenge.pricingrule.PetFee
import org.samberry.bravochallenge.service.RoomService
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellComponent

@ShellComponent
class MotelCustomerSetupCLI(
    private val roomService: RoomService,
    private val pricingRuleDAO: PricingRuleDAO
) {
    @ShellMethod("Setup rooms for the motel customer")
    fun setupCustomerMotel(): String {
        val rooms = setOf(
            // 1st floor
            Room(roomNumber = "A1", numberOfBeds = 1, buildingLevel = 1, handicapAccessible = true, petFriendly = true),
            Room(roomNumber = "A2", numberOfBeds = 1, buildingLevel = 1, handicapAccessible = true, petFriendly = true),
            Room(roomNumber = "A3", numberOfBeds = 2, buildingLevel = 1, handicapAccessible = true, petFriendly = true),
            Room(roomNumber = "A4", numberOfBeds = 2, buildingLevel = 1, handicapAccessible = true, petFriendly = true),
            Room(roomNumber = "A5", numberOfBeds = 3, buildingLevel = 1, handicapAccessible = true, petFriendly = true),
            Room(roomNumber = "A6", numberOfBeds = 3, buildingLevel = 1, handicapAccessible = true, petFriendly = true),
            // 2nd floor
            Room(roomNumber = "B1", numberOfBeds = 1, buildingLevel = 2, handicapAccessible = false, petFriendly = false),
            Room(roomNumber = "B2", numberOfBeds = 1, buildingLevel = 2, handicapAccessible = false, petFriendly = false),
            Room(roomNumber = "B3", numberOfBeds = 2, buildingLevel = 2, handicapAccessible = false, petFriendly = false),
            Room(roomNumber = "B4", numberOfBeds = 2, buildingLevel = 2, handicapAccessible = false, petFriendly = false),
            Room(roomNumber = "B5", numberOfBeds = 3, buildingLevel = 2, handicapAccessible = false, petFriendly = false),
            Room(roomNumber = "B6", numberOfBeds = 3, buildingLevel = 2, handicapAccessible = false, petFriendly = false)
        )

        val pricingRules = setOf(
            BaseRate(rate = Money.of(PRICING_CURRENCY, 50.0), numberOfBeds = 1),
            BaseRate(rate = Money.of(PRICING_CURRENCY, 75.0), numberOfBeds = 2),
            BaseRate(rate = Money.of(PRICING_CURRENCY, 90.0), numberOfBeds = 3),
            PetFee(fee = Money.of(PRICING_CURRENCY, 20.0))
        )

        val roomResults = rooms
            .asSequence()
            .map { roomService.addRoom(it) }
            .joinToString(separator = "\n", transform = { it.toString() })

        val pricingResults = pricingRules
            .asSequence()
            .map { pricingRuleDAO.saveRule(it) }
            .joinToString(separator = "\n", transform = { it.toString() })

        return "Successfully setup customer.\n\nRooms:\n$roomResults\n\nPricing rules:\n$pricingResults"
    }
}