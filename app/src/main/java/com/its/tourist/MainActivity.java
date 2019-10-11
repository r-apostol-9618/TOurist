package com.its.tourist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private Runnable run;
    private Handler handler;
    private GlobalVariable global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        global = GlobalVariable.getInstance();
        toBudget();

        if(getIntent().getBooleanExtra("Exit", false)){
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        if(global.getBackPeople()) {
            new AlertDialog.Builder(this).setTitle("Chiudi").setMessage("Sei sicuro di voler uscire?")
                    .setPositiveButton("ESCI", (dialogInterface, i) ->
                            finish()
                    ).setNegativeButton("ANNULLA", (dialogInterface, i) -> { }).show();
        }else{
            super.onBackPressed();
        }
    }

    private void toBudget(){
        run = () -> {
            //Per poterci inserire all'interno della view il logo prima dell'avvio dei fragment
            FrameLayout main = findViewById(R.id.frame_main);
            main.setVisibility(View.VISIBLE);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_main, new BudgetFragment());
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
        if (global.getHandlerBudget()) {
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