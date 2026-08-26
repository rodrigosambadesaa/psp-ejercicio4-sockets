package dev.rodrigosambade.seats;
import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
class WagonTest{@Test void reservesAtomically(){var w=new Wagon(2,2);assertEquals(Wagon.Status.RESERVED,w.reserve(0,0).status());assertEquals(Wagon.Status.OCCUPIED,w.reserve(0,0).status());assertEquals(3,w.freeSeats().size());}@Test void rejectsInvalidSeat(){assertEquals(Wagon.Status.INVALID,new Wagon(4,4).reserve(9,9).status());}}
