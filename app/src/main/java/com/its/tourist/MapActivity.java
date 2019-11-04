package com.its.tourist;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.appbar.AppBarLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ToolbarArcBackground mToolbarArcBackground;
    private AppBarLayout mAppBarLayout;
    private GoogleMap mMap;

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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.070935, 7.685048), (float) 11));
        mMap.setMinZoomPreference(11);






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

    private Marker addMarker(LatLng coords, String title){
        return mMap.addMarker(new MarkerOptions().position(coords).title(title));
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
