package com.example.trilha;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapsConfigurar extends HelpLocation implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SharedPreferences mapPreferences;
    private Switch mapTypeSwitch, navigationModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_configurar);

        // Inicializa SharedPreferences específicas para mapas
        mapPreferences = getSharedPreferences("MapSettings", MODE_PRIVATE);

        mapTypeSwitch = findViewById(R.id.switchMapType);
        navigationModeSwitch = findViewById(R.id.switchNavigationMode);

        // Restaura estados dos switches
        mapTypeSwitch.setChecked(mapPreferences.getInt("mapType", GoogleMap.MAP_TYPE_NORMAL) == GoogleMap.MAP_TYPE_SATELLITE);
        navigationModeSwitch.setChecked(mapPreferences.getBoolean("isCourseUp", false));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onLocationUpdated(Location location) {
        if (mMap != null && location != null) {
            boolean isCourseUp = mapPreferences.getBoolean("isCourseUp", false);
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new com.google.android.gms.maps.model.CameraPosition.Builder()
                            .target(currentLatLng)
                            .zoom(mMap.getCameraPosition().zoom) // Mantém o zoom ajustado
                            .bearing(isCourseUp ? location.getBearing() : 0) // North Up ou Course Up
                            .tilt(0) // Sem inclinação
                            .build()
            ));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(mapPreferences.getInt("mapType", GoogleMap.MAP_TYPE_NORMAL));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Verifica e habilita a localização se a permissão foi concedida
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    // Métodos para salvar as configurações dos switches
    public void onMapTypeSwitchClick(View view) {
        boolean isSatellite = ((Switch) view).isChecked();
        int mapType = isSatellite ? GoogleMap.MAP_TYPE_SATELLITE : GoogleMap.MAP_TYPE_NORMAL;
        mapPreferences.edit().putInt("mapType", mapType).apply();
        if (mMap != null) {
            mMap.setMapType(mapType);
        }
    }

    public void onNavigationModeSwitchClick(View view) {
        mapPreferences.edit().putBoolean("isCourseUp", ((Switch) view).isChecked()).apply();
    }
}
