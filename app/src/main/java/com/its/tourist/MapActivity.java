package com.its.tourist;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.libraries.places.api.Places;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ToolbarArcBackground mToolbarArcBackground;
    private AppBarLayout mAppBarLayout;
    private GoogleMap mMap;
    private ArrayList<Marker> mMusei;

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
    }

    private void provaPlaces() {

        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyDdpiYU6WSZpgzlB9d2wai983kwqAjBNXM");//DA MODIFICARE

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        //ID BERNINI 8218

        // Define a Place ID.
        String placeId = "ChIJtVWx_gFtiEcR7ptfEFvH3lw";

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();

        // Add a listener to handle the response.
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            Log.i("Bernini", "Place found: " + place.getName());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e("NoBernini", "Place not found: " + exception.getMessage());
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        setInitialZoomMap();
        setInitialMarkers();
        filtriMarker();

        //Circoscrizione Torino
        circoscrizioneTorino();
        provaPlaces();


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
    }

    private void setInitialZoomMap(){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.070935, 7.685048), (float) 11));
        mMap.setMinZoomPreference(11);
    }

    private void circoscrizioneTorino(){
        //splitto per ";"
        String[] coordinate = metodoLetturaCoordinate().split(";");
        //istanzio l'array list
        List<LatLng> latlngs = new ArrayList<>();

        //ciclo for fino alla fine del array per aggiungere latitudine e longitude
        for (String s : coordinate) {
            String[] LatLng = s.split(",");
            latlngs.add(new LatLng(Double.parseDouble(LatLng[1]), Double.parseDouble(LatLng[0])));
        }
        //disegno tutti i poligoni grazie alla lista
        PolylineOptions rectOptions = new PolylineOptions().addAll(latlngs);
        rectOptions.color(Color.RED);
        rectOptions.width(8);
        mMap.addPolyline(rectOptions);
    }

    private void visualizzaMappa(){
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        fm.beginTransaction().replace(R.id.map2,mapFragment).commit();
        mapFragment.getMapAsync(this);
    }


    private void treeObserve(){
        //Tree Observe Listener per prendere la larghezza e la lunghezza della toolbar quando finisce di creare la view
        ViewTreeObserver vto = mAppBarLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mAppBarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width  = mAppBarLayout.getMeasuredWidth();
                int height = mAppBarLayout.getMeasuredHeight();

                //Mando i dati alla ToolbarArcBackground class per gestire la posizione del sole

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

    public String metodoLetturaCoordinate(){
        try {
            InputStream is = getAssets().open("turinCoordinates.txt");
            int size = is.available();

            // Legge tutto l'asset e lo mette in un buffer
            byte[] buffer = new byte[size];
            //noinspection ResultOfMethodCallIgnored
            is.read(buffer);
            is.close();

            // Converte il buffer in una stringa dove all'interno vi è il contenuto del file txt, restituendola a fine metodo
            return new String(buffer);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
