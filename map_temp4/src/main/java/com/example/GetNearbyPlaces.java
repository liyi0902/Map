package com.example;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object, String, String> {

    private String googleplaceData, url;
    private GoogleMap mMap;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadURL downloadURL = new DownloadURL();
        try {
            googleplaceData = downloadURL.readTheURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("search function", "the returned data is : " + googleplaceData);
        return googleplaceData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(s);
        Log.i("search function", "get returns, start show markers");
        displayNearbyPlaces(nearbyPlacesList);
    }

    private void displayNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList){

        for(int i=0; i<nearbyPlacesList.size(); i++){
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googleNearByPlace = nearbyPlacesList.get(i);
            String nameOfPlace = googleNearByPlace.get("place_name");
            String vicinity = googleNearByPlace.get("vicinity");
            Log.i("search function", "get place = " + nameOfPlace);
            Double lat = Double.parseDouble(googleNearByPlace.get("lat"));
            Double lng = Double.parseDouble(googleNearByPlace.get("lng"));

//            String reference = googleNearByPlace.get("reference");
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(nameOfPlace + ": " + vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mMap.addMarker(markerOptions);

        }

    }
}
