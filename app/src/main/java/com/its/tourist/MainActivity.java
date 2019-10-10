package com.its.tourist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private GlobalVariable back=GlobalVariable.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        back.setBackMain(false);

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


        if(getIntent().getBooleanExtra("Exit", false)){
            finish();
        }


    }

    @Override
    public void onBackPressed() {
        if(back.getBackMain() || back.getBackPeople()) {
            super.onBackPressed();
        }else{
            new AlertDialog.Builder(this).setTitle("Chiudi").setMessage("Sei sicuro di voler uscire?").setPositiveButton("ESCI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).setNegativeButton("ANNULLA", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            }).show();
        }
    }

}