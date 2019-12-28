package com.its.tourist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.appbar.AppBarLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ToolbarArcBackground mToolbarArcBackground;
    private AppBarLayout mAppBarLayout;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private Location mLastLocation;
    private LocationCallback locationCallback;
    private View mapView;
    private GlobalVariable global;

    private final float DEFAULT_ZOOM = 18;

    public static String BaseUrl = "http://api.openweathermap.org/";
    public static String AppId = "c96e8bd7dcf26eab873b1b5417951ba7";
    public static String lat = "45.070935";
    public static String lon = "7.685048";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);

        global = GlobalVariable.getInstance();
        global.setBackPeople(true);
        global.setHandlerPeople(true);

        mToolbarArcBackground = findViewById(R.id.toolbarArcBackground);
        mAppBarLayout = findViewById(R.id.appbar);

        Places.initialize(this, getResources().getString(R.string.google_maps_key));

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        placesClient = Places.createClient(this);

        visualizzaMappa();
        treeObserve();
        toolbar();
        getWindow().getDecorView().post(() -> mToolbarArcBackground.startAnimate());
        getCurrentWeather();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMinZoomPreference(11);
        CustomMarkerInfoWindowView adapter = new CustomMarkerInfoWindowView(MapActivity.this);
        mMap.setInfoWindowAdapter(adapter);

        createBtnGeo();
        places();
        checkGPS();

        filtriMarker();

        circoscrizioneTorino();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }

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
                }).setNegativeButton("ANNULLA", (dialogInterface, i) -> {
        }).show();
    }

    private void visualizzaMappa () {
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        assert fm != null;
        fm.getMapAsync(this);
        mapView = fm.getView();
    }

    public String metodoLetturaCoordinate () {
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

    private void circoscrizioneTorino () {
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
    }

    private void checkGPS() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, locationSettingsResponse -> getDeviceLocation());

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                ResolvableApiException resolvable = (ResolvableApiException) e;
                try {
                    resolvable.startResolutionForResult(MapActivity.this, 51);
                } catch (IntentSender.SendIntentException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void createBtnGeo() {
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 180);
        }
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastLocation = task.getResult();
                            if (mLastLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), DEFAULT_ZOOM));
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        } else {
                            Toast.makeText(MapActivity.this, "Non è possibile trovare l'ultima posizione nota", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void places() {

        //Lista delle informazioni reperibili in un posto --POSSONO ESSERE INSERITE TUTTE QUELLE CONTENUTE IN Place.Field--
        List<Place.Field> placeFetchFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.RATING,
                Place.Field.PHOTO_METADATAS, Place.Field.PRICE_LEVEL, Place.Field.OPENING_HOURS);

        //Serve solo l'ID del posto da ricercare
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(Collections.singletonList(Place.Field.ID));

        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FindCurrentPlaceResponse response = task.getResult();
                    assert response != null;
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {

                        //Chiedo di darmi tutte le informazioni all'interno del posto con il relativo ID
                        FetchPlaceRequest requestFetch = FetchPlaceRequest.newInstance(Objects.requireNonNull(placeLikelihood.getPlace().getId()), placeFetchFields);
                        placesClient.fetchPlace(requestFetch).addOnSuccessListener((responseFetch) -> {

                            Place place = responseFetch.getPlace();
                            MarkerOptions markerOptions = new MarkerOptions();

                            //if ((place.getPriceLevel() != null) && (place.getPriceLevel() == gestioneDatiPrezzo())) {

                            markerOptions.title(place.getName());
                            markerOptions.position(Objects.requireNonNull(place.getLatLng()));

                            if(place.getRating() != null) {
                                markerOptions.snippet("Indirizzo: "+place.getAddress()+"\nRating: "+place.getRating());
                            } else {
                                markerOptions.snippet("Indirizzo: "+place.getAddress());
                            }

                            if (place.getPhotoMetadatas() != null) {
                                PhotoMetadata photoMetadata;
                                photoMetadata = place.getPhotoMetadatas().get(0);

                                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata).build();
                                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) ->
                                        mMap.addMarker(markerOptions).setTag(fetchPhotoResponse.getBitmap())
                                ).addOnFailureListener((exception) -> {
                                    if (exception instanceof ApiException) {
                                        Log.e("PlaceNotFoundPhoto", "Place not found: " + exception.getMessage());
                                    }
                                });
                            } else {
                                mMap.addMarker(markerOptions).setTag(null);
                            }

                            /*
                            List<String> open = Objects.requireNonNull(place.getOpeningHours()).getWeekdayText();

                            for(String i: open) {
                                Log.i("opening", "Opening: " + i);
                            }
                            */



                            //}


                        }).addOnFailureListener((exception) -> {
                            if (exception instanceof ApiException) {
                                Log.e("Fetch not found", "Place not found: " + exception.getMessage());
                            }
                        });

                    }
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        Log.e("Find not found", "Place not found: " + exception.getMessage());
                    }
                }
            });
        }
        /*else {
            //checkLocationPermission();
        }*/

    }

    private void filtriMarker () {
        Button btnMusei = findViewById(R.id.btnMusei);
        Button btnCinema = findViewById(R.id.btnCinema);
        Button btnRisto = findViewById(R.id.btnRistoranti);

        btnMusei.setOnClickListener(view -> {

        });

        btnCinema.setOnClickListener(view -> {

        });

        btnRisto.setOnClickListener(view -> {

        });
    }

    //Si utilizza questa funzione per prendere la larghezza e la lunghezza della toolbar quando finisce di creare la view
    private void treeObserve () {
        ViewTreeObserver vto = mAppBarLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mAppBarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int height = mAppBarLayout.getMeasuredHeight();
                mToolbarArcBackground.setHeight(height);
            }
        });
    }

    private void toolbar () {
        /*
        //Collego la toolbar al relativo toolbar del xml
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        */

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

    private void getCurrentWeather() {

        Log.d("giusto", "debug");

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BaseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getCurrentWeatherData(lat, lon, AppId);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {

                if (response.code() == 200) {
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;

                    @SuppressLint("DefaultLocale") String stringBuilder =
                            String.format("%.0f", kelvinToCelsius(weatherResponse.main.temp)) + "°" +
                                    "\n" +
                                    "Min: " +
                                    String.format("%.0f", kelvinToCelsius(weatherResponse.main.temp_min)) +
                                    "\n" +
                                    "Max: " +
                                    String.format("%.0f", kelvinToCelsius(weatherResponse.main.temp_max)) +
                                    "\n" +
                                    "Umidità: " + weatherResponse.main.humidity;

                    TextView txtMeteo = findViewById(R.id.txtMeteo);
                    txtMeteo.setText(stringBuilder);
                    //meteoString(stringBuilder);
                    Log.e("giusto", "meteo");

                }
            }


            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                Log.d("giusto", "errore");
                TextView txtMeteo = findViewById(R.id.txtMeteo);
                String testoBase = "Temperatura: 3, Minima: 0, Massima: 3, Umidità: 60";
                txtMeteo.setText(testoBase);
            }

        });

    }

    private double kelvinToCelsius ( double grades){
        return grades - 273.15;
    }

    // Funzione che mette i dati meteo in una TextView, ancora da modificare
    private void meteoString (String meteoInfo){
        TextView txtMeteo = findViewById(R.id.txtMeteo);
        txtMeteo.setText(meteoInfo);
    }

    private int gestioneDatiPrezzo () {
        int priceS = global.getBudgetStart();
        int priceE = global.getBudgetEnd();
        String personT = global.getTypePerson();

        if (priceE == 0) {
            return 0;
        } else if ((priceS >= 0 && priceE <= 20) && personT.equals("singolo")) {
            return 1;
        } else if ((priceS >= 0 && priceE <= 50) && personT.equals("singolo")) {
            return 2;
        } else if ((priceS >= 0 && priceE <= 100) && personT.equals("singolo")) {
            return 3;
        } else if ((priceS >= 0 && priceE <= 200) && personT.equals("singolo")) {
            return 4;
        } else if ((priceS >= 0 && priceE <= 30) && personT.equals("coppia")) {
            return 1;
        } else if ((priceS >= 0 && priceE <= 70) && personT.equals("coppia")) {
            return 2;
        } else if ((priceS >= 0 && priceE <= 120) && personT.equals("coppia")) {
            return 3;
        } else if ((priceS >= 0 && priceE <= 200) && personT.equals("coppia")) {
            return 4;
        } else if ((priceS >= 0 && priceE <= 50) && personT.equals("gruppo")) {
            return 1;
        } else if ((priceS >= 0 && priceE <= 100) && personT.equals("gruppo")) {
            return 2;
        } else if ((priceS >= 0 && priceE <= 150) && personT.equals("gruppo")) {
            return 3;
        } else if ((priceS >= 0 && priceE <= 200) && personT.equals("gruppo")) {
            return 4;
        }

        return 0;
    }

    /*public String gestioneCalendario(){

        String timeS = global.getTimeStart();
        String timeE = global.getTimeEnd();
        String timeD = global.getCalendarDay();

    }*/

}
