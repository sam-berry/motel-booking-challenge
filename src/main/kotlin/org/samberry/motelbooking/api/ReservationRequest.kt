package org.samberry.motelbooking.api

import org.samberry.motelbooking.exception.InvalidCheckOutDateException
import java.time.LocalDate

data class ReservationRequest(
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val numberOfBeds: Int,
    val numberOfPets: Int = 0,
    val handicapAccessible: Boolean = false
) {
    init {
        if (!checkOutDate.isAfter(checkInDate))
            throw InvalidCheckOutDateException(this)
    }

    fun toReservation(): Reservation {
        return Reservation(
            startDate = checkInDate,
            endDate = checkOutDate.minusDays(1)
        )
    }
}
