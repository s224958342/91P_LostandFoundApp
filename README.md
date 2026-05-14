# SIT708 Task 9.1P - Lost and Found Map Mobile App

## Project Overview

This project is an Android Lost and Found mobile application developed for SIT708 Task 9.1P.  
It is extended from the previous Task 7.1P Lost and Found app by adding geo-location and map features.

The app allows users to create lost or found adverts, upload an item image, select or detect a location, and view lost/found items on Google Maps. Users can also search for nearby items using a radius-based search.

## Student Details

- Name: Your Name
- Student ID: Your Student ID
- Unit: SIT708
- Task: Pass Task 9.1P - Lost and Found Map Mobile App

## Main Features

### 1. Create Lost or Found Advert

Users can create a new advert by entering:

- Advert type: Lost or Found
- Item name
- Phone number
- Description
- Date
- Location
- Category
- Item image

The advert is saved into a local SQLite database.

### 2. Image Upload

Users are required to upload an image before saving an advert.  
The selected image is previewed in the Create Advert page and saved using its URI.

### 3. Date Picker

The app uses a DatePickerDialog so users can select the advert date easily instead of typing it manually.

### 4. Category Filtering

Users can filter lost and found adverts by category, such as:

- Electronics
- Pets
- Wallets

This is implemented using a Spinner and SQLite query.

### 5. Google Maps Integration

A new **Show On Map** button is added to the home page.  
When the user clicks this button, the app opens a Google Map screen and displays lost and found adverts as map markers.

### 6. Location Autocomplete

The app uses Google Places Autocomplete to help users select a valid location.  
After selecting a place, the app saves the address, latitude, and longitude.

### 7. Get Current Location

Users can click the **GET CURRENT LOCATION** button to automatically get their current location.  
The app uses `FusedLocationProviderClient` to get the user’s latitude and longitude.

### 8. Radius-Based Search

The map page includes a radius search feature.  
Users can enter a distance in kilometres, for example 5 km, and the app will only show lost or found items within that distance from the user’s current location.

The app calculates the distance between the user and each advert using latitude and longitude.

### 9. Advert Detail and Delete

Users can click an advert from the list to view full details.  
The detail page shows the item information and image.  
Users can also delete an advert when it is no longer needed.

## Technologies Used

- Java
- Android Studio
- SQLite Database
- Google Maps SDK for Android
- Google Places API
- Fused Location Provider
- Android Emulator
- XML Layouts

## Project Structure

```text
app/src/main/java/com/example/sit708_9_1p/
│
├── MainActivity.java
│   - Handles home page navigation
│   - Opens CreateAdvertActivity, ItemListActivity, and MapsActivity
│
├── CreateAdvertActivity.java
│   - Creates lost/found adverts
│   - Handles image upload
│   - Handles DatePickerDialog
│   - Handles Google Places Autocomplete
│   - Handles Get Current Location
│   - Saves advert data into SQLite
│
├── DatabaseHelper.java
│   - Creates and manages SQLite database
│   - Inserts advert data
│   - Retrieves all adverts
│   - Retrieves adverts by category
│   - Deletes adverts
│
├── ItemListActivity.java
│   - Displays saved adverts in a ListView
│   - Supports category filtering
│   - Opens AdvertDetailActivity when an advert is clicked
│
├── AdvertDetailActivity.java
│   - Displays full advert details
│   - Shows uploaded item image
│   - Allows user to delete advert
│
└── MapsActivity.java
    - Displays Google Map
    - Shows advert markers
    - Gets current location
    - Handles radius-based search
