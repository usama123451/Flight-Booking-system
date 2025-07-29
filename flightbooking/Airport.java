package it.polito.flightbooking;

//represents an airport in the flight booking system.
public class Airport {
    private String name;
    private String city;
    private double latitude;
    private double longitude;

    public Airport(String name, String city, double latitude, double longitude) {
        this.name = name;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * returns the unique name of the airport in format "city-airportName"
     */
    public String getUniqueName() {
        return city + "-" + name;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    // Setters (if needed)
    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return getUniqueName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Airport airport = (Airport) obj;
        return getUniqueName().equals(airport.getUniqueName());
    }

    @Override
    public int hashCode() {
        return getUniqueName().hashCode();
    }
}