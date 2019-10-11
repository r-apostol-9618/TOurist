package com.its.tourist;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;

public class MapActivity extends AppCompatActivity {

    private ToolbarArcBackground mToolbarArcBackground;
    private AppBarLayout mAppBarLayout;

    // Gestione Meteo (Gradi)
    private TextView currentTemperatureField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);

        GlobalVariable global = GlobalVariable.getInstance();
        global.setBackPeople(true);

        currentTemperatureField = findViewById(R.id.current_temperature_field);
        mToolbarArcBackground = findViewById(R.id.toolbarArcBackground);
        mAppBarLayout = findViewById(R.id.appbar);

        treeObserve();
        toolbar();
        getWindow().getDecorView().post(() -> mToolbarArcBackground.startAnimate());

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
                //Intent i = new Intent(MainActivity.this, ToolbarArcBackground.class);
                //i.putExtra("deviceWidth", width);

                mToolbarArcBackground.setWidth(width);
                mToolbarArcBackground.setHeight(height);
            }
        });
    }

    private void toolbar(){
        //Collego la toolbar al relativo toolbar del xml
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //final ActionBar ab = getSupportActionBar();
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

}