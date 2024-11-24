package com.example.trilha;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsRegistrarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializa as preferências compartilhadas
        sharedPreferences = getSharedPreferences("MapSettings", MODE_PRIVATE);

        // Inicializa o banco de dados
        trilhaDB = new TrilhaDB(this);

        // Apaga trilha anterior (se necessário)
        trilhaDB.apagarTrilha();

        // Obtém o fragmento do mapa e inicializa
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
                            .zoom(mMap.getCameraPosition().zoom) // Mantém o zoom atual
                            .bearing(isCourseUp ? location.getBearing() : 0) // Curso ou norte
                            .tilt(0) // Sem inclinação
                            .build()
            ));

            // Grava o waypoint no banco de dados
            Waypoint waypoint = new Waypoint(location);
            trilhaDB.registrarWaypoint(waypoint);

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
