package com.example.trilha;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;

public class Waypoint {
    private long id;
    private double latitude;
    private double longitude;
    private double altitude;
    private long timestamp; // Hora em milissegundos
    private float velocity; // Velocidade em metros por segundo

    // Construtor padrão
    public Waypoint(double latitude, double longitude, double altitude, long timestamp, float velocity) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = timestamp;
        this.velocity = velocity;
    }

    // Construtor baseado em um objeto Location
    public Waypoint(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.altitude = location.getAltitude();
        this.timestamp = location.getTime(); // Timestamp em milissegundos
        this.velocity = location.hasSpeed() ? location.getSpeed() : 0; // Verifica se a velocidade está disponível
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    // Converte os valores de latitude e longitude armazenados no objeto Waypoint em um objeto LatLng do Google Maps
    public LatLng toLatLng() {
        return new LatLng(this.getLatitude(), this.getLongitude());
    }

    // Método para calcular a distância entre dois pontos em metros
    public static double calcularDistancia(Waypoint ponto1, Waypoint ponto2) {
        // Raio da Terra em metros, utilizado na fórmula de Haversine
        double R = 6371000;
        // Converte as coordenadas de latitude e longitude de ponto1 de graus para radianos
        double lat1 = Math.toRadians(ponto1.getLatitude());
        double lon1 = Math.toRadians(ponto1.getLongitude());
        // Converte as coordenadas de latitude e longitude de ponto2 de graus para radianos
        double lat2 = Math.toRadians(ponto2.getLatitude());
        double lon2 = Math.toRadians(ponto2.getLongitude());

        // Calcula a diferença entre as latitudes e longitudes dos dois pontos
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        // Aplica a fórmula de Haversine para calcular a distância
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        // Calcula o ângulo central (c) utilizando a função atan2
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Retorna a distância em metros (multiplicada pelo raio da Terra)
        return R * c; // Resultado em metros
    }

    // Método para calcular a velocidade média entre dois pontos (em metros por segundo)
    public static float calcularVelocidade(Waypoint ponto1, Waypoint ponto2) {
        double distancia = calcularDistancia(ponto1, ponto2); // Distância em metros
        long tempo = ponto2.getTimestamp() - ponto1.getTimestamp(); // Tempo em milissegundos

        // Retorna a velocidade em metros por segundo
        return tempo > 0 ? (float) (distancia / (tempo / 1000.0)) : 0;
    }
}
