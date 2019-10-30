package com.example;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        BottomSheetDialog.BottomSheetListener,
        NavigationView.OnNavigationItemSelectedListener{

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placeClient;
    private List<AutocompletePrediction> predictionList;

    private Location mLastKnowLocation;
    private double latOfScreenCenter;
    private double lngOfScreenCenter;
    private LocationCallback locationCallback;

    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private Button btnFilter;

    private final float DEFAULT_ZOOM = 18;
    private final int ProximityRadius = 5000;

    private ImageView ic_favorite;
    private DrawerLayout mDrawerLayout;

    private ArrayList<HashMap<String, String>> savedOptions;
    private final String LOCAL_FILE = "mCollections.txt";
    private NavigationView navigationView;

    public static Context mContext;

   // private PolygonOptions mPolygonOptions;


//    private List<Address> addresses = new ArrayList<>();   //preserve addresses for search returns

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mContext = this.getApplicationContext();

        materialSearchBar = findViewById(R.id.searchBar);
        btnFilter = findViewById(R.id.btn_filter);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapView = mapFragment.getView();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);
        Places.initialize(MapActivity.this, "AIzaSyC23Nfih07UvEoosLJ7f6k148YtxSReE-4");
        placeClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

//        Intent intent = new Intent(this, DataPrepareService.class);
//        startService(intent);
//

        navigationView = findViewById(R.id.navigation);
        prepareNavigationData();
        navigationView.setNavigationItemSelectedListener(this);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable("savedOptions", savedOptions);
                bottomSheetDialog.setArguments(bundle);
                bottomSheetDialog.show(getSupportFragmentManager(), "BottomSheet");
            }
        });

        ImageView floatingCloseButton = findViewById(R.id.mt_clear);
        floatingCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if(id == R.id.mt_clear){
                    if(materialSearchBar.isSuggestionsVisible()){
                        materialSearchBar.clearSuggestions();
                    }
                    if(materialSearchBar.isSearchEnabled()){
                        materialSearchBar.disableSearch();
                    }
//                    Toast.makeText(MapActivity.this, "the close button is clicked", Toast.LENGTH_SHORT).show();
                }

            }
        });


        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //change this to search inside app
//                startSearch(text.toString(), true, null, true);
//                Toast.makeText(MapActivity.this, "search confirmed", Toast.LENGTH_SHORT).show();
                String searchString = materialSearchBar.getText();
                Object[] transeferData = new Object[2];
                String searchURL = getURL(latOfScreenCenter, lngOfScreenCenter, searchString);
                transeferData[0] = mMap;
                transeferData[1] = searchURL;
                Toast.makeText(MapActivity.this, "get the search text, start search", Toast.LENGTH_SHORT).show();

                GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
                getNearbyPlaces.execute(transeferData);

                Toast.makeText(MapActivity.this, "Searching for nearby " + searchString, Toast.LENGTH_SHORT).show();
                mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialSearchBar.clearSuggestions();
                    }
                }, 1000);

                //hide the soft keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);




//                Geocoder geocoder = new Geocoder(MapActivity.this);
//                List<Address> addresses = new ArrayList<Address>();
//                mMap.clear();
//                try {
//                    addresses = geocoder.getFromLocationName(searchString, 10);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                if(addresses.size() > 0){
//                    for(int i=0; i<addresses.size(); i++){
//                        Address address = addresses.get(i);
//                        address.getLatitude();
//                        address.getLongitude();
//                        address.getThoroughfare();
//                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//                        mMap.addMarker(new MarkerOptions().position(latLng).title(address.getThoroughfare()).
//                                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//                    }
//                }else{
//                    Toast.makeText(MapActivity.this, "Place not found", Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if(buttonCode == MaterialSearchBar.BUTTON_NAVIGATION){
                    //opening or closing a navigation drawer
                    ImageView navIcon = findViewById(R.id.mt_nav);
                    PopupMenu popupMenu = new PopupMenu(MapActivity.this, navIcon);
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            mMap.clear();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnowLocation.getLatitude(), mLastKnowLocation.getLongitude()),
                                    DEFAULT_ZOOM));
                            latOfScreenCenter = mLastKnowLocation.getLatitude();
                            lngOfScreenCenter = mLastKnowLocation.getLongitude();
                            return true;
                        }
                    });
                    popupMenu.show();
                }else if(buttonCode == MaterialSearchBar.BUTTON_BACK){
                    materialSearchBar.disableSearch();
//                    materialSearchBar.clearSuggestions();
                }
            }
        });

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setCountry("AU")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                placeClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if(task.isSuccessful()){
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if(predictionsResponse != null){
                                predictionList = predictionsResponse.getAutocompletePredictions();
                                List<String> suggestionsList = new ArrayList<>();
                                for(int i=0; i<predictionList.size(); i++){
                                    AutocompletePrediction prediction = predictionList.get(i);
                                    suggestionsList.add(prediction.getFullText(null).toString());
                                }
                                materialSearchBar.updateLastSuggestions(suggestionsList);
                                if(!materialSearchBar.isSuggestionsVisible()){
                                    materialSearchBar.showSuggestionsList();
                                }
                            }
                        }else{
                            Log.i("mytag", "prediction fetching task unsuccessful");
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if(position >= predictionList.size()){
                    return;
                }
                AutocompletePrediction selectedPrediction = predictionList.get(position);
                final String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(suggestion);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialSearchBar.clearSuggestions();
                    }
                }, 1000);


                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                if(imm != null){
                    imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
                String placeId = selectedPrediction.getPlaceId();
                List<Place.Field> placeField = Arrays.asList(Place.Field.LAT_LNG);

                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeField).build();
                placeClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        Log.i("mytag", "Place found: " + place.getName());
                        LatLng latLngOfPlace = place.getLatLng();
                        if(latLngOfPlace != null){
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOfPlace, DEFAULT_ZOOM));
                            latOfScreenCenter = latLngOfPlace.latitude;
                            lngOfScreenCenter = latLngOfPlace.longitude;
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof ApiException){
                            ApiException apiException = (ApiException) e;
                            apiException.printStackTrace();
                            int statusCode = apiException.getStatusCode();
                            Log.i("mytag", "Place not found: " + e.getMessage());
                            Log.i("mytag", "status code: " + statusCode);
                        }
                    }
                });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });

        mDrawerLayout = findViewById(R.id.mDrawerLayout);
        ic_favorite = findViewById(R.id.ic_favorite);
        ic_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DrawerLayout mDrawerLayout = findViewById(R.id.navigation);
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
        });



    }


    private String getURL(double lat, double lng, String place){
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + lat + "," + lng);
        googleURL.append("&radius=" + ProximityRadius);
        googleURL.append("&keyword=" + place);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + "AIzaSyC23Nfih07UvEoosLJ7f6k148YtxSReE-4");

        Log.d("MapActivity", "url = " + googleURL.toString());
        return googleURL.toString();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);  //enable the location button
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

//        if(mPolygonOptions != null){
//            mMap.addPolygon(mPolygonOptions);
//        }

        //change the position of location button
        if(mapView != null && mapView.findViewById(Integer.parseInt("1")) != null){
            View locationButton = ((View)mapView.findViewById(Integer.parseInt("1")).getParent())
                    .findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 200);

        }

        //check if gps is enabled or not then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(MapActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(MapActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(MapActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException){
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MapActivity.this, 51);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if(materialSearchBar.isSuggestionsVisible()){
                    materialSearchBar.clearSuggestions();
                }
                if(materialSearchBar.isSearchEnabled()){
                    materialSearchBar.disableSearch();
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 51){
            if(resultCode == RESULT_OK){
                getDeviceLocation();
            }
        }
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    mLastKnowLocation = task.getResult();
                    if(mLastKnowLocation != null){
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnowLocation.getLatitude(), mLastKnowLocation.getLongitude()),
                                DEFAULT_ZOOM));
                        latOfScreenCenter = mLastKnowLocation.getLatitude();
                        lngOfScreenCenter = mLastKnowLocation.getLongitude();
                    }else{
                        LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setInterval(10000);
                        locationRequest.setFastestInterval(5000);
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationCallback = new LocationCallback(){
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                if(locationResult == null){
                                    return;
                                }
                                mLastKnowLocation = locationResult.getLastLocation();
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnowLocation.getLatitude(), mLastKnowLocation.getLongitude()),
                                        DEFAULT_ZOOM));
                                latOfScreenCenter = mLastKnowLocation.getLatitude();
                                lngOfScreenCenter = mLastKnowLocation.getLongitude();
                                mFusedLocationProviderClient.removeLocationUpdates(locationCallback); //don't know its use, maybe it makes app get location only once
                            }
                        };
                        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                }else{
                    Toast.makeText(MapActivity.this, "unalbe to get last location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onButtomClicked(HashMap<String, String> result, String command) {
        switch(command){
            case "save":
                savedOptions.add(result);
                navigationView.getMenu().add("my collection " + savedOptions.size());
                saveNavigationData();
                Toast.makeText(MapActivity.this, "saved setting successful", Toast.LENGTH_SHORT).show();
                break;
            case "confirm":
                DBActivity dbActivity = new DBActivity();
                ArrayList<HashMap<String, String>> returnedResult = dbActivity.filterRequest(result);
                if(returnedResult.size() == 0){
                    Toast.makeText(this, "cannot find a proper area", Toast.LENGTH_LONG).show();
                }else{
                    mMap.clear();
                    mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapActivity.this));

                    HashMap<String, String> eachResult;
                    HashMap<String, String> firstResult = null;
                    String sa2_code;
                    ArrayList<float[]> polygonShape;
                    ArrayList<LatLng> firstPolygon = null;
                    for(int i=0; i<returnedResult.size(); i++){
                        eachResult = returnedResult.get(i);
                        sa2_code = eachResult.get("sa2_main16");
                        polygonShape = dbActivity.polygonRequest(sa2_code);
                        float[] temp;
                        ArrayList<LatLng> polygonCoordinates = new ArrayList<>();
                        for(int j=0; j<polygonShape.size(); j++){
                            temp = polygonShape.get(j);
                            polygonCoordinates.add(new LatLng(temp[1], temp[0]));
                        }
                        PolygonOptions polygonOptions = new PolygonOptions();
                        polygonOptions.addAll(polygonCoordinates);
                        if(i == 0){
                            firstPolygon = polygonCoordinates;
                            firstResult = eachResult;
                        }
                        //polygonOptions.fillColor(R.color.selected);
                        Polygon polygon1 = mMap.addPolygon(polygonOptions);
                        //mPolygonOptions = polygonOptions;
//                    polygon1.setFillColor(Color.);
                    }

                    if(firstPolygon != null && firstResult != null){
                        LatLng latLng = getPolygonCenterPoint(firstPolygon);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(latLng.latitude, latLng.longitude),
                                13));
                        String content = getSelectedAreaInfo(firstResult, result);
                        mMap.addMarker(new MarkerOptions().position(latLng)
                                .title(firstResult.get("sa2_name16"))
                                .snippet(content));
                        latOfScreenCenter = latLng.latitude;
                        lngOfScreenCenter = latLng.longitude;
                    }

                }
                break;
        }
        //Toast.makeText(this, "get price " + result.get("price"), Toast.LENGTH_SHORT).show();
    }

    public void onBackPressed(){
        if(mDrawerLayout.isDrawerOpen(Gravity.RIGHT)){
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        }else{
            super.onBackPressed();
        }
    }

    public void prepareNavigationData(){
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            fileInputStream = openFileInput(LOCAL_FILE);
            objectInputStream = new ObjectInputStream(fileInputStream);
            savedOptions = (ArrayList<HashMap<String, String>>) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            try{
                if(objectInputStream != null){
                    objectInputStream.close();
                }
                if(fileInputStream != null){
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(savedOptions != null){
            //HashMap<String, String> temp;
            Menu menu = navigationView.getMenu();
            for(int i=0; i<savedOptions.size(); i++){
                menu.add(0,  i, 0,"my collection " + String.valueOf(i+1));
            }

        }else{
            savedOptions = new ArrayList<>();
        }
    }

    public void saveNavigationData(){
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            fileOutputStream = openFileOutput(LOCAL_FILE, MODE_PRIVATE);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(savedOptions);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if(objectOutputStream != null){
                    objectOutputStream.close();
                }
                if(fileOutputStream != null){
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(this, "saved file successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int index = menuItem.getItemId();
        Toast.makeText(this, "the id is" + index, Toast.LENGTH_SHORT).show();
        HashMap<String, String> temp = savedOptions.get(index);
        String message = getFilterContent(temp);
        builder.setMessage(message);
        builder.setTitle(menuItem.getTitle());

        builder.setPositiveButton("open in filter", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int index = menuItem.getItemId();
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
                activateBottomSheet(index);
            }
        });

        builder.setNegativeButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int index = menuItem.getItemId();
                deleteItemInNavigation(index);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

       // Toast.makeText(MapActivity.this, "id is " + menuItem.getItemId(), Toast.LENGTH_SHORT).show();
        return false;
    }

    public void deleteItemInNavigation(int index){
        savedOptions.remove(index);
        Menu menu = navigationView.getMenu();
        menu.clear();
        for(int i=0; i<savedOptions.size(); i++){
            menu.add(0, i, 0, "my collection " + String.valueOf(i+1));
        }
        saveNavigationData();
    }

    public void activateBottomSheet(int index){
        HashMap<String, String> temp = savedOptions.get(index);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedItem", temp);
        bottomSheetDialog.setArguments(bundle);
        bottomSheetDialog.show(getSupportFragmentManager(), "BottomSheet");
    }

    private LatLng getPolygonCenterPoint(ArrayList<LatLng> polygonPointsList){
        LatLng centerLatLng = null;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0 ; i < polygonPointsList.size() ; i++)
        {
            builder.include(polygonPointsList.get(i));
        }
        LatLngBounds bounds = builder.build();
        centerLatLng =  bounds.getCenter();

        return centerLatLng;
    }

    private String getFilterContent(HashMap<String, String> hashMap){
        StringBuilder content = new StringBuilder();
        if(!hashMap.get("housePrice").equals("0")){
            content.append("housePrice: " + hashMap.get("housePrice") + "\n");
        }
        if(!hashMap.get("unitPrice").equals("0")){
            content.append("unitPrice: " + hashMap.get("unitPrice") + "\n" );
        }
        if(!hashMap.get("traffic").equals("0")){
            content.append("traffic: " + hashMap.get("traffic") + "\n" );
        }
        if(!hashMap.get("houseRent").equals("0")){
            content.append("houseRent: " + hashMap.get("houseRent") + "\n" );
        }
        if(!hashMap.get("unitRent").equals("0")){
            content.append("unitRent: " + hashMap.get("unitRent") + "\n" );
        }
        if(!hashMap.get("income").equals("0")){
            content.append("income: " + hashMap.get("income") + "\n" );
        }
        if(!hashMap.get("education").equals("0")){
            content.append("education: " + hashMap.get("education") + "\n" );
        }
        if(!hashMap.get("immigrant").equals("0")){
            content.append("immigrant: " + hashMap.get("immigrant") + "\n" );
        }
        if(hashMap.get("religion")!=null){
            content.append("religion: " + hashMap.get("religion") + "\n" );
        }

        content.deleteCharAt(content.length() - 1);

        return content.toString();
    }

    private String getSelectedAreaInfo(HashMap<String, String> hashMap, HashMap<String, String> filter){
        StringBuilder content = new StringBuilder();
        if(hashMap.get("h_sale_price") != null && !hashMap.get("h_sale_price").equals("0")){
            content.append("Average house price: " + hashMap.get("h_sale_price") + "$" + "\n");
        }
        if(hashMap.get("u_sale_price") != null && !hashMap.get("u_sale_price").equals("0")){
            content.append("Average unit price: " + hashMap.get("u_sale_price") + "$" + "\n" );
        }
        if(hashMap.get("vehicle_num") != null && !hashMap.get("vehicle_num").equals("0")){
            content.append("Vehicle quantity: " + hashMap.get("vehicle_num") + "\n" );
        }
        if(hashMap.get("h_rent_price") != null && !hashMap.get("h_rent_price").equals("0")){
            content.append("Average house rent: " + hashMap.get("h_rent_price") + "$/week" + "\n" );
        }
        if(hashMap.get("u_rent_price") != null && !hashMap.get("u_rent_price").equals("0")){
            content.append("Average unit rent: " + hashMap.get("u_rent_price") + "$/week" + "\n" );
        }
        if(hashMap.get("median_income") != null && !hashMap.get("median_income").equals("0")){
            content.append("Median income: " + hashMap.get("median_income") + "$" + "\n" );
        }
        if(hashMap.get("children_education") != null && !hashMap.get("children_education").equals("0")){
            content.append("Education quality: " + hashMap.get("children_education") + "\n" );
        }
        if(hashMap.get("immi_not_citizen") != null && !hashMap.get("immi_not_citizen").equals("0")){
            content.append("Immigrant: " + hashMap.get("immi_not_citizen") + "%" + "\n" );
        }

        if(filter.get("religion") != null){
            String key = filter.get("religion");
            switch (key){
                case "christian":
                    content.append("Christian: " + hashMap.get("christian_pr100") + "%" + "\n" );
                    break;
                case "buddhism":
                    content.append("Buddhism: " + hashMap.get("buddhism_pr100") + "%" + "\n" );
                    break;
                case "hinduism":
                    content.append("Hinduism: " + hashMap.get("hinduism_pr100") + "%" + "\n" );
                    break;
                case "judasim":
                    content.append("Judaism: " + hashMap.get("judaism_pr100") + "%" + "\n" );
                    break;
                case "islam":
                    content.append("Islam: " + hashMap.get("islam_pr100") + "%" + "\n" );
                    break;
                case "others":
                    content.append("Other religions: " + hashMap.get("ohetr_religion_pr100") + "%" + "\n" );
                    break;
            }
        }

        content.deleteCharAt(content.length() - 1);

        return content.toString();
    }
}
//result[0] = "sa2_main16";
//        result[1] = "sa2_name16";
//        result[2] = "h_sale_price";
//        result[3] ="h_rent_price";
//        result[4] = "u_sale_price";
//        result[5] = "u_rent_price";
//        result[6] = "vehicle_num";
//        result[7] ="median_income";
//        result[8] ="children_education";
//        result[9] ="immi_not_citizen";
//        result[10] ="christian_pr100";
//        result[11] ="buddhism_pr100";
//        result[12] ="hinduism_pr100";
//        result[13] = "judaism_pr100 ";
//        result[14] ="islam_pr100";
//        result[15] ="ohetr_religion_pr100";