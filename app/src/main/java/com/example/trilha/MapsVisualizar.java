package com.example.trilha;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.example.trilha.databinding.ActivityMapsVisualizarBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsVisualizar extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsVisualizarBinding binding;
    private TrilhaDB trilhaDB; // Banco de dados para recuperar waypoints

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsVisualizarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializa o banco de dados
        trilhaDB = new TrilhaDB(this);

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

        // Habilita os controles de zoom no mapa
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Recupera os waypoints do banco de dados
        ArrayList<Waypoint> waypoints = trilhaDB.recuperarWaypoints();

        if (waypoints != null && !waypoints.isEmpty()) {
            // Adiciona marcadores no mapa para cada waypoint
            for (Waypoint waypoint : waypoints) {
                LatLng latLng = new LatLng(waypoint.getLatitude(), waypoint.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Altitude: " + waypoint.getAltitude() + "m"));
            }

            // Move a câmera para o primeiro waypoint
            LatLng firstWaypoint = new LatLng(waypoints.get(0).getLatitude(), waypoints.get(0).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstWaypoint, 15f));
        } else {
            // Exibe uma mensagem no console se nenhum waypoint for encontrado
            System.out.println("Nenhum waypoint encontrado no banco de dados.");
        }
    }
}
