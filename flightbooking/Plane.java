package it.polito.flightbooking;


//  Represents a plane in the flight booking system.

public class Plane {
    private String id;
    private int capacity;

    public Plane(String id, int capacity) {
        this.id = id;
        this.capacity = capacity;
    }

    // Getters
    public String getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Plane{" +
                "id='" + id + '\'' +
                ", capacity=" + capacity +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Plane plane = (Plane) obj;
        return id.equals(plane.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}