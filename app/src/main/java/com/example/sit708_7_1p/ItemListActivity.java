package com.example.sit708_7_1p;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import android.content.Intent;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.view.View;

public class ItemListActivity extends AppCompatActivity {

    ListView listView;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        listView = findViewById(R.id.listView);
        databaseHelper = new DatabaseHelper(this);

        ArrayList<String> list = new ArrayList<>();
        ArrayList<Integer> idList = new ArrayList<>();

        Cursor cursor = databaseHelper.getAdvertsByCategory("All");

        Spinner filterSpinner = findViewById(R.id.filterSpinner);

        String[] categories = {"All", "Electronics", "Pets", "Wallets"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);


        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String item = cursor.getString(2);
                String type = cursor.getString(1);

                long postedTime = cursor.getLong(9);
                String timeAgo = getTimeAgo(postedTime);

                list.add(type + " - " + item + "\nPosted " + timeAgo);

                idList.add(id);
            } while (cursor.moveToNext());
        }

        ArrayAdapter<String> listAdapter  = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                list
        );

        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            int advertId = idList.get(position);

            Intent intent = new Intent(ItemListActivity.this, AdvertDetailActivity.class);
            intent.putExtra("id", advertId);
            startActivity(intent);
        });

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedCategory = parent.getItemAtPosition(position).toString();

                ArrayList<String> list = new ArrayList<>();
                ArrayList<Integer> idList = new ArrayList<>();

                Cursor cursor = databaseHelper.getAdvertsByCategory(selectedCategory);

                if (cursor.moveToFirst()) {
                    do {
                        int advertId = cursor.getInt(0);
                        String item = cursor.getString(2);
                        String type = cursor.getString(1);

                        long postedTime = cursor.getLong(9);
                        String timeAgo = getTimeAgo(postedTime);

                        list.add(type + " - " + item + "\nPosted " + timeAgo);

                        idList.add(advertId);
                    } while (cursor.moveToNext());
                }

                ArrayAdapter<String> newAdapter = new ArrayAdapter<>(
                        ItemListActivity.this,
                        android.R.layout.simple_list_item_1,
                        list
                );

                listView.setAdapter(newAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private String getTimeAgo(long postedTime) {
        long now = System.currentTimeMillis();
        long diff = now - postedTime;

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (seconds < 60) {
            return "just now";
        } else if (minutes < 60) {
            return minutes + " min ago";
        } else if (hours < 24) {
            return hours + " hours ago";
        } else {
            return days + " days ago";
        }
    }


}