package com.its.tourist;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.appbar.AppBarLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    private ToolbarArcBackground mToolbarArcBackground;
    private AppBarLayout mAppBarLayout;
    private GoogleMap mMap;
    private ArrayList<Marker> mMusei;

    public static String BaseUrl = "http://api.openweathermap.org/";
    public static String AppId = "c96e8bd7dcf26eab873b1b5417951ba7";
    public static String lat = "45.070935";
    public static String lon = "7.685048";



    //


    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 7500;


    //private TextView weatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);

        GlobalVariable global = GlobalVariable.getInstance();
        global.setBackPeople(true);
        global.setHandlerPeople(true);

        mToolbarArcBackground = findViewById(R.id.toolbarArcBackground);
        mAppBarLayout = findViewById(R.id.appbar);

        visualizzaMappa();
        treeObserve();
        toolbar();
        getWindow().getDecorView().post(() -> mToolbarArcBackground.startAnimate());
        getCurrentData();

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));

        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);

        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Collections.singletonList(Place.Field.NAME);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    FindCurrentPlaceResponse response = task.getResult();
                    assert response != null;
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Log.i("posto", String.format("Place '%s' has likelihood: %f",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e("Not found", "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        } else {
            checkLocationPermission();
        }

    }

    void getCurrentData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getCurrentWeatherData(lat, lon, AppId);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.code() == 200) {
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;

                    @SuppressLint("DefaultLocale") String stringBuilder = "Country: " +
                            weatherResponse.sys.country +
                            "\n" +
                            "Temperatura: " +
                            String.format("%.2f",kelvinToCelsius(weatherResponse.main.temp)) +
                            "\n" +
                            "Minima: " +
                            String.format("%.2f",kelvinToCelsius(weatherResponse.main.temp_min)) +
                            "\n" +
                            "Massima: " +
                            String.format("%.2f",kelvinToCelsius(weatherResponse.main.temp_max)) +
                            "\n" +
                            "Umidità: " +
                            weatherResponse.main.humidity +
                            "\n" +
                            "Pressione atmosferica: " +
                            weatherResponse.main.pressure;

                    Toast.makeText(MapActivity.this, stringBuilder, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) { }

        });
    }

    double kelvinToCelsius(double grades) {
        return grades - 273.15;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        setInitialZoomMap();
        //setInitialMarkers();
        //filtriMarker();

        //Circoscrizione Torino
        circoscrizioneTorino();

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("Chiudi").setMessage("Sei sicuro di voler uscire?")
                .setPositiveButton("ESCI", (dialogInterface, i) -> {
                    Intent intent = new Intent(MapActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Exit", true);
                    startActivity(intent);
                    finish();
                }).setNegativeButton("ANNULLA", (dialogInterface, i) -> { }).show();
    }

    /*
    private Marker addMarker(LatLng coords, String title, float color){
        return mMap.addMarker(new MarkerOptions().position(coords).title(title).icon(BitmapDescriptorFactory.defaultMarker(color)));
    }

    private void setInitialMarkers(){
        mMusei = new ArrayList<>();
        setInitialZoomMap();
        mMusei.add(addMarker(new LatLng(45.070935, 7.685048),"Centro Musei",BitmapDescriptorFactory.HUE_RED));
    }

    private void filtriMarker(){
        Button btnMusei = findViewById(R.id.btnMusei);
        Button btnCinema = findViewById(R.id.btnCinema);
        Button btnRisto = findViewById(R.id.btnRistoranti);
        ArrayList<Marker> mCinema = new ArrayList<>();
        ArrayList<Marker> mRisto = new ArrayList<>();

        btnMusei.setOnClickListener(view -> {
            setInitialZoomMap();
            mMusei.add(addMarker(new LatLng(45.070935, 7.685048),"Centro Musei",BitmapDescriptorFactory.HUE_RED));
            removeMarkers(mCinema);
            removeMarkers(mRisto);
        });

        btnCinema.setOnClickListener(view -> {
            setInitialZoomMap();
            mCinema.add(addMarker(new LatLng(45.070935, 7.685048),"Centro Cinema",BitmapDescriptorFactory.HUE_ORANGE));
            mCinema.add(addMarker(new LatLng(45.107147, 7.678741),"Centro Cinema",BitmapDescriptorFactory.HUE_ORANGE));
            removeMarkers(mRisto);
            removeMarkers(mMusei);
        });

        btnRisto.setOnClickListener(view -> {
            setInitialZoomMap();
            mRisto.add(addMarker(new LatLng(45.044114, 7.664933),"Centro Ristoranti",BitmapDescriptorFactory.HUE_GREEN));
            removeMarkers(mCinema);
            removeMarkers(mMusei);
        });
    }

    private void removeMarkers(ArrayList<Marker> markers){
        for(Marker m : markers){
            m.remove();
        }
    }*/

    private void setInitialZoomMap(){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.070935, 7.685048), (float) 11));
        mMap.setMinZoomPreference(11);
    }

    private void circoscrizioneTorino(){
        String[] coordinate = metodoLetturaCoordinate().split(";");
        List<LatLng> latlngs = new ArrayList<>();
        for (String s : coordinate) {
            String[] LatLng = s.split(",");
            latlngs.add(new LatLng(Double.parseDouble(LatLng[1]), Double.parseDouble(LatLng[0])));
        }
        PolylineOptions rectOptions = new PolylineOptions().addAll(latlngs);
        rectOptions.color(Color.RED);
        rectOptions.width(8);
        mMap.addPolyline(rectOptions);
        setInitialZoomMap();
    }

    private void visualizzaMappa(){
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        fm.beginTransaction().replace(R.id.map2,mapFragment).commit();
        mapFragment.getMapAsync(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
    }

    //Si utilizza questa funzione per prendere la larghezza e la lunghezza della toolbar quando finisce di creare la view
    private void treeObserve(){
        ViewTreeObserver vto = mAppBarLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mAppBarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int height = mAppBarLayout.getMeasuredHeight();
                mToolbarArcBackground.setHeight(height);
            }
        });
    }

    private void toolbar(){
        //Collego la toolbar al relativo toolbar del xml
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("TOurist");
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                float scale = (float) Math.abs(verticalOffset) / scrollRange;
                mToolbarArcBackground.setScale(1 - scale);

            }
        });
    }

    // Questa funzione converte un flusso di dati, restituendolo in una stringa dove all'interno vi è il contenuto del file txt
    public String metodoLetturaCoordinate(){
        try {
            InputStream is = getAssets().open("turinCoordinates.txt");
            int size = is.available();
            byte[] buffer = new byte[size];

            //noinspection ResultOfMethodCallIgnored
            is.read(buffer);
            is.close();

            return new String(buffer);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }











    /* AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaaa */







/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (client == null) {
                        bulidGoogleApiClient();
                    }
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
            }
        }

    }

    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastlocation = location;
        if(currentLocationmMarker != null) {
            currentLocationmMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
    }

    public void onClick(View v) {
        Object[] dataTransfer = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        String url;

        switch(v.getId()) {
            case R.id.btnMusei:
                mMap.clear();
                circoscrizioneTorino();
                url = getUrl("museum");
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapActivity.this, "Showing Nearby Museum", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnCinema:
                mMap.clear();
                circoscrizioneTorino();
                url = getUrl("movie_theater");
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapActivity.this, "Showing Nearby Cinema", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnRistoranti:
                mMap.clear();
                circoscrizioneTorino();
                url = getUrl("restaurant");
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapActivity.this, "Showing Nearby Restaurants", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private String getUrl(String nearbyPlace) {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=").append(lat).append(",").append(lon);
        googlePlaceUrl.append("&radius=").append(PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type=").append(nearbyPlace);
        googlePlaceUrl.append("&keyword=");
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=").append(getResources().getString(R.string.google_maps_key));

        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

*/
    public boolean checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,new String[] {ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this,new String[] {ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;
        } else {
            return true;
        }

    }
/*
    @Override
    public void onConnectionSuspended(int i) { }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }
*/
}
