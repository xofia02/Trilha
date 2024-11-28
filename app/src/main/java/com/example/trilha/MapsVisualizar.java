package com.example.trilha;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MapsVisualizar extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TrilhaDB trilhaDB; // Banco de dados
    private TextView distanceText, speedText, startTimeText, durationText;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_visualizar);

        // Inicializa banco de dados
        trilhaDB = new TrilhaDB(this);

        // Inicializa as preferências compartilhadas
        sharedPreferences = getSharedPreferences("MapSettings", MODE_PRIVATE);

        // Inicializa os TextViews
        distanceText = findViewById(R.id.distanceText);
        speedText = findViewById(R.id.speedText);
        startTimeText = findViewById(R.id.startTimeText);
        durationText = findViewById(R.id.durationText);

        // Configura o mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    // Recupera as configurações do SharedPreferences
        int mapType = sharedPreferences.getInt("mapType", GoogleMap.MAP_TYPE_NORMAL);
        boolean isCourseUp = sharedPreferences.getBoolean("isCourseUp", false);

        // Aplica o tipo de mapa configurado
        mMap.setMapType(mapType);

        // Habilita os controles de zoom no mapa
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Carregar waypoints do banco de dados
        List<Waypoint> waypoints = trilhaDB.getAllWaypoints();

        if (waypoints != null && !waypoints.isEmpty()) {
            Log.d("MapsVisualizar", "Waypoints encontrados: " + waypoints.size());

            // Configuração do Polyline
            PolylineOptions polylineOptions = new PolylineOptions()
                    .color(android.graphics.Color.RED)  // Cor da linha para visibilidade
                    .width(12);  // Aumentando a largura da linha
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            LatLng previousLatLng = null;
            float totalDistance = 0;

            // Adiciona os pontos da trilha ao Polyline e calcula a distância
            for (Waypoint waypoint : waypoints) {
                LatLng currentLatLng = new LatLng(waypoint.getLatitude(), waypoint.getLongitude());
                polylineOptions.add(currentLatLng);
                boundsBuilder.include(currentLatLng);

                if (previousLatLng != null) {
                    totalDistance += SphericalUtil.computeDistanceBetween(previousLatLng, currentLatLng);
                //SphericalUtil=fornece utilitários para cálculos geométricos sobre a superfície da Terra,
                    // considerada como uma esfera. Ela facilita a realização de operações como distâncias,
                    // ângulos e posições relativas entre pontos geográficos, sem a necessidade de implementar
                    // esses cálculos manualmente.
                }
                previousLatLng = currentLatLng;
            }

            // Adiciona a linha da trilha no mapa
            mMap.addPolyline(polylineOptions);
            Log.d("MapsVisualizar", "Polyline adicionada");

            // Ajusta o zoom do mapa para mostrar toda a trilha
            LatLngBounds bounds = boundsBuilder.build();
            int padding = 100; // Aumentar o padding para garantir que a trilha caiba
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

            // Exibe os dados da trilha
            displayTrailData(waypoints, totalDistance);
        } else {
            Log.d("MapsVisualizar", "Nenhum waypoint encontrado.");
        }
    }

    private void displayTrailData(List<Waypoint> waypoints, float totalDistance) {
        // Data/hora de início
        long startTimestamp = waypoints.get(0).getTimestamp();
        String startTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                .format(startTimestamp);
        startTimeText.setText("Início: " + startTime);

        // Duração da trilha
        long endTimestamp = waypoints.get(waypoints.size() - 1).getTimestamp();
        long durationInSeconds = (endTimestamp - startTimestamp) / 1000;
        String duration = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                durationInSeconds / 3600, (durationInSeconds % 3600) / 60, durationInSeconds % 60);
        durationText.setText("Duração: " + duration);

        // Distância total
        distanceText.setText(String.format(Locale.getDefault(), "Distância: %.2f km", totalDistance / 1000));

        // Velocidade média
        float averageSpeed = (totalDistance / durationInSeconds) * 3.6f; // m/s para km/h
        speedText.setText(String.format(Locale.getDefault(), "Velocidade média: %.2f km/h", averageSpeed));
    }
}
