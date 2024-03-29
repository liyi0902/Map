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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private final float DEFAULT_ZOOM_FAR = 18;
    private final float DEFAULT_ZOOM_NEAR = 13;
    private final int ProximityRadius = 5000;

    private ImageView ic_favorite;
    private DrawerLayout mDrawerLayout;

    private ArrayList<HashMap<String, String>> savedOptions;
    private final String LOCAL_FILE = "mCollections.txt";
    private NavigationView navigationView;

    public static Context mContext;

    private String RETURN_NUM = "1";
    private LinearLayout hideLinearLayout;
    private Button btn_hideConfirm;
    private EditText editText;

    private LinearLayout move_camera_btns;

    private int centerIndex;
    private ArrayList<LatLng> allCenters;


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
        Places.initialize(MapActivity.this, getResources().getString(R.string.google_map_apikey));
        placeClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        DBActivity.updateNum(RETURN_NUM);
        navigationView = findViewById(R.id.navigation);
        prepareNavigationData();
        navigationView.setNavigationItemSelectedListener(this);


        //invoke bottomsheet dialog when click "SET FILTER" button
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hideLinearLayout.getVisibility() == View.VISIBLE){
                    hideLinearLayout.setVisibility(View.INVISIBLE);
                }
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable("savedOptions", savedOptions);
                bottomSheetDialog.setArguments(bundle);
                bottomSheetDialog.show(getSupportFragmentManager(), "BottomSheet");
            }
        });

        //clear user inputs in search bar and close prediction list
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
                mMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM_NEAR));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialSearchBar.clearSuggestions();
                    }
                }, 1000);

                //hide the soft keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

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
                            if(item.getItemId() == R.id.item_set_number){
                                hideLinearLayout.setVisibility(View.VISIBLE);
                            }else if(item.getItemId() == R.id.item_clear){
                                mMap.clear();
                                move_camera_btns.setVisibility(View.INVISIBLE);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnowLocation.getLatitude(), mLastKnowLocation.getLongitude()),
                                        DEFAULT_ZOOM_NEAR));
                                latOfScreenCenter = mLastKnowLocation.getLatitude();
                                lngOfScreenCenter = mLastKnowLocation.getLongitude();
                            }
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


        //input prediction
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
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOfPlace, DEFAULT_ZOOM_FAR));
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

        hideLinearLayout = findViewById(R.id.text_set_return_number);
        editText = findViewById(R.id.edit_return_number);
        //hideConfirm is the confirm button in the hide LinearLayout
        btn_hideConfirm = findViewById(R.id.btn_return_number_confirm);
        btn_hideConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editText.getText().toString();
                if(input.length() != 0){
                    if(isNumeric(input)){
                        RETURN_NUM = input;
                        editText.setHint(input);
                        DBActivity.updateNum(RETURN_NUM);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                        hideLinearLayout.setVisibility(View.INVISIBLE);
                        Toast.makeText(MapActivity.this, "Set return number successful", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MapActivity.this, "Please enter a validate number", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    hideLinearLayout.setVisibility(View.INVISIBLE);
                }
            }
        });


        move_camera_btns = findViewById(R.id.move_camera);
    }


    private String getURL(double lat, double lng, String place){
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + lat + "," + lng);
        googleURL.append("&radius=" + ProximityRadius);
        googleURL.append("&keyword=" + place);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + getResources().getString(R.string.google_map_apikey));

        Log.d("MapActivity", "url = " + googleURL.toString());
        return googleURL.toString();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);  //enable the location button
        mMap.getUiSettings().setMyLocationButtonEnabled(true);


        //change the position of location button
        if(mapView != null && mapView.findViewById(Integer.parseInt("1")) != null){
            View locationButton = ((View)mapView.findViewById(Integer.parseInt("1")).getParent())
                    .findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 230);

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
                                DEFAULT_ZOOM_FAR));
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
                                        DEFAULT_ZOOM_FAR));
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

    //override the method in BottomSheetDialog,
    //when click "confirm" or "save my option", this method would be called in BottomSheetDialog
    @Override
    public void onButtomClicked(HashMap<String, String> result, String command) {
        switch(command){
            case "save":
                savedOptions.add(result);
                navigationView.getMenu().add(0, savedOptions.size()-1, 0, "my collection " + savedOptions.size());
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
                    //HashMap<String, String> firstResult = null;
                    String sa2_code;
                    ArrayList<float[]> polygonShape;
                    //ArrayList<LatLng> firstPolygon = null;
                    allCenters = new ArrayList<>();
                    LatLng latLng;
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
//                        Polygon polygon1 = mMap.addPolygon(polygonOptions);
                        mMap.addPolygon(polygonOptions);
                        String content = getSelectedAreaInfo(eachResult, result);
                        latLng = getPolygonCenterPoint(polygonCoordinates);
                        allCenters.add(latLng);
                        mMap.addMarker(new MarkerOptions().position(latLng)
                                .title(eachResult.get("sa2_name16"))
                                .snippet(content));
                        latOfScreenCenter = latLng.latitude;
                        lngOfScreenCenter = latLng.longitude;
                    }

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(allCenters.get(0).latitude, allCenters.get(0).longitude),
                            DEFAULT_ZOOM_NEAR));
                    latOfScreenCenter = allCenters.get(0).latitude;
                    lngOfScreenCenter = allCenters.get(0).longitude;

                    if(allCenters.size() > 1){
                        move_camera_btns.setVisibility(View.VISIBLE);
                        Button btn_prev = findViewById(R.id.btn_prev);
                        Button btn_next = findViewById(R.id.btn_next);
                        centerIndex = 0;
                        btn_prev.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                moveToNextArea();
                            }
                        });
                        btn_next.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                moveToPrevArea();
                            }
                        });
                    }
                }
                break;
        }
        //Toast.makeText(this, "get price " + result.get("price"), Toast.LENGTH_SHORT).show();
    }

    public void onBackPressed(){
        if(mDrawerLayout.isDrawerOpen(Gravity.RIGHT)){
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        }else if(hideLinearLayout.getVisibility() == View.VISIBLE){
            hideLinearLayout.setVisibility(View.INVISIBLE);
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
//        Toast.makeText(this, "the id is" + index, Toast.LENGTH_SHORT).show();
        HashMap<String, String> temp = savedOptions.get(index);
        String message = getFilterContent(temp);
        builder.setMessage(message);
        builder.setTitle(menuItem.getTitle());

        builder.setPositiveButton("open in filter", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(hideLinearLayout.getVisibility() == View.VISIBLE){
                    hideLinearLayout.setVisibility(View.INVISIBLE);
                }
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

    //when click an item in navigation drawer
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

    private static boolean isNumeric(String str){
        for (int i=0; i<str.length(); i++){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

    private void moveToNextArea(){
        if(centerIndex < allCenters.size()-1){
            centerIndex ++;
        }else{
            centerIndex = 0;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(allCenters.get(centerIndex).latitude, allCenters.get(centerIndex).longitude),
                DEFAULT_ZOOM_NEAR));
        latOfScreenCenter = allCenters.get(centerIndex).latitude;
        lngOfScreenCenter = allCenters.get(centerIndex).longitude;
    }

    private void moveToPrevArea(){
        if(centerIndex > 0){
            centerIndex --;
        }else{
            centerIndex = allCenters.size()-1;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(allCenters.get(centerIndex).latitude, allCenters.get(centerIndex).longitude),
                DEFAULT_ZOOM_NEAR));
        latOfScreenCenter = allCenters.get(centerIndex).latitude;
        lngOfScreenCenter = allCenters.get(centerIndex).longitude;
    }
}
