# Flight-Booking-system
Write a program to support flight booking with multiple legs towards a single destination
# Flight Booking

The classes must be placed in the package and the main class will be `FlightManager`. The class `TestApp` in the `example` package contains use cases and test cases.
Exceptions thrown by the methods listed below must be of type `FlightException`.

## R1: Route Definition

Each airport is registered via the method:

```java
addAirport(String airportName, String city, double latitude, double longitude)
```

This method adds an airport identified by its name, associated with the city and its GPS coordinates (latitude and longitude in decimal degrees). The combination of city and airport name, separated by a dash, is considered the unique name of the airport, for example, "Torino-Caselle".
If an already existing airport is added again (that is, an airport with an existing unique name already associated to previously a defined airport), a `FlightException` is thrown.
Example:

```java
fm.addAirport("Caselle", "Torino", 45.2008, 7.6497);
```

The method `listAirports()` returns a collection with the unique names of the defined airports.

The method `defineRoute(String... connections)` of `FlightManager` allows defining a flight route from a departure airport to a destination, with optional intermediate stopovers.
A route consists of legs connecting two consecutive airports. The method returns the number of legs. In case of an insufficient number of connections, or the same connection repeated multiple times (e.g., Torino-Caselle, Torino-Caselle), it must throw a `FlightException`.

Example:

```java
fm.defineRoute("Torino-Caselle", "Francoforte-Francoforte", "Istanbul-Instanbul", "Tokyo-Haneda");
```

creates a route composed of three legs: Torino → Frankfurt, Frankfurt → Istanbul, Istanbul → Tokyo.

## R2: Adding Planes

The method `addPlane(String planeId, int seats)` adds a plane identified by a unique ID, with a maximum number of available seats. In case of a duplicate ID or non-positive capacity, it throws a `FlightException`.

The method `getSeats()` returns a map with the plane IDs as keys and the number of seats as values.

The method `assignPlaneToLeg(String from, String to, String planeId)` assigns a plane to a specific leg. The `from` and `to` parameters must be the full names of the airports previously registered with `addAirport(...)`. The plane must exist. A leg can have only one assigned plane. The method returns the number of seats of the assigned plane.
If the leg does not exist, the plane is not defined, or the leg is already assigned, a `FlightException` is thrown.

## R3: Searching and Booking Seats

The method `findAvailableSeats(String from, String to)` returns a map that associates, for each leg between the departure and arrival airports, the list of available seats.
The legs are represented in the format `"from;to"`, e.g., `"Torino-Caselle;Francoforte-Francoforte"`.
It throws an exception if the leg does not exist.

Each seat number is a positive integer between 1 and the **capacity of the plane assigned to that leg**. A seat is considered available for the entire journey only if it is **available on all legs** between departure and destination. The resulting collection must contain, for each leg in the route, the list of available seats. The seats that are actually bookable for the journey are those **common to all legs of the route**.

The method `bookSeat(String passengerId, String from, String to, int seatNumber)` creates a booking associated with the journey, seat, and passenger and allows a passenger (identified by a unique ID) to book a seat from a departure airport to an arrival airport.
The seat must be available on **all** intermediate legs. The method returns a unique booking code `bookingId`. Otherwise, a `FlightException` is thrown.

The booking code is a sequence of 6 random alphanumeric characters (uppercase letters and digits).
Hint: you can use `int java.util.Random.nextInt(int bound)` to generate a random integer number between `0` and `bound` included.

## R4: Information and Statistics

Some methods are defined to calculate booking statistics:

* `listBookingsForLeg(String from, String to)` returns a list of all valid booking codes for the specified `FlightLeg`, sorted alphabetically.

* `occupationRate(String from, String to)` returns the occupation rate (float between 0.0 and 1.0) of the specified leg, calculated as `booked seats / total seats`.

* `mostPopularLeg()` returns the leg with the highest number of bookings (based on the number of unique bookings) in the same format as described in R3. If multiple legs share the highest number of unique bookings, the method returns the first in alphabetic order.

---

