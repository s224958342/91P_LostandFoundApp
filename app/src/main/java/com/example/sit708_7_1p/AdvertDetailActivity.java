package com.example.sit708_7_1p;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.widget.Toast;
import android.database.Cursor;
import android.net.Uri;
import android.widget.ImageView;

public class AdvertDetailActivity extends AppCompatActivity {

    int advertId;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advert_detail);

        TextView detailText = findViewById(R.id.detailText);
        Button deleteButton = findViewById(R.id.deleteButton);
        ImageView detailImageView = findViewById(R.id.detailImageView);

        databaseHelper = new DatabaseHelper(this);

        advertId = getIntent().getIntExtra("id", -1);

        Cursor cursor = databaseHelper.getAdvertById(advertId);

        if (cursor.moveToFirst()) {
            String type = cursor.getString(1);
            String item = cursor.getString(2);
            String phone = cursor.getString(3);
            String description = cursor.getString(4);
            String date = cursor.getString(5);
            String location = cursor.getString(6);
            String imageUri = cursor.getString(8);

            if (imageUri != null && !imageUri.isEmpty()) {
                try {
                    detailImageView.setImageURI(Uri.parse(imageUri));
                } catch (Exception e) {
                    Toast.makeText(this, "Cannot load image", Toast.LENGTH_SHORT).show();
                }
            }

            detailText.setText(
                    "Type: " + type +
                            "\nItem: " + item +
                            "\nPhone: " + phone +
                            "\nDescription: " + description +
                            "\nDate: " + date +
                            "\nLocation: " + location
            );
        }

        deleteButton.setOnClickListener(v -> {
            databaseHelper.deleteAdvert(advertId);
            Toast.makeText(this, "Advert removed", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(AdvertDetailActivity.this, ItemListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}