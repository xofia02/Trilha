package com.example.trilha;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import com.example.trilha.databinding.ActivityMapsRegistrarBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapsRegistrar extends HelpLocation implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsRegistrarBinding binding;
    private SharedPreferences sharedPreferences;
    private TrilhaDB trilhaDB; // Banco de dados para armazenar trilhas
    private int waypointCounter = 0; // Contador de waypoints
    private Location previousLocation = null;
    private long startTime;

    // Variáveis para calcular distância, velocidade e tempo
    private float totalDistance = 0; // Distância total percorrida
    private TextView speedText, distanceText, timeText; // TextViews para exibir as informações

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsRegistrarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializa o banco de dados
        trilhaDB = new TrilhaDB(this);

        // Apaga os waypoints antigos ao abrir a atividade, ou seja, quando apertamos o botao os waypoints são apagados
        trilhaDB.apagarTrilha();

        // Inicializa as preferências compartilhadas
        sharedPreferences = getSharedPreferences("MapSettings", MODE_PRIVATE);

        // Obtém o fragmento do mapa e inicializa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Inicializa os TextViews
        speedText = findViewById(R.id.speedText);
        distanceText = findViewById(R.id.distanceText);
        timeText = findViewById(R.id.timeText);

        // Define o horário de início
        startTime = System.currentTimeMillis();
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

        // Habilita a funcionalidade de localização se a permissão for concedida
        if (checkLocationPermission()) {
            mMap.setMyLocationEnabled(true); // Exibe a bolinha azul
        }
    }

    @Override
    protected void onLocationUpdated(Location location) {
        if (mMap != null && location != null) {
            // Recupera a configuração do SharedPreferences para "Course Up"
            boolean isCourseUp = sharedPreferences.getBoolean("isCourseUp", false);

            // Obtém a localização atual
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            // Define a câmera com base na configuração "North Up" ou "Course Up"
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new com.google.android.gms.maps.model.CameraPosition.Builder()
                            .target(currentLatLng)
                            .zoom(mMap.getCameraPosition().zoom) // Mantém o zoom
                            .bearing(isCourseUp ? location.getBearing() : 0) // Curso ou Norte
                            .tilt(0) // Sem inclinação
                            .build()
            ));

            // Calcula a distância total percorrida
            if (previousLocation != null) {
                totalDistance += previousLocation.distanceTo(location); // Calcula a distância entre dois pontos
            }
            previousLocation = location;

            // Atualiza a distância na interface
            distanceText.setText("Distância: " + totalDistance + " m");

            // Calcula a velocidade (em km/h)
            float speed = location.getSpeed() * 3.6f; // Converte de m/s para km/h
            speedText.setText("Velocidade: " + String.format("%.2f", speed) + " km/h");

            // Calcula o tempo transcorrido
            long elapsedTime = (System.currentTimeMillis() - startTime) / 1000; // Em segundos
            int minutes = (int) (elapsedTime / 60);
            int seconds = (int) (elapsedTime % 60);
            timeText.setText(String.format("Tempo: %02d:%02d", minutes, seconds));

            // Cria e grava o waypoint no banco de dados, incluindo o timestamp e a velocidade
            long timestamp = System.currentTimeMillis(); // Define o timestamp
            Waypoint waypoint = new Waypoint(location);
            waypoint.setTimestamp(timestamp); // Define o timestamp no waypoint
            waypoint.setVelocity(speed); // Define a velocidade no waypoint
            trilhaDB.registrarWaypoint(waypoint); // Salva o waypoint no banco

            // Incrementa o contador de waypoints
            waypointCounter++;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates(); // Para as atualizações de localização quando a Activity é parada
    }

    // Método auxiliar para verificar permissões
    private boolean checkLocationPermission() {
        return androidx.core.app.ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED;
    }
}
