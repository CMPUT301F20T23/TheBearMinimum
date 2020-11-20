package com.example.bearminimum;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends MainActivity implements OnMapReadyCallback{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //set map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Add a misc marker
        // and move the map's camera to the same location.
        LatLng misc = new LatLng(0, 0);
        googleMap.addMarker(new MarkerOptions()
                .position(misc)
                .title("generic"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(misc));
    }

}
