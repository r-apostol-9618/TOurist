package com.its.tourist;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.AppBarLayout;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ToolbarArcBackground mToolbarArcBackground;
    private AppBarLayout mAppBarLayout;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);

        GlobalVariable global = GlobalVariable.getInstance();
        global.setBackPeople(true);
        global.setHandlerBudget(true);

        mToolbarArcBackground = findViewById(R.id.toolbarArcBackground);
        mAppBarLayout = findViewById(R.id.appbar);

        treeObserve();
        toolbar();
        getWindow().getDecorView().post(() -> mToolbarArcBackground.startAnimate());

        FragmentManager fm = getSupportFragmentManager();
        mapFragment = SupportMapFragment.newInstance();
        fm.beginTransaction().replace(R.id.map2,mapFragment).commit();
        if(mapFragment!=null)
        {
            mapFragment.getMapAsync(this);
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
                }).setNegativeButton("ANNULLA", (dialogInterface, i) -> { }).show();
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

                mToolbarArcBackground.setWidth(width);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {


        Log.d("tag2", "ciao");
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng turin = new LatLng(-45.07120845, 7.686839780486022);
        mMap.addMarker(new MarkerOptions().position(turin).title("Marker in Turin"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(turin));
    }

}