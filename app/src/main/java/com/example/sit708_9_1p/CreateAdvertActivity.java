package com.example.sit708_9_1p;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.app.DatePickerDialog;
import java.util.Calendar;
import android.view.View;

import android.location.Address;
import android.location.Geocoder;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.util.Arrays;

public class CreateAdvertActivity extends AppCompatActivity {

    String selectedImageUri = "";
    boolean imageSelected = false;
    ImageView previewImageView;

    double selectedLatitude = 0;
    double selectedLongitude = 0;
    boolean locationSelected = false;
    private ActivityResultLauncher<Intent> autocompleteLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_advert);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RadioGroup typeRadioGroup = findViewById(R.id.typeRadioGroup);
        EditText itemEditText = findViewById(R.id.itemEditText);
        EditText phoneEditText = findViewById(R.id.phoneEditText);
        EditText descriptionEditText = findViewById(R.id.descriptionEditText);
        EditText dateEditText = findViewById(R.id.dateEditText);

        dateEditText.setFocusable(false);
        dateEditText.setClickable(true);

        dateEditText.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        dateEditText.setText(selectedDate);
                    },
                    year,
                    month,
                    day
            );

            datePickerDialog.show();
        });

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCQeGBR4UoX_pgYmkXV5qUiKrRfGQbWtiw");
        }

        EditText locationEditText = findViewById(R.id.locationEditText);

        autocompleteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Place place = Autocomplete.getPlaceFromIntent(result.getData());

                        locationEditText.setText(place.getAddress());

                        if (place.getLatLng() != null) {
                            selectedLatitude = place.getLatLng().latitude;
                            selectedLongitude = place.getLatLng().longitude;
                            locationSelected = true;
                        }
                    }
                }
        );

        locationEditText.setFocusable(false);
        locationEditText.setClickable(true);

        locationEditText.setOnClickListener(v -> {
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY,
                    Arrays.asList(
                            Place.Field.ID,
                            Place.Field.NAME,
                            Place.Field.ADDRESS,
                            Place.Field.LAT_LNG
                    )
            ).build(this);

            autocompleteLauncher.launch(intent);
        });

        Button saveButton = findViewById(R.id.saveButton);
        Button uploadImageButton = findViewById(R.id.uploadImageButton);

        Button currentLocationButton = findViewById(R.id.currentLocationButton);
        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);


        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        Spinner categorySpinner = findViewById(R.id.categorySpinner);

        previewImageView = findViewById(R.id.previewImageView);

        String[] categories = {"Electronics", "Pets", "Wallets"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        uploadImageButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(intent, 1);
        });

        currentLocationButton.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        100
                );
                return;
            }

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            selectedLatitude = location.getLatitude();
                            selectedLongitude = location.getLongitude();
                            locationSelected = true;

                            try {
                                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(
                                        selectedLatitude,
                                        selectedLongitude,
                                        1
                                );

                                if (addresses != null && !addresses.isEmpty()) {
                                    String address = addresses.get(0).getAddressLine(0);
                                    locationEditText.setText(address);
                                } else {
                                    locationEditText.setText(
                                            selectedLatitude + ", " + selectedLongitude
                                    );
                                }

                                Toast.makeText(this, "Current location selected", Toast.LENGTH_SHORT).show();

                            } catch (IOException e) {
                                locationEditText.setText(selectedLatitude + ", " + selectedLongitude);
                            }
                        } else {
                            Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        saveButton.setOnClickListener(view -> {

            if (!imageSelected) {
                Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = typeRadioGroup.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this, "Please select Lost or Found", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadioButton = findViewById(selectedId);

            String type = selectedRadioButton.getText().toString();
            String item = itemEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String date = dateEditText.getText().toString();
            String location = locationEditText.getText().toString();
            String category = categorySpinner.getSelectedItem().toString();

            long postedTime = System.currentTimeMillis();

            double latitude;
            double longitude;

            if (locationSelected) {
                latitude = selectedLatitude;
                longitude = selectedLongitude;
            } else {
                try {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocationName(location, 1);

                    if (addresses == null || addresses.isEmpty()) {
                        Toast.makeText(this, "Cannot find this location. Please enter a more specific address.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Address address = addresses.get(0);
                    latitude = address.getLatitude();
                    longitude = address.getLongitude();

                } catch (IOException e) {
                    Toast.makeText(this, "Location service error. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            boolean inserted = databaseHelper.insertAdvert(
                    type, item, phone, description, date, location,
                    latitude, longitude,
                    category, selectedImageUri, postedTime
            );

            if (inserted) {
                Toast.makeText(this, "Advert saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save advert", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        previewImageView.setVisibility(View.VISIBLE);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageSelected = true;

            Uri imageUri = data.getData();
            selectedImageUri = imageUri.toString();

            getContentResolver().takePersistableUriPermission(
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            previewImageView.setImageURI(imageUri);
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
        }
    }
}