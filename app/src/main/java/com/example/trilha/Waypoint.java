package com.example.trilha;

import android.location.Location;

public class Waypoint {
    private long id;
    private double latitude;
    private double longitude;
    private double altitude;

    // Construtor padrão
    public Waypoint(double latitude, double longitude, double altitude) {
        this.id = 0;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.altitude = 0.0;
    }

    // Construtor baseado em um objeto Location
    public Waypoint(Location location) {
        this.id = 0; // ID inicial será gerado automaticamente pelo banco de dados
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.altitude = location.getAltitude();
    }

    // Getters e setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}
