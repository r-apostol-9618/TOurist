package com.its.tourist;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;


import com.google.android.material.appbar.AppBarLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Aspetto 5 secondi e passo alla nuova activity
        new Handler().postDelayed(new Runnable() {
            public void run() {

                //Per poterci inserire all'interno della view il logo prima dell'avvio dei fragment
                FrameLayout main = findViewById(R.id.frame_main);
                main.setVisibility(View.VISIBLE);

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_main, new BudgetFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }, 3000);


    }
}