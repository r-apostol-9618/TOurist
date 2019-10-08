package com.its.tourist;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;

public class MapActivity extends AppCompatActivity {

    // Inizio gestione Toolbar

    //Istanzio ToolbarArcBackground e AppbarLayout
    ToolbarArcBackground mToolbarArcBackground;
    AppBarLayout mAppBarLayout;
    // Gestione Meteo (Gradi)
    TextView currentTemperatureField;
    //Fine gestione Toolbar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Questa solo nella MainActivity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getSupportActionBar().hide();

        setContentView(R.layout.activity_map);


        // Inizio gestione Toolbar

        // Gestione Meteo Gradi
        //currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);

        // Gestione Toolbar
        mToolbarArcBackground = (ToolbarArcBackground) findViewById(R.id.toolbarArcBackground);

        //Tree Observe Listener per prendere la larghezza e la lunghezza della toolbar quando finisce di creare la view
        final AppBarLayout layout = findViewById(R.id.appbar);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width  = layout.getMeasuredWidth();
                int height = layout.getMeasuredHeight();
                //Mando i dati alla ToolbarArcBackground class per gestire la posizione del sole
                //Intent i = new Intent(MainActivity.this, ToolbarArcBackground.class);
                //i.putExtra("deviceWidth", width);

                mToolbarArcBackground.setWidth(width);
                mToolbarArcBackground.setHeight(height);
            }
        });





        //Collego la toolbar al relativo toolbar del xml
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        setTitle("TOurist");

        mAppBarLayout = findViewById(R.id.appbar);

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
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                mToolbarArcBackground.startAnimate();
            }
        });
    }
}