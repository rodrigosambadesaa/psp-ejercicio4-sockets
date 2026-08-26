package dev.rodrigosambade.seats;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WagonTest {

    @Test
    void reservesSeatAtomically() {
        Wagon wagon = new Wagon(2, 2);

        Wagon.Reservation firstAttempt = wagon.reserve(0, 0);
        Wagon.Reservation secondAttempt = wagon.reserve(0, 0);

        assertEquals(Wagon.Status.RESERVED, firstAttempt.status());
        assertEquals(Wagon.Status.OCCUPIED, secondAttempt.status());
        assertEquals(3, wagon.freeSeats().size());
    }

    @Test
    void rejectsInvalidSeat() {
        Wagon wagon = new Wagon(4, 4);

        Wagon.Reservation reservation = wagon.reserve(9, 9);

        assertEquals(Wagon.Status.INVALID, reservation.status());
    }
}
