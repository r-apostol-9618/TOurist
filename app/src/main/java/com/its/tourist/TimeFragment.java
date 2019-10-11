package com.its.tourist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Objects;

public class TimeFragment extends Fragment {

    private Button avanti;

    public TimeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toMap();

        /*
        //Gestione button calendar
        ImageView calendar = getView().findViewById(R.id.imgViewCalendar);

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_main, new CalendarFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

         */
    }

    private void toMap(){
        avanti = Objects.requireNonNull(getView()).findViewById(R.id.btnAvanti3);
        avanti.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapActivity.class);
            startActivity(intent);
        });
    }

}

/*
* Cose da fare:
*
* 1) Calendario tramite bottone (apertura di un fragment)
* 2) Verificare che la data inserita manualmente sia corretta
* 3) Avviare una selezione dell'ora alla pressione delle TextView Dalle/Alle
*
* */
