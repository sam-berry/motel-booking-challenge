package org.samberry.bravochallenge

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
}
