package org.samberry.bravochallenge

import java.time.LocalDate

data class Reservation(
    val startDate: LocalDate,
    val endDate: LocalDate
) : Comparable<Reservation> {
    override fun compareTo(other: Reservation): Int {
        return startDate.compareTo(other.startDate)
    }
}
