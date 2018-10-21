package org.samberry.bravochallenge

import java.time.LocalDate

data class ReservationRequest(
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val numberOfBeds: Int
) {
    init {
        if (!checkOutDate.isAfter(checkInDate))
            throw InvalidCheckOutDateException(this)
    }
}
