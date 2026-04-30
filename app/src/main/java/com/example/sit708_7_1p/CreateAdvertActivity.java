package com.example.sit708_7_1p;

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

public class CreateAdvertActivity extends AppCompatActivity {

    String selectedImageUri = "";
    boolean imageSelected = false;
    ImageView previewImageView;

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

        EditText locationEditText = findViewById(R.id.locationEditText);
        Button saveButton = findViewById(R.id.saveButton);
        Button uploadImageButton = findViewById(R.id.uploadImageButton);


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

            boolean inserted = databaseHelper.insertAdvert(
                    type, item, phone, description, date, location, category, selectedImageUri, postedTime
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