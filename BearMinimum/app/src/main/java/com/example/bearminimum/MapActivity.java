package com.example.bearminimum;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends MainActivity implements OnMapReadyCallback{

    private Button confirmButton;
    public LatLng location;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //button UI element
        confirmButton = findViewById(R.id.location_selected);


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the location of the marker
                Double latitude = marker.getPosition().latitude;
                Double longitude = marker.getPosition().longitude;
                location = new LatLng(latitude, longitude);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //set map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Add a misc marker at Edmonton location
        // and move the map's camera to the same location.
        LatLng misc = new LatLng(53.631611, -113.323975);
        marker = googleMap.addMarker(new MarkerOptions()
                .position(misc)
                .title("generic")
                .draggable(true));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(misc));
    }


}
