package com.example.trilha;

import androidx.fragment.app.FragmentActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.trilha.databinding.ActivityMapsConfigurarBinding;

public class MapsConfigurar extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsConfigurarBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsConfigurarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("MapSettings", MODE_PRIVATE);

        // Solicita permissão de localização
        PermissionHelper.requestLocationPermission(this);

        // Obtém o SupportMapFragment e é notificado quando o mapa estiver pronto para ser usado
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Obter as configurações salvas
        int mapType = sharedPreferences.getInt("mapType", GoogleMap.MAP_TYPE_NORMAL);
        boolean isCourseUp = sharedPreferences.getBoolean("isCourseUp", false);

        // Aplicar configurações de mapa
        mMap.setMapType(mapType);

        UiSettings mapUI = mMap.getUiSettings();
        if (isCourseUp) {
            mapUI.setAllGesturesEnabled(true);
        } else {
            mapUI.setAllGesturesEnabled(false);
        }

        // Adiciona um marcador e move a câmera para Sydney como exemplo
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    // Método para alternar o tipo de mapa (vetorial/satélite)
    public void onMapTypeSwitchClick(View view) {
        boolean isSatellite = ((Switch) view).isChecked();
        int mapType = isSatellite ? GoogleMap.MAP_TYPE_SATELLITE : GoogleMap.MAP_TYPE_NORMAL;

        // Salva a configuração do tipo de mapa
        sharedPreferences.edit().putInt("mapType", mapType).apply();

        if (mMap != null) {
            mMap.setMapType(mapType);
        }
    }

    // Método para alternar o modo de navegação (North Up/Course Up)
    public void onNavigationModeSwitchClick(View view) {
        boolean isCourseUp = ((Switch) view).isChecked();

        // Salva a configuração do modo de navegação
        sharedPreferences.edit().putBoolean("isCourseUp", isCourseUp).apply();

        if (mMap != null) {
            UiSettings mapUI = mMap.getUiSettings();
            if (isCourseUp) {
                mapUI.setAllGesturesEnabled(true);
            } else {
                mapUI.setAllGesturesEnabled(false);
            }
        }
    }
}
