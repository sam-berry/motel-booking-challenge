package org.samberry.bravochallenge

import org.assertj.core.api.Assertions.assertThat
import org.joda.money.Money
import org.junit.Before
import org.junit.Test
import org.samberry.bravochallenge.api.ReservationRequest
import org.samberry.bravochallenge.dao.PricingRuleDAO
import org.samberry.bravochallenge.pricingrule.BaseRate
import org.samberry.bravochallenge.pricingrule.PRICING_CURRENCY
import org.samberry.bravochallenge.pricingrule.PetFee
import org.samberry.bravochallenge.pricingrule.PricingRule
import org.samberry.bravochallenge.pricingrule.PricingRuleChain
import java.time.LocalDate

class PricingRuleChainTest {
    private lateinit var today: LocalDate
    private lateinit var zeroMoney: Money

    private lateinit var pricingRuleDatabase: MutableSet<PricingRule>
    private lateinit var pricingRuleDAO: PricingRuleDAO
    private lateinit var pricingRuleChain: PricingRuleChain

    @Before
    fun setUp() {
        today = LocalDate.now()
        zeroMoney = Money.zero(PRICING_CURRENCY)

        pricingRuleDatabase = mutableSetOf()
        pricingRuleDAO = PricingRuleDAO(pricingRuleDatabase)
        pricingRuleChain = PricingRuleChain(pricingRuleDAO)
    }

    private fun money(amount: Double): Money {
        return Money.of(PRICING_CURRENCY, amount)
    }

    @Test
    fun `base rates can be configured based on number of beds`() {
        val numberOfBeds = 1

        val baseRate = BaseRate(
            numberOfBeds = numberOfBeds,
            rate = money(50.0)
        )

        pricingRuleDatabase.add(baseRate)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = numberOfBeds
        )

        val result = pricingRuleChain.run(request)
        assertThat(result).isEqualTo(money(100.0))
    }

    @Test
    fun `base rate only applies if number of beds match`() {
        val numberOfBeds = 1

        val baseRate = BaseRate(
            numberOfBeds = numberOfBeds,
            rate = money(50.0)
        )

        pricingRuleDatabase.add(baseRate)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = numberOfBeds + 1
        )

        val result = pricingRuleChain.run(request)
        assertThat(result).isEqualTo(zeroMoney)
    }

    @Test
    fun `no base rates results in zero pricing`() {
        val numberOfBeds = 1

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = numberOfBeds
        )

        val result = pricingRuleChain.run(request)
        assertThat(result).isEqualTo(zeroMoney)
    }

    @Test
    fun `pet fees can be configured`() {
        val numberOfBeds = 1

        val baseRate = BaseRate(
            numberOfBeds = numberOfBeds,
            rate = money(50.0)
        )

        val petFee = PetFee(
            fee = money(20.0)
        )

        pricingRuleDatabase.add(baseRate)
        pricingRuleDatabase.add(petFee)

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = numberOfBeds,
            numberOfPets = 2
        )

        val result = pricingRuleChain.run(request)
        assertThat(result).isEqualTo(money(140.0))
    }

    @Test
    fun `many pricing rules can be configured`() {
        val oneBedRate = BaseRate(
            numberOfBeds = 1,
            rate = money(50.0)
        )
        val twoBedRate = BaseRate(
            numberOfBeds = 2,
            rate = money(75.0)
        )
        val threeBedRate = BaseRate(
            numberOfBeds = 3,
            rate = money(90.0)
        )
        val petFee = PetFee(
            fee = money(20.0)
        )

        pricingRuleDatabase.addAll(setOf(
            oneBedRate,
            twoBedRate,
            threeBedRate,
            petFee
        ))

        val oneBedNoPets = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = 1
        )
        val twoBedsTwoPets = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = 2,
            numberOfPets = 2
        )
        val threeBedsOnePet = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = 3,
            numberOfPets = 1
        )

        assertThat(pricingRuleChain.run(oneBedNoPets)).isEqualTo(money(100.0))
        assertThat(pricingRuleChain.run(twoBedsTwoPets)).isEqualTo(money(190.0))
        assertThat(pricingRuleChain.run(threeBedsOnePet)).isEqualTo(money(200.0))
    }
}