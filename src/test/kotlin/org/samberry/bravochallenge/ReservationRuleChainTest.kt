package org.samberry.bravochallenge

import org.assertj.core.api.Assertions.assertThat
import org.joda.money.Money
import org.junit.Before
import org.junit.Test
import org.samberry.bravochallenge.api.ReservationRequest
import org.samberry.bravochallenge.dao.PricingRuleDAO
import org.samberry.bravochallenge.dao.ReservationRuleDAO
import org.samberry.bravochallenge.exception.TooManyPetsException
import org.samberry.bravochallenge.pricingrule.BaseRate
import org.samberry.bravochallenge.pricingrule.PRICING_CURRENCY
import org.samberry.bravochallenge.pricingrule.PetFee
import org.samberry.bravochallenge.pricingrule.PricingRule
import org.samberry.bravochallenge.pricingrule.PricingRuleChain
import org.samberry.bravochallenge.reservationrule.MaxPetsRule
import org.samberry.bravochallenge.reservationrule.ReservationRule
import org.samberry.bravochallenge.reservationrule.ReservationRuleChain
import java.time.LocalDate
import kotlin.math.exp

class ReservationRuleChainTest {
    private lateinit var today: LocalDate

    private lateinit var reservationRuleDatabase: MutableSet<ReservationRule>
    private lateinit var reservationRuleDAO: ReservationRuleDAO
    private lateinit var reservationRuleChain: ReservationRuleChain

    @Before
    fun setUp() {
        today = LocalDate.now()

        reservationRuleDatabase = mutableSetOf()
        reservationRuleDAO = ReservationRuleDAO(reservationRuleDatabase)
        reservationRuleChain = ReservationRuleChain(reservationRuleDAO)
    }

    @Test(expected = TooManyPetsException::class)
    fun `throws an error if too many pets are requested`() {
        val maxAllowedPets = 2
        val maxPetsRule = MaxPetsRule(
            maxAllowedPets = maxAllowedPets
        )

        reservationRuleDatabase.add(maxPetsRule)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = 1,
            numberOfPets = maxAllowedPets + 1
        )

        reservationRuleChain.run(request)
    }

    @Test
    fun `allows the maximum number of pets`() {
        val maxAllowedPets = 2
        val maxPetsRule = MaxPetsRule(
            maxAllowedPets = maxAllowedPets
        )

        reservationRuleDatabase.add(maxPetsRule)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = 1,
            numberOfPets = maxAllowedPets
        )

        reservationRuleChain.run(request) // no exception means success
    }

    @Test
    fun `allows less than the maximum number of pets`() {
        val maxAllowedPets = 2
        val maxPetsRule = MaxPetsRule(
            maxAllowedPets = maxAllowedPets
        )

        reservationRuleDatabase.add(maxPetsRule)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = 1,
            numberOfPets = maxAllowedPets - 1
        )

        reservationRuleChain.run(request) // no exception means success
    }
}