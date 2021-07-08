package com.example.rentahome;

public class LocationHelper {

    private double Longtitude;
    private double Latitude;

    public LocationHelper(double longtitude, double latitude){
        Longtitude=longtitude;
        Latitude=latitude;
    }

    public double getLongtitude() {
        return Longtitude;
    }

    public void setLongtitude(double longtitude) {
        Longtitude = longtitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }
}
