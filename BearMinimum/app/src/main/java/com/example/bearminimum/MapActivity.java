package com.example.bearminimum;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * this class is an activty for viewing and setting the location associated with a book
 */
public class MapActivity extends MainActivity implements OnMapReadyCallback{

    private Button confirmButton;
    private Marker marker;
    private String latitude;
    private String longitude;
    private String bid;
    private Book book;
    private FirebaseFirestore db;
    private String borrower;

    private GoogleMap map;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private static final int DEFAULT_ZOOM = 15;
    private final LatLng defaultLocation = new LatLng(53.631611, -113.323975);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
                Log.d("MAPACT", "marker: " + latitude + ", " + longitude);

                //update the selected book
                Map<String, Object> data = new HashMap<>();
                data.put("latitude", latitude);
                data.put("longitude", longitude);
                //data.put("borrower", borrower);
                db.collection("books").document(bid).set(data, SetOptions.merge());

                finish();

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //set map type
        this.map = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        getLocationPermission();
        //marker doesn't seem to update its position unless this listener is here
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {}
            @Override
            public void onMarkerDrag(Marker marker) {}
            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.d("MAPACT", "drag end: " + marker.getPosition().latitude + ", " + marker.getPosition().longitude);
            }
        });
        //if no location is set, use the devices current location
        if (getIntent().getStringExtra("latitude").equals("")) {
            Log.d("MAP", "using device location");
            getDeviceLocation();
        } else {
            Log.d("MAP", "using stored location");
            LatLng misc = new LatLng(Double.parseDouble(getIntent().getStringExtra("latitude")), Double.parseDouble(getIntent().getStringExtra("longitude")));
            marker = map.addMarker(new MarkerOptions()
                    .position(misc)
                    .title("generic")
                    .draggable(true));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(Double.parseDouble(getIntent().getStringExtra("latitude")), Double.parseDouble(getIntent().getStringExtra("longitude"))), DEFAULT_ZOOM));
        }
    }

    /**
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            Log.d("location permissions","permission request");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                Log.d("maps", "got location");
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                // Add a misc marker at current location
                                // and move the map's camera to the same location.
                                LatLng misc = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                marker = map.addMarker(new MarkerOptions()
                                        .position(misc)
                                        .title("generic")
                                        .draggable(true));
                            }
                        } else {
                            Log.d("maps", "Current location is null. Using defaults.");
                            Log.e("maps", "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                } else {
                    Toast.makeText(MapActivity.this, "location permission not granted, using default location", Toast.LENGTH_LONG).show();
                    useDefaultLocation();
                }
            }
        }
        if (getIntent().getStringExtra("latitude").equals(""))
            getDeviceLocation();
    }

    private void useDefaultLocation() {
        //default to edmonton
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(53.564439795215634, -113.49549423687328), 10));
        // Add a misc marker at current location
        // and move the map's camera to the same location.
        LatLng misc = new LatLng(53.564439795215634, -113.49549423687328);
        marker = map.addMarker(new MarkerOptions()
                .position(misc)
                .title("generic")
                .draggable(true));
    }
}
