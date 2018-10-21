package org.samberry.bravochallenge

import java.time.LocalDate

/**
 * Note: it is important to not confuse "end date" with "checkout date".
 * For example, if a customer wants to stay in a room for one night they
 * expect to say, "I will arrive on 1/1 and depart on 1/2". If the
 * system treated this as a reservation with start 1/1 and end 1/2, it
 * would prevent another reservation from booking 1/2. It is perhaps
 * easiest to think of start and end as "nights". So, the example would
 * start the night of 1/1 and end the night of 1/1. Checkout date,
 * is end date + 1 day and should not be used in calculations.
 */
data class Reservation(
    val roomNumber: String,
    val startDate: LocalDate,
    val endDate: LocalDate
) : Comparable<Reservation> {
    override fun compareTo(other: Reservation): Int {
        return startDate.compareTo(other.startDate)
    }
}
