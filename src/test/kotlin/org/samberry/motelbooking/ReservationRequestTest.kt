package org.samberry.motelbooking

import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Test
import org.samberry.motelbooking.api.ReservationRequest
import org.samberry.motelbooking.exception.InvalidCheckOutDateException
import java.time.LocalDate

class ReservationRequestTest {
    @Test
    fun `check out date must be after check in date`() {
        val checkInDate = LocalDate.now()
        val badCheckOutDates = setOf(checkInDate, checkInDate.minusDays(1), checkInDate.minusYears(1))
        val goodCheckOutDates = setOf(checkInDate.plusDays(1), checkInDate.plusYears(1))

        badCheckOutDates.forEach {
            assertThatExceptionOfType(InvalidCheckOutDateException::class.java).isThrownBy {
                ReservationRequest(
                    checkInDate = checkInDate,
                    checkOutDate = it,
                    numberOfBeds = 1
                )
            }
        }

        // expect no exception thrown
        goodCheckOutDates.forEach {
            ReservationRequest(
                checkInDate = checkInDate,
                checkOutDate = it,
                numberOfBeds = 1
            )
        }
    }
}