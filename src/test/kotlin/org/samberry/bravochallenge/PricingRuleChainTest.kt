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

    private fun money(amount: Double): Money {
        return Money.of(CurrencyUnit.USD, amount)
    }

    @Test
    fun `base rates can be configured based on number of beds`() {
        val numberOfBeds = 1

        val baseRate = BaseRate(
            numberOfBeds = numberOfBeds,
            rate = money(50.0)
        )

        val pricingChain = PricingRuleChain(
            baseRate
        )

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = numberOfBeds
        )

        val result = pricingChain.run(request)
        assertThat(result).isEqualTo(money(100.0))
    }

    @Test
    fun `base rate only applies if number of beds match`() {
        val numberOfBeds = 1

        val baseRate = BaseRate(
            numberOfBeds = numberOfBeds,
            rate = money(50.0)
        )

        val pricingChain = PricingRuleChain(
            baseRate
        )

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = numberOfBeds + 1
        )

        val result = pricingChain.run(request)
        assertThat(result).isEqualTo(zeroMoney)
    }

    @Test
    fun `no base rates results in zero pricing`() {
        val numberOfBeds = 1

        val pricingChain = PricingRuleChain()

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = numberOfBeds
        )

        val result = pricingChain.run(request)
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

        val pricingChain = PricingRuleChain(
            baseRate,
            petFee
        )

        val request = ReservationRequest(
            checkInDate = today,
            checkOutDate = today.plusDays(2),
            numberOfBeds = numberOfBeds,
            numberOfPets = 2
        )

        val result = pricingChain.run(request)
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

        val pricingChain = PricingRuleChain(
            oneBedRate,
            twoBedRate,
            threeBedRate,
            petFee
        )

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

        assertThat(pricingChain.run(oneBedNoPets)).isEqualTo(money(100.0))
        assertThat(pricingChain.run(twoBedsTwoPets)).isEqualTo(money(190.0))
        assertThat(pricingChain.run(threeBedsOnePet)).isEqualTo(money(200.0))
    }
}