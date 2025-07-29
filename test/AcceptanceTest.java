package it.polito.oop.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import it.polito.flightbooking.FlightException;
import it.polito.flightbooking.FlightManager;

public class AcceptanceTest {

    private FlightManager fm;

    @Before
    public void setUp() {
        fm = new FlightManager();
    }

    /* R1: airports & flight legs */

    @Test public void testR1_AddAirport() throws FlightException {
        fm.addAirport("Caselle", "Torino", 45.2, 7.65);

        Collection<String> airports = fm.listAirports();
        assertNotNull("Missing airports", airports);
        assertEquals(1, airports.size());
        assertTrue(airports.contains("Torino-Caselle"));

    }

    @Test public void testR1_DuplicateAirport() throws FlightException {
        fm.addAirport("Caselle", "Torino", 45.2, 7.65);

        assertThrows("Expected FlightException when airport names are duplicated", FlightException.class,
        ()->fm.addAirport("Caselle", "Torino", 1.1, 1.1));
    }

    @Test public void testR1_DefineRouteValid() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.addAirport("C", "C", 2, 2);
        assertEquals(2, fm.defineRoute("A-A", "B-B", "C-C"));
    }

    @Test public void testR1_DefineRouteInvalidAirport() {
        assertThrows("Expected FlightException when airport is undefined", FlightException.class,
                     ()->fm.defineRoute("X-X", "C-C"));
    }

    @Test public void testR1_DefineRouteInsufficientConnections() throws FlightException {
        fm.addAirport("A", "A", 10, 20);
        assertThrows("Expected FlightException when a route has insufficient connections", FlightException.class,
                     ()->fm.defineRoute("A-A"));
    }

    @Test public void testR1_RepeatedConnections() throws FlightException {
        fm.addAirport("A", "A", 10, 20);
        assertThrows("Expected FlightException when a route has repeated connections", FlightException.class,
                     ()->fm.defineRoute("A-A", "A-A"));
    }

    @Test
    public void testR1_AddMultipleAirports() throws FlightException {
        fm.addAirport("Linate", "Milano", 45.45, 9.28);
        fm.addAirport("Malpensa", "Milano", 45.63, 8.72);
        fm.addAirport("Orio", "Bergamo", 45.67, 9.70);
        fm.listAirports();
        assertNotNull(fm.listAirports());
        assertEquals(3, fm.listAirports().size());
    }

    @Test
    public void testR1_DefineRouteExactEndMatch() throws FlightException {
        fm.addAirport("MXP", "Milano", 45.63, 8.72);
        fm.addAirport("FCO", "Roma", 41.8, 12.25);
        fm.addAirport("NAP", "Napoli", 40.85, 14.29);
        int legs = fm.defineRoute("Milano-MXP", "Roma-FCO", "Napoli-NAP");
        assertEquals(2, legs);
    }

    @Test
    public void testR1_DefineRouteWithOnlyTwoStops() throws FlightException {
        fm.addAirport("TRN", "Torino", 45.07, 7.66);
        fm.addAirport("LIN", "Milano", 45.45, 9.28);
        int legs = fm.defineRoute("Torino-TRN", "Milano-LIN");
        assertEquals(1, legs);
    }

    @Test
    public void testR1_DefineRouteWithIntermediateStops() throws FlightException {
        fm.addAirport("Caselle", "Torino", 45.07, 7.66);
        fm.addAirport("LIN", "Milano", 45.45, 9.28);
        fm.addAirport("BLQ", "Bologna", 44.5, 11.3);
        fm.addAirport("FCO", "Roma", 41.8, 12.25);
        int legs = fm.defineRoute("Torino-Caselle", "Milano-LIN", "Bologna-BLQ", "Roma-FCO");
        assertEquals(3, legs);
    }

    @Test
    public void testR1_DefineRouteWithSharedLegs() throws FlightException {
        fm.addAirport("TRN", "Torino", 45.07, 7.66);
        fm.addAirport("LIN", "Milano", 45.45, 9.28);
        fm.addAirport("BLQ", "Bologna", 44.5, 11.3);
        fm.addAirport("FCO", "Roma", 41.8, 12.25);
        int legs1 = fm.defineRoute("Torino-TRN", "Roma-FCO", "Bologna-BLQ");
        int legs2 = fm.defineRoute("Milano-LIN", "Roma-FCO", "Bologna-BLQ");
        int legs3 = fm.defineRoute("Roma-FCO", "Bologna-BLQ","Milano-LIN","Torino-TRN");
        assertEquals(2, legs1);
        assertEquals(2, legs2);
        assertEquals(3, legs3);
    }


    /* R2: airplanes */

    @Test public void testR2_AddPlaneValid() throws FlightException {
        fm.addPlane("PLA1", 100);
        fm.addPlane("PLA2", 120);

        Map<String,Integer> seats = fm.getSeats();
        assertNotNull("Missing seats", seats);
        assertEquals("Wrong number of planes", 2, seats.size());
        assertNotNull("Missing plane PLA2" , seats.get("PLA2"));
        assertEquals("Wrong number of seats on plane PLA1" , 120, seats.get("PLA2").intValue());
    }

    @Test public void testR2_AddPlaneInvalidCapacity() {
        assertThrows("Expected FlightException when plane capacity is not valid", FlightException.class,
                     ()->fm.addPlane("P2", 0));
    }

    @Test public void testR2_AssignPlaneToLeg() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.defineRoute("A-A", "B-B");
        fm.addPlane("P", 50);
        
        int seats = fm.assignPlaneToLeg("A-A", "B-B", "P");
        assertEquals("Wrong number of seats", 50 , seats);
    }

    @Test public void testR2_AssignPlaneTwice() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.defineRoute("A-A", "B-B");
        fm.addPlane("P", 50);
        fm.assignPlaneToLeg("A-A", "B-B", "P");

        assertThrows("Expected FlightException when a plane was already assigned", FlightException.class,
                     ()->fm.assignPlaneToLeg("A-A", "B-B", "P"));
    }

    @Test
    public void testR2_AddPlaneWithSameIdFails() throws FlightException {
        fm.addPlane("P1", 100);
        assertThrows("Expected FlightException when the plane ID is duplicated", FlightException.class,
                     ()->fm.addPlane("P1", 150));
    }

    @Test
    public void testR2_AssignPlaneToNonexistentLeg() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.addPlane("P1", 100);

        assertThrows("Expected FlightException when a plane is assigned to an undefined leg", FlightException.class,
                     ()->fm.assignPlaneToLeg("A-A", "B-B", "P1"));
    }

    @Test
    public void testR2_AssignPlaneToTwoDifferentLegs() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.addAirport("C", "C", 2, 2);
        fm.defineRoute("A-A", "B-B", "C-C");
        fm.addPlane("P1", 50);
        
        int seats1 = fm.assignPlaneToLeg("A-A", "B-B", "P1");
        int seats2 = fm.assignPlaneToLeg("B-B", "C-C", "P1"); 

        assertEquals("Wrong number of seats", 50 , seats1);
        assertEquals("Wrong number of seats", 50 , seats2);
    }

    @Test
    public void testR2_AssignPlane_NonexistentPlane() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.defineRoute("A-A", "B-B");
        assertThrows("Expected FlightException when an undefined plane is assigned to a leg", FlightException.class,
                     ()->fm.assignPlaneToLeg("A-A", "B-B", "XXX"));
    }

    /* R3: Booking & availability */

    @Test
    public void testR3_findAvailableSeats() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.defineRoute("A-A", "B-B");
        fm.addPlane("P1", 5);
        fm.assignPlaneToLeg("A-A", "B-B", "P1");
        Map<String, List<Integer>> seats = fm.findAvailableSeats("A-A", "B-B");
        assertNotNull(seats);
        List<Integer> values = seats.values().iterator().next();
        assertNotNull(values);
        assertEquals(5, values.size());
        assertTrue(values.contains(1));
        assertTrue(values.contains(5));
    }

    @Test
    public void testR3_FindAvailableSeatsExactIntersection() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.addAirport("C", "C", 2, 2);
        fm.addAirport("D", "D", 30, 30);
        fm.defineRoute("A-A", "B-B", "C-C", "D-D");
        fm.addPlane("P1", 10);
        fm.addPlane("P2", 4);
        fm.addPlane("P3", 8);
        fm.assignPlaneToLeg("A-A", "B-B", "P1");
        fm.assignPlaneToLeg("B-B", "C-C", "P2");
        fm.assignPlaneToLeg("C-C", "D-D", "P3");
        Map<String, List<Integer>> available = fm.findAvailableSeats("A-A", "D-D");
        assertNotNull(available);
        for(List<Integer> list : available.values()) {
            assertTrue(list.contains(1));
            assertTrue(list.contains(4));
            assertFalse(list.contains(5));
            assertFalse(list.contains(8));
            assertFalse(list.contains(10));
        }
    }

    @Test
    public void testR2_FindAvailableSeatsInvalid() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.defineRoute("A-A", "B-B");
        fm.addPlane("P1", 5);
        fm.assignPlaneToLeg("A-A", "B-B", "P1");
        assertThrows("Expected FlightException when a leg is undefined", FlightException.class,
                     ()->fm.findAvailableSeats("A-A", "C-C"));
    }

    @Test 
    public void testR3_BookSeatValid() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.defineRoute("A-A", "B-B");
        fm.addPlane("P", 10);
        fm.assignPlaneToLeg("A-A", "B-B", "P");
        String id1 = fm.bookSeat("U1", "A-A", "B-B", 1);
        assertNotNull(id1);
        assertEquals(6, id1.length());
        String id2 = fm.bookSeat("U2", "A-A", "B-B", 2);
        assertNotNull(id2);
        assertEquals(6, id2.length());
        assertNotEquals(id1, id2);
    }

    @Test 
    public void testR3_BookSeatUnique() throws FlightException {
        int numBookings = 100;
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.defineRoute("A-A", "B-B");
        fm.addPlane("P", numBookings);
        fm.assignPlaneToLeg("A-A", "B-B", "P");

        Set<String> ids = new HashSet<>();
        for(int i=1; i<=numBookings; ++i){
            assertTrue("duplicate booking id",
                        ids.add( fm.bookSeat("U"+i, "A-A", "B-B", i) ));
        }
    }


    @Test public void testR3_BookSeatAlreadyTaken() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.defineRoute("A-A", "B-B");
        fm.addPlane("P", 1);
        fm.assignPlaneToLeg("A-A", "B-B", "P");
        fm.bookSeat("U1", "A-A", "B-B", 1);
        assertThrows("Expected FlightException when a seat is already taken", FlightException.class,
                     ()->fm.bookSeat("U2", "A-A", "B-B", 1));
    }

    @Test
    public void testR3_BookSeatAcrossMultipleLegs() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.addAirport("C", "C", 2, 2);
        fm.defineRoute("A-A", "B-B", "C-C");
        fm.addPlane("P1", 3);
        fm.assignPlaneToLeg("A-A", "B-B", "P1");
        fm.assignPlaneToLeg("B-B", "C-C", "P1");
        String id = fm.bookSeat("U123", "A-A", "C-C", 2);
        assertNotNull(id);
        assertEquals(6, id.length());
    }

    @Test
    public void testR3_FindAvailableSeatsAfterBooking() throws FlightException {
        fm.addAirport("X", "X", 0, 0);
        fm.addAirport("Y", "Y", 1, 1);
        fm.defineRoute("X-X", "Y-Y");
        fm.addPlane("P1", 2);
        fm.assignPlaneToLeg("X-X", "Y-Y", "P1");
        fm.bookSeat("U1", "X-X", "Y-Y", 1);
        Map<String, List<Integer>> seats = fm.findAvailableSeats("X-X", "Y-Y");
        assertNotNull(seats);
        List<Integer> available = seats.values().iterator().next();
        assertFalse(available.contains(1));
        assertTrue(available.contains(2));
    }

    @Test
    public void testR3_BookSeatOutsideDefinedPathFails() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.defineRoute("A-A", "B-B");
        fm.addPlane("P", 1);
        fm.assignPlaneToLeg("A-A", "B-B", "P");
        assertThrows("Expected FlightException when booking a seat on an undefined leg", FlightException.class,
                     ()->fm.bookSeat("U1", "B-B", "A-A", 1));
    }

    @Test
    public void testR3_FindAvailableSeatsOnUnassignedPlaneFails() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.defineRoute("A-A", "B-B");
        assertThrows("Expected FlightException when a leg is not assigned to a plane", FlightException.class,
                     ()->fm.findAvailableSeats("A-A", "B-B"));
    }

    @Test
    public void testR3_FindAvailableSeatsTwoLegs() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.addAirport("C", "C", 2, 2);
        fm.defineRoute("A-A", "B-B", "C-C");
        fm.addPlane("P1", 10);
        fm.addPlane("P2", 3);
        fm.assignPlaneToLeg("A-A", "B-B", "P1");
        fm.assignPlaneToLeg("B-B", "C-C", "P2");
        fm.bookSeat("U1", "A-A", "C-C", 2);
        Map<String, List<Integer>> available = fm.findAvailableSeats("A-A", "C-C");
        assertNotNull(available);
        for (List<Integer> list : available.values()) {
            assertFalse(list.contains(2));
        }
    }
    
    /* R4: Stats */

    @Test public void testR4_OccupationRateZero() throws FlightException {
        fm.addAirport("X", "X", 0, 0);
        fm.addAirport("Y", "Y", 1, 1);
        fm.defineRoute("X-X", "Y-Y");
        fm.addPlane("P", 2);
        fm.assignPlaneToLeg("X-X", "Y-Y", "P");
        assertEquals(0.0, fm.occupationRate("X-X", "Y-Y"), 0.0001);
    }

    @Test public void testR4_ListBookingsEmpty() throws FlightException {
        fm.addAirport("X", "X", 0, 0);
        fm.addAirport("Y", "Y", 1, 1);
        fm.defineRoute("X-X", "Y-Y");
        fm.addPlane("P", 3);
        fm.assignPlaneToLeg("X-X", "Y-Y", "P");
        List<String> bookings = fm.listBookingsForLeg("X-X", "Y-Y");
        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    public void testR4_OccupationRatePartial() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.defineRoute("A-A", "B-B");
        fm.addPlane("P", 4);
        fm.assignPlaneToLeg("A-A", "B-B", "P");
        fm.bookSeat("U1", "A-A", "B-B", 1);
        double rate = fm.occupationRate("A-A", "B-B");
        assertEquals(0.25, rate, 0.001);
    }

    @Test
    public void testR4_ListBookingsForLegSorted() throws FlightException {
        fm.addAirport("X", "X", 0, 0);
        fm.addAirport("Y", "Y", 1, 1);
        fm.defineRoute( "X-X", "Y-Y");
        fm.addPlane("P", 3);
        fm.assignPlaneToLeg("X-X", "Y-Y", "P");
        String[] codes = new String[3];
        codes[0] = fm.bookSeat("U3", "X-X", "Y-Y", 1);
        codes[1] = fm.bookSeat("U1", "X-X", "Y-Y", 2);
        codes[2] = fm.bookSeat("U2", "X-X", "Y-Y", 3);
        List<String> bookings = fm.listBookingsForLeg("X-X", "Y-Y");
        assertNotNull(bookings);
        Arrays.sort(codes);
        assertEquals(Arrays.asList(codes), bookings);
    }

    @Test
    public void testR4_ListBookingsOnLegSubset() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.addAirport("C", "C", 2, 2);
        fm.defineRoute("A-A", "B-B", "C-C");
        fm.defineRoute("A-A", "B-B");
        fm.addPlane("P", 5);
        fm.assignPlaneToLeg("A-A", "B-B", "P");
        fm.assignPlaneToLeg("B-B", "C-C", "P");
        String[] codes = new String[2];
        codes[0] = fm.bookSeat("U1", "A-A", "C-C", 1); 
        codes[1] = fm.bookSeat("U2", "A-A", "B-B", 2);
        List<String> bookings = fm.listBookingsForLeg("A-A", "B-B");
        assertNotNull(bookings);
        Arrays.sort(codes);
        assertEquals(Arrays.asList(codes), bookings);
    }

    @Test
    public void testR4_MostPopularLegSingle() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.defineRoute("A-A", "B-B");
        fm.addPlane("P", 3);
        fm.assignPlaneToLeg("A-A", "B-B", "P");
        fm.bookSeat("U1", "A-A", "B-B", 1);
        String popular = fm.mostPopularLeg();
        assertEquals("A-A;B-B", popular);
    }

    @Test
    public void testR4_MostPopularLegTiebreaker() throws FlightException {
        fm.addAirport("A", "A", 0, 0);
        fm.addAirport("B", "B", 1, 1);
        fm.addAirport("C", "C", 2, 2);
        fm.defineRoute( "A-A", "B-B", "C-C");
        fm.addPlane("P", 3);
        fm.assignPlaneToLeg("A-A", "B-B", "P");
        fm.assignPlaneToLeg("B-B", "C-C", "P");
        fm.bookSeat("U1", "A-A", "C-C", 1);  
        String popular = fm.mostPopularLeg();
        assertNotNull(popular);
        assertTrue(popular.equals("A-A;B-B") || popular.equals("B-B;C-C"));
    }
}
