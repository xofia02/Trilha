package com.example.trilha;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public abstract class HelpLocation extends AppCompatActivity {

    private static final int REQUEST_LOCATION_UPDATES = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private SharedPreferences locationPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializa SharedPreferences específicas para localização
        locationPreferences = getSharedPreferences("LocationSettings", MODE_PRIVATE);

        // Define configurações padrão (se não existirem)
        if (!locationPreferences.contains("updateInterval")) {
            locationPreferences.edit().putInt("updateInterval", 5000).apply(); // Intervalo padrão: 5 segundos
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            onStartLocationUpdates();
        } else {
            Toast.makeText(this, "Permissão de localização não concedida", Toast.LENGTH_SHORT).show();
        }
    }

    private void onStartLocationUpdates() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtém o intervalo de atualização das preferências
        int updateInterval = locationPreferences.getInt("updateInterval", 5000);
        mLocationRequest = new LocationRequest.Builder(updateInterval).build();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    onLocationUpdated(locationResult.getLastLocation());
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    protected abstract void onLocationUpdated(Location location);

    protected void stopLocationUpdates() {
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }
}
