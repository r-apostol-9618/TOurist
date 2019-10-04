package com.its.tourist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class PeopleFragment extends Fragment {

    private ImageView imgSingolo;
    private ImageView imgCoppia;
    private ImageView imgGruppo;

    public PeopleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_people, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Gestione Singolo

        //imgSingolo.setClickable(true);

        imgSingolo = getView().findViewById(R.id.imgViewSingolo);
        imgSingolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newFragment();
            }
        });
        //Gestione Coppia

        //imgCoppia.setClickable(true);

        imgCoppia = getView().findViewById(R.id.imgViewCoppia);
        imgCoppia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newFragment();
            }
        });
        //Gestione Gruppo

        //imgGruppo.setClickable(true);

        imgGruppo = getView().findViewById(R.id.imgViewGruppo);
        imgGruppo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newFragment();
            }
        });

    }

    private void newFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_main, new TimeFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
