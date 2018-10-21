package org.samberry.bravochallenge

import java.lang.RuntimeException
import java.time.LocalDate

class ReservationUnavailableException(
    reservation: Reservation
) : RuntimeException("Reservation for ${reservation.startDate} - ${reservation.endDate} is not available")
