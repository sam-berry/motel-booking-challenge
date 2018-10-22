package org.samberry.bravochallenge

import org.assertj.core.api.Assertions.assertThat
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.Before
import org.junit.Test
import org.samberry.bravochallenge.api.ReservationRequest
import org.samberry.bravochallenge.pricing.BaseRate
import org.samberry.bravochallenge.pricing.PRICING_ROUNDING_MODE
import org.samberry.bravochallenge.pricing.PetFee
import org.samberry.bravochallenge.pricing.PricingRuleChain
import java.time.LocalDate

class PricingRuleChainTest {
    private lateinit var today: LocalDate
    private lateinit var zeroMoney: Money

    @Before
    fun setUp() {
        today = LocalDate.now()
        zeroMoney = Money.zero(CurrencyUnit.USD)
    }

    @Test
    fun `base rates can be configured based on number of beds`() {
        val numberOfBeds = 1

        val baseRate = BaseRate(
            numberOfBeds = numberOfBeds,
            rate = Money.of(CurrencyUnit.USD, 50.0)
        )

        val pricingRules = PricingRuleChain(
            baseRate
        )

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = numberOfBeds
        )

        val result = pricingRules.run(request)

        assertThat(result).isEqualTo(baseRate.rate)
    }

    @Test
    fun `base rate only applies if number of beds match`() {
        val numberOfBeds = 1

        val baseRate = BaseRate(
            numberOfBeds = numberOfBeds,
            rate = Money.of(CurrencyUnit.USD, 50.0)
        )

        val pricingRules = PricingRuleChain(
            baseRate
        )

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = numberOfBeds + 1
        )

        val result = pricingRules.run(request)

        assertThat(result).isEqualTo(zeroMoney)
    }

    @Test
    fun `no base rates results in zero pricing`() {
        val numberOfBeds = 1

        val pricingRules = PricingRuleChain()

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = numberOfBeds
        )

        val result = pricingRules.run(request)

        assertThat(result).isEqualTo(zeroMoney)
    }

    @Test
    fun `pet fees can be configured`() {
        val numberOfBeds = 1

        val baseRate = BaseRate(
            numberOfBeds = numberOfBeds,
            rate = Money.of(CurrencyUnit.USD, 50.0)
        )

        val petFee = PetFee(
            fee = Money.of(CurrencyUnit.USD, 20.0)
        )

        val pricingRules = PricingRuleChain(
            baseRate,
            petFee
        )

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = numberOfBeds,
            numberOfPets = 2
        )

        val result = pricingRules.run(request)
        val total = baseRate.rate.plus(petFee.fee.multipliedBy(request.numberOfPets.toDouble(), PRICING_ROUNDING_MODE))
        assertThat(result).isEqualTo(total)
    }
}