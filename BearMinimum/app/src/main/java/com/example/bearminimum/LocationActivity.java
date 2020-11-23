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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LocationActivity extends MainActivity implements OnMapReadyCallback{

    private Marker marker;
    private String latitude;
    private String longitude;
    private Double lati;
    private Double longi;
    private String bid;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.location_map);
        mapFragment.getMapAsync(this);


        //db and user
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //the book id for the selected book
        //the book id for the selected book
        bid = getIntent().getExtras().getString("bookid");

        //get the specified book from firestore
        FirebaseFirestore.getInstance().collection("books").document(bid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map data = task.getResult().getData();
                    latitude = (String) data.get("latitude");
                    longitude = (String) data.get("longitude");
                }
            }
        });

        //convert to number format
        lati = Double.parseDouble(latitude);
        longi = Double.parseDouble(longitude);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //set map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Add a misc marker at Edmonton location
        // and move the map's camera to the same location.
        LatLng bookLocation = new LatLng( lati, longi);
        marker = googleMap.addMarker(new MarkerOptions()
                .position(bookLocation)
                .title("generic")
                .draggable(false));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(bookLocation));
    }


}