package it.polito.flightbooking;

import java.util.*;


 //represents a flight leg between two airports.
public class FlightLeg {
    private String from;
    private String to;
    private Plane assignedPlane;
    private Set<Integer> bookedSeats;
    private List<String> bookingCodes;

    public FlightLeg(String from, String to) {
        this.from = from;
        this.to = to;
        this.bookedSeats = new HashSet<>();
        this.bookingCodes = new ArrayList<>();
    }


     // returns the key representation of this leg in format "from;to"

    public String getKey() {
        return from + ";" + to;
    }


    public void bookSeat(int seatNumber, String bookingCode) {
        bookedSeats.add(seatNumber);
        bookingCodes.add(bookingCode);
    }


    //  checking if a seat is available on this leg

    public boolean isSeatAvailable(int seatNumber) {
        if (assignedPlane == null) {
            return false;
        }
        return seatNumber >= 1 && seatNumber <= assignedPlane.getCapacity() &&
                !bookedSeats.contains(seatNumber);
    }


    public List<Integer> getAvailableSeats() {
        List<Integer> availableSeats = new ArrayList<>();
        if (assignedPlane != null) {
            for (int seat = 1; seat <= assignedPlane.getCapacity(); seat++) {
                if (!bookedSeats.contains(seat)) {
                    availableSeats.add(seat);
                }
            }
        }
        return availableSeats;
    }


    public double getOccupationRate() {
        if (assignedPlane == null) {
            return 0.0;
        }
        return (double) bookedSeats.size() / assignedPlane.getCapacity();
    }

    
    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Plane getAssignedPlane() {
        return assignedPlane;
    }

    public Set<Integer> getBookedSeats() {
        return new HashSet<>(bookedSeats);
    }

    public List<String> getBookingCodes() {
        return new ArrayList<>(bookingCodes);
    }

    
    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setAssignedPlane(Plane assignedPlane) {
        this.assignedPlane = assignedPlane;
    }

    @Override
    public String toString() {
        return getKey();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FlightLeg flightLeg = (FlightLeg) obj;
        return from.equals(flightLeg.from) && to.equals(flightLeg.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}