package com.its.tourist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private Runnable run;
    private Handler handler;
    private GlobalVariable global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

     //  setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        global = GlobalVariable.getInstance();
        toPeople();

        if(getIntent().getBooleanExtra("Exit", false)){
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        if(global.isBackPeople()) {
            new AlertDialog.Builder(this).setTitle("Chiudi").setMessage("Sei sicuro di voler uscire?")
                    .setPositiveButton("ESCI", (dialogInterface, i) ->
                            finish()
                    ).setNegativeButton("ANNULLA", (dialogInterface, i) -> { }).show();
        }else{
            super.onBackPressed();
        }
    }

    private void toPeople(){
        run = () -> {
            findViewById(R.id.frame_main).setVisibility(View.VISIBLE);
            findViewById(R.id.mainIntro).setVisibility(View.GONE);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_main, new PeopleFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        };
        handler = new Handler();
        handler.postDelayed(run,3000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(run);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (global.isHandlerPeople()) {
            handler.removeCallbacks(run);
            handler.postDelayed(run, 3000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(run);
    }
}


