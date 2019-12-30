package com.its.tourist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

/**
 *
 *  TOurist
 *  Applicazione turistica per poter viaggiare e scoprire la città metropolitana di Torino
 *  @author Razvan Apostol, Simone Tugnetti, Federica Vacca
 *  @version 1.0
 *
 */
public class MainActivity extends AppCompatActivity {

    private Runnable run;
    private Handler handler;
    private GlobalVariable global;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        global = GlobalVariable.getInstance();
        toPeople();

        // Gestione dell'uscita da parte di MapActivity
        if(getIntent().getBooleanExtra("Exit", false)){
            finish();
        }

    }


    /**
     *  Metodo per la gestione del pulsante Back
     *  Nel caso in cui l'utente si trovi al primo Fragment visulizzato (PeopleFragment), apparirà un messaggio di chiusura, indietro altrimenti
     */
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


    /**
     *  Metodo per la navigazione al primo Fragment
     *  Verrà visualizzata la intro dell'applicazione per 3 secondi, per poi nasconderla e passare al Fragment
     */
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


    /**
     *  Metodo per la gestione di onStop()
     *  Nel caso in cui l'utente smetta di usare l'app, verrà rimosso il delay
     */
    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(run);
    }


    /**
     *  Metodo per la gestione di onRestart()
     *  Nel caso in cui l'utente riprenda ad utilizzare l'app durante la visualizzazione della intro, verrà reinserito il delay
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if (global.isHandlerPeople()) {
            handler.removeCallbacks(run);
            handler.postDelayed(run, 3000);
        }
    }


    /**
     *  Metodo per la gestione di onDestroy()
     *  Nel caso in cui l'utente chiuda l'app, verrà rimosso il delay
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(run);
    }

}


