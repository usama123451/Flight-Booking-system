package it.polito.flightbooking;

import java.util.*;

/**
 * The FlightManager class is the main class for the flight booking system.
 * It provides functionality to manage airports, define routes, add planes,
 * assign planes to flight legs, search for available seats, book seats,
 * and retrieve various statistics about bookings and flight occupancy.
 */
public class FlightManager {
    private Map<String, Airport> airports = new HashMap<>();
    private Map<String, Plane> planes = new HashMap<>();
    private Map<String, FlightLeg> legs = new HashMap<>();
    private List<String> routes = new ArrayList<>();
    private Random random = new Random();

    /**
     * Adds an airport to the system.
     * 
     * @param airportName the name of the airport
     * @param city the city where the airport is located
     * @param latitude the latitude coordinate of the airport in decimal degrees
     * @param longitude the longitude coordinate of the airport in decimal degrees
     * @throws FlightException if an airport with the same unique name (city-airportName) already exists
     */
    public void addAirport(String airportName, String city, double latitude, double longitude) throws FlightException {
        // To be implemented
        String uniqueName = city + "-" + airportName;
        if (airports.containsKey(uniqueName)) {
            throw new FlightException("Airport " + uniqueName + " already exists");
        }
        airports.put(uniqueName, new Airport(airportName, city, latitude, longitude));
    }

    /**
     * Returns a collection of all registered airports' unique names.
     * 
     * @return a collection containing the unique names of all defined airports (in the format "city-airportName")
     */
    public Collection<String> listAirports() {
        return new ArrayList<>(airports.keySet());
    }

    /**
     * Defines a flight route from a departure airport to a destination, with optional intermediate stopovers.
     * A route consists of legs connecting two consecutive airports.
     * 
     * @param connections variable number of airport unique names representing the route
     * @return the number of legs in the defined route
     * @throws FlightException if there are fewer than 2 connections, if any airport is undefined,
     *                         or if the same connection is repeated multiple times
     */
    public int defineRoute(String... connections) throws FlightException {
        if (connections.length < 2) {
            throw new FlightException("Route must have at least 2 connections");
        }

        // Check for duplicate connections
        Set<String> connectionSet = new HashSet<>();
        for (String connection : connections) {
            if (connectionSet.contains(connection)) {
                throw new FlightException("Duplicate connection: " + connection);
            }
            connectionSet.add(connection);
        }

        // Check if all airports exist
        for (String connection : connections) {
            if (!airports.containsKey(connection)) {
                throw new FlightException("Airport " + connection + " not found");
            }
        }

        
        int numLegs = connections.length - 1;
        for (int i = 0; i < numLegs; i++) {
            String from = connections[i];
            String to = connections[i + 1];
            String legKey = from + ";" + to;

            if (!legs.containsKey(legKey)) {
                legs.put(legKey, new FlightLeg(from, to));
            }
        }

        return numLegs;

    }

    /**
     * Adds a plane to the system with a unique ID and a specified capacity.
     * 
     * @param planeId the unique identifier for the plane
     * @param capacity the maximum number of available seats on the plane
     * @throws FlightException if the planeId already exists or if the capacity is not positive
     */
    public void addPlane(String planeId, int capacity) throws FlightException {
        
        if (planes.containsKey(planeId)) {
            throw new FlightException("Plane " + planeId + " already exists");
        }
        if (capacity <= 0) {
            throw new FlightException("Capacity must be positive");
        }
        planes.put(planeId, new Plane(planeId, capacity));
    }

    /**
     * Returns a map with the plane IDs as keys and the number of seats as values.
     * 
     * @return a map containing plane IDs and their corresponding seat capacities
     */
    public Map<String, Integer> getSeats() {

        Map<String, Integer> result = new HashMap<>();
        for (Plane plane : planes.values()) {
            result.put(plane.getId(), plane.getCapacity());
        }
        return result;
    }


    /**
     * Assigns a plane to a specific leg of a route.
     * 
     * @param from the full name of the departure airport
     * @param to the full name of the arrival airport
     * @param planeId the unique identifier of the plane to assign
     * @return the number of seats of the assigned plane
     * @throws FlightException if the leg does not exist, the plane is not defined, 
     *                         or the leg already has an assigned plane
     */
    public int assignPlaneToLeg(String from, String to, String planeId) throws FlightException {
        String legKey = from + ";" + to;
        FlightLeg leg = legs.get(legKey);

        if (leg == null) {
            throw new FlightException("Leg " + legKey + " does not exist");
        }

        Plane plane = planes.get(planeId);
        if (plane == null) {
            throw new FlightException("Plane " + planeId + " not found");
        }

        if (leg.getAssignedPlane() != null) {
            throw new FlightException("Leg " + legKey + " already has an assigned plane");
        }

        leg.setAssignedPlane(plane);
        return plane.getCapacity();
    }

    /** 
     * Finds available seats for a journey between two airports.
     * 
     * @param from the full name of the departure airport
     * @param to the full name of the arrival airport
     * @return a map that associates, for each leg between the departure and arrival airports,
     *         the list of available seats. The legs are represented in the format "from;to".
     * @throws FlightException if the route does not exist
     */
    public Map<String, List<Integer>> findAvailableSeats(String from, String to) throws FlightException {
        List<String> routeLegs = findRouteLegs(from, to);
        if (routeLegs.isEmpty()) {
            throw new FlightException("Route from " + from + " to " + to + " does not exist");
        }

        Map<String, List<Integer>> result = new HashMap<>();
        Set<Integer> commonSeats = null;

        // Find seats available on all legs
        for (String legKey : routeLegs) {
            FlightLeg leg = legs.get(legKey);
            if (leg.getAssignedPlane() == null) {
                throw new FlightException("Leg " + legKey + " has no assigned plane, cannot find available seats.");
                
            }

            List<Integer> availableSeats = leg.getAvailableSeats();

            if (commonSeats == null) {
                commonSeats = new HashSet<>(availableSeats);
            } else {
                commonSeats.retainAll(availableSeats);
            }
        }

        // Convert common seats to list
        List<Integer> commonSeatsList = new ArrayList<>(commonSeats != null ? commonSeats : new HashSet<>());
        Collections.sort(commonSeatsList);

        // All legs should show the same available seats (only those available on all legs)
        for (String legKey : routeLegs) {
            result.put(legKey, new ArrayList<>(commonSeatsList));
        }

        return result;
    }

    /**
     * Books a seat for a passenger on a journey between two airports.
     * 
     * @param passengerId the unique identifier of the passenger
     * @param from the full name of the departure airport
     * @param to the full name of the arrival airport
     * @param seatNumber the seat number to book
     * @return a unique booking code (6 random alphanumeric characters)
     * @throws FlightException if the route does not exist or the seat is not available on all legs
     */
    public String bookSeat(String passengerId, String from, String to, int seatNumber) throws FlightException {
        List<String> routeLegs = findRouteLegs(from, to);
        if (routeLegs.isEmpty()) {
            throw new FlightException("Route from " + from + " to " + to + " does not exist");
        }

        // Check if seat is available on all legs
        for (String legKey : routeLegs) {
            FlightLeg leg = legs.get(legKey);
            if (leg.getAssignedPlane() == null) {
                throw new FlightException("No plane assigned to leg " + legKey);
            }
            if (seatNumber < 1 || seatNumber > leg.getAssignedPlane().getCapacity()) {
                throw new FlightException("Seat " + seatNumber + " does not exist on leg " + legKey);
            }
            if (!leg.isSeatAvailable(seatNumber)) {
                throw new FlightException("Seat " + seatNumber + " is not available on leg " + legKey);
            }
        }

        // Generate booking code
        String bookingCode = generateBookingCode();

        // Book the seat on all legs
        for (String legKey : routeLegs) {
            FlightLeg leg = legs.get(legKey);
            leg.bookSeat(seatNumber, bookingCode);
        }

        return bookingCode;
    }

    /**
     * Lists all valid booking codes for a specific flight leg.
     * 
     * @param from the full name of the departure airport
     * @param to the full name of the arrival airport
     * @return a list of all valid booking codes for the specified leg, sorted alphabetically
     */
    public List<String> listBookingsForLeg(String from, String to) {

        String legKey = from + ";" + to;
        FlightLeg leg = legs.get(legKey);

        if (leg == null) {
            return new ArrayList<>();
        }

        List<String> bookings = new ArrayList<>(leg.getBookingCodes());
        Collections.sort(bookings);
        return bookings;
    }

    /**
     * Calculates the occupation rate of a specific flight leg.
     * 
     * @param from the full name of the departure airport
     * @param to the full name of the arrival airport
     * @return the occupation rate (a float between 0.0 and 1.0) calculated as booked seats / total seats
     */
    public double occupationRate(String from, String to) {
        String legKey = from + ";" + to;
        FlightLeg leg = legs.get(legKey);

        if (leg == null || leg.getAssignedPlane() == null) {
            return 0.0;
        }

        return leg.getOccupationRate();
    }

    /**
     * Finds the leg with the highest number of bookings.
     * 
     * @return the leg with the highest number of unique bookings in the format "from;to".
     *         If multiple legs share the highest number of unique bookings, returns the first in alphabetic order.
     */
    public String mostPopularLeg() {
        String mostPopular = null;
        int maxBookings = 0;

        List<String> sortedLegKeys = new ArrayList<>(legs.keySet());
        Collections.sort(sortedLegKeys);

        for (String legKey : sortedLegKeys) {
            FlightLeg leg = legs.get(legKey);
            int bookings = leg.getBookingCodes().size();

            if (bookings > maxBookings) {
                maxBookings = bookings;
                mostPopular = legKey;
            }
        }

        return mostPopular;
    }
    // Helper methods
    private List<String> findRouteLegs(String from, String to) {
        List<String> result = new ArrayList<>();

        // Try to find a direct route or route through intermediate stops
        for (String legKey : legs.keySet()) {
            String[] parts = legKey.split(";");
            if (parts[0].equals(from)) {
                if (parts[1].equals(to)) {
                    // Direct route
                    result.add(legKey);
                    return result;
                } else {
                    // Check if there's a continuation
                    List<String> continuation = findRouteLegs(parts[1], to);
                    if (!continuation.isEmpty()) {
                        result.add(legKey);
                        result.addAll(continuation);
                        return result;
                    }
                }
            }
        }

        return result;
    }

    private String generateBookingCode() {
        StringBuilder code = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }

        return code.toString();
    }

}