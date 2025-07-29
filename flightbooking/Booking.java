package it.polito.flightbooking;

import java.util.Objects;


 // represents a booking in the flight booking system.

public class Booking {
    private String bookingId;
    private String passengerId;
    private String from;
    private String to;
    private int seatNumber;

    public Booking(String bookingId, String passengerId, String from, String to, int seatNumber) {
        this.bookingId = bookingId;
        this.passengerId = passengerId;
        this.from = from;
        this.to = to;
        this.seatNumber = seatNumber;
    }

    // Getters
    public String getBookingId() {
        return bookingId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    // Setters
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", passengerId='" + passengerId + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", seatNumber=" + seatNumber +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Booking booking = (Booking) obj;
        return bookingId.equals(booking.bookingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }
}