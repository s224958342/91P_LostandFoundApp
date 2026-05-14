package com.example.sit708_9_1p;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.sit708_9_1p.databinding.ActivityMapsBinding;

import android.database.Cursor;
import android.widget.Toast;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.model.Marker;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private DatabaseHelper databaseHelper;

    private FusedLocationProviderClient fusedLocationClient;
    private EditText radiusEditText;
    private Button searchRadiusButton;
    private double userLatitude;
    private double userLongitude;
    private boolean userLocationReady = false;
    private Button myLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        radiusEditText = findViewById(R.id.radiusEditText);
        searchRadiusButton = findViewById(R.id.searchRadiusButton);

        myLocationButton = findViewById(R.id.myLocationButton);

        myLocationButton.setOnClickListener(v -> {
            moveToCurrentLocation();
        });

        searchRadiusButton.setOnClickListener(v -> {
            getUserLocationAndShowNearbyAdverts();
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        enableMyLocationOnMap();

        showAllAdvertsOnMap();
    }

    private void showAllAdvertsOnMap() {
        mMap.clear();

        Cursor cursor = databaseHelper.getAllAdverts();

        if (cursor != null && cursor.moveToFirst()) {
            boolean firstMarker = true;

            do {
                String type = cursor.getString(1);
                String item = cursor.getString(2);
                String location = cursor.getString(6);
                double latitude = cursor.getDouble(7);
                double longitude = cursor.getDouble(8);

                LatLng advertPosition = new LatLng(latitude, longitude);

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(advertPosition)
                        .title(type + ": " + item)
                        .snippet(location));

                if (marker != null) {
                    marker.showInfoWindow();
                }

                if (firstMarker) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(advertPosition, 12));
                    firstMarker = false;
                }

            } while (cursor.moveToNext());

            cursor.close();
        } else {
            Toast.makeText(this, "No adverts found", Toast.LENGTH_SHORT).show();

            LatLng melbourne = new LatLng(-37.8136, 144.9631);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(melbourne, 10));
        }
    }

    private void getUserLocationAndShowNearbyAdverts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    200
            );
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        userLatitude = location.getLatitude();
                        userLongitude = location.getLongitude();
                        userLocationReady = true;

                        showAdvertsWithinRadius();
                    } else {
                        Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAdvertsWithinRadius() {
        String radiusText = radiusEditText.getText().toString();

        if (radiusText.isEmpty()) {
            Toast.makeText(this, "Please enter radius in km", Toast.LENGTH_SHORT).show();
            return;
        }

        double radiusKm = Double.parseDouble(radiusText);

        mMap.clear();

        LatLng userPosition = new LatLng(userLatitude, userLongitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 12));

        Cursor cursor = databaseHelper.getAllAdverts();

        int count = 0;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String type = cursor.getString(1);
                String item = cursor.getString(2);
                String location = cursor.getString(6);
                double advertLatitude = cursor.getDouble(7);
                double advertLongitude = cursor.getDouble(8);

                float[] results = new float[1];

                Location.distanceBetween(
                        userLatitude,
                        userLongitude,
                        advertLatitude,
                        advertLongitude,
                        results
                );

                double distanceKm = results[0] / 1000.0;

                if (distanceKm <= radiusKm) {
                    LatLng advertPosition = new LatLng(advertLatitude, advertLongitude);

                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(advertPosition)
                            .title(type + ": " + item)
                            .snippet(location + " (" + String.format("%.2f", distanceKm) + " km away)"));

                    if (marker != null) {
                        marker.showInfoWindow();
                    }

                    count++;
                }

            } while (cursor.moveToNext());

            cursor.close();
        }

        Toast.makeText(this, count + " adverts found within " + radiusKm + " km", Toast.LENGTH_SHORT).show();
    }

    private void enableMyLocationOnMap() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    200
            );
        }
    }

    private void moveToCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    200
            );
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng currentPosition = new LatLng(
                                location.getLatitude(),
                                location.getLongitude()
                        );

                        mMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(currentPosition, 15)
                        );

                    } else {
                        Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}