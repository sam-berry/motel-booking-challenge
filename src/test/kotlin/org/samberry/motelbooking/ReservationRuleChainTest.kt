package org.samberry.motelbooking

import org.junit.Before
import org.junit.Test
import org.samberry.motelbooking.api.ReservationRequest
import org.samberry.motelbooking.dao.ReservationRuleDAO
import org.samberry.motelbooking.exception.TooManyPetsException
import org.samberry.motelbooking.reservationrule.MaxPetsRule
import org.samberry.motelbooking.reservationrule.ReservationRule
import org.samberry.motelbooking.reservationrule.ReservationRuleChain
import java.time.LocalDate

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