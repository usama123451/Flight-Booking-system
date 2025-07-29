package example;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import it.polito.flightbooking.FlightException;
import it.polito.flightbooking.FlightManager;

public class TestApp {

    private FlightManager fm;

    @Before
    public void setUp() {
        fm = new FlightManager();
    }

    /* R1: airports & flight legs */

    @Test 
    public void testR1() throws FlightException {
        fm.addAirport("Caselle", "Torino", 45.2, 7.65);

        Collection<String> airports = fm.listAirports();
        assertNotNull("Missing airports", airports);
        assertEquals(1, airports.size());
        assertTrue(airports.contains("Torino-Caselle"));

        assertThrows("Expected FlightException when airport name is duplicated", FlightException.class,
        ()->fm.addAirport("Caselle", "Torino", 1.1, 1.1));

        fm.addAirport("FCO", "Roma", 41.8, 12.25);
        fm.addAirport("NAP", "Napoli", 40.85, 14.29);

        fm.defineRoute("Torino-Caselle", "Roma-FCO", "Napoli-NAP");
    }

    /* R2: airplanes */

    @Test 
    public void testR2() throws FlightException {
        fm.addAirport("TRN", "Torino", 45.2, 7.65);
        fm.addAirport("FCO", "Roma", 41.8, 12.25);
        fm.addAirport("NAP", "Napoli", 40.85, 14.29);
        fm.defineRoute("Torino-TRN", "Roma-FCO", "Napoli-NAP");

        fm.addPlane("P1", 50);
        fm.addPlane("P2", 100);

        Map<String,Integer> seats = fm.getSeats();
        assertNotNull("Missing seats", seats);
        assertEquals("Missing planes", 2, seats.size());
        assertNotNull("Missing plane P2" , seats.get("P2"));
        assertEquals("Wrong number of seats on plane P1" , 100, seats.get("P2").intValue());

        int s1 = fm.assignPlaneToLeg("Torino-TRN", "Roma-FCO", "P1");
        int s2 = fm.assignPlaneToLeg("Roma-FCO", "Napoli-NAP", "P2");

        assertEquals(50, s1);
        assertEquals(100, s2);
    }

    /* R3: Booking & availability */

    @Test 
    public void testR3_SeatBookings() throws FlightException {
        fm.addAirport("TRN", "Torino", 45.2, 7.65);
        fm.addAirport("FCO", "Roma", 41.8, 12.25);
        fm.addAirport("NAP", "Napoli", 40.85, 14.29);
        fm.defineRoute("Torino-TRN", "Roma-FCO", "Napoli-NAP");

        fm.addPlane("P1", 50);
        fm.addPlane("P2", 100);
        fm.assignPlaneToLeg("Torino-TRN", "Roma-FCO", "P1");
        fm.assignPlaneToLeg("Roma-FCO", "Napoli-NAP", "P2");

        Map<String, List<Integer>> seats = fm.findAvailableSeats("Torino-TRN", "Napoli-NAP");
        assertNotNull(seats);
        assertEquals(50, seats.values().iterator().next().size());

        
        String bookId = fm.bookSeat("Pass1", "Torino-TRN", "Napoli-NAP", 1);
        assertNotNull(bookId);
        assertEquals(6, bookId.length());
        fm.bookSeat("Pass1", "Torino-TRN", "Napoli-NAP", 10);

        assertThrows("Expected FlightException when seat not available", FlightException.class,
        ()->fm.bookSeat("Pass2", "Torino-TRN", "Napoli-NAP", 55));
    }
    
    /* R4: Stats */

    @Test
    public void testR4_Stats() throws FlightException {
        fm.addAirport("TRN", "Torino", 45.2, 7.65);
        fm.addAirport("FCO", "Roma", 41.8, 12.25);
        fm.addAirport("NAP", "Napoli", 40.85, 14.29);
        fm.addAirport("MXP", "Milano", 45.62, 8.72);
        fm.defineRoute("Torino-TRN", "Roma-FCO", "Napoli-NAP");
        fm.defineRoute("Milano-MXP", "Roma-FCO", "Napoli-NAP");

        fm.addPlane("P1", 10);
        fm.addPlane("P2", 20);
        fm.addPlane("P3", 20);
        fm.assignPlaneToLeg("Torino-TRN", "Roma-FCO", "P1");
        fm.assignPlaneToLeg("Roma-FCO", "Napoli-NAP", "P2");
        fm.assignPlaneToLeg("Milano-MXP", "Roma-FCO", "P3");

        String[] bookIds = new String[2];
        bookIds[0] = fm.bookSeat("Pass1", "Torino-TRN", "Napoli-NAP", 1);
        bookIds[1] = fm.bookSeat("Pass2", "Milano-MXP", "Napoli-NAP", 10);

        double rate = fm.occupationRate("Torino-TRN", "Roma-FCO");
        assertEquals(0.1, rate, 0.001);

        rate = fm.occupationRate("Roma-FCO", "Napoli-NAP");
        assertEquals(0.1, rate, 0.001);

        List<String> bookings = fm.listBookingsForLeg("Roma-FCO", "Napoli-NAP");
        Arrays.sort(bookIds);
        assertEquals(Arrays.asList(bookIds), bookings);

        String popular = fm.mostPopularLeg();
        assertEquals("Roma-FCO;Napoli-NAP", popular);

    }
}
