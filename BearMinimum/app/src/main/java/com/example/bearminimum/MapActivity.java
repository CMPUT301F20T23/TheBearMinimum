package com.example.bearminimum;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapActivity extends MainActivity implements OnMapReadyCallback{

    private Button confirmButton;
    private Marker marker;
    private String latitude;
    private String longitude;
    private String bid;
    private Book book;
    private FirebaseFirestore db;
    private String borrower;

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

        //db and user
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //the book id for the selected book
        bid = getIntent().getExtras().getString("bookid");

        //get borrower
        borrower = getIntent().getExtras().getString("borrower");

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the location of the marker and convert to string
                latitude = String.valueOf(marker.getPosition().latitude);
                longitude = String.valueOf(marker.getPosition().longitude);

                //update the selected book
                Map<String, Object> data = new HashMap<>();
                data.put("latitude", latitude);
                data.put("longitude", longitude);
                data.put("borrower", borrower);
                db.collection("books").document(bid).set(data, SetOptions.merge());

                finish();

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
