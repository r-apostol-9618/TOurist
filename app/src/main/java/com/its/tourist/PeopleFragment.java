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

import java.util.Objects;


public class PeopleFragment extends Fragment {

    public PeopleFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_people, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GlobalVariable global = GlobalVariable.getInstance();
        global.setBackPeople(false);

        chooseImageGroup();

    }

    private void chooseImageGroup(){
        ImageView imgSingolo = Objects.requireNonNull(getView()).findViewById(R.id.imgViewSingolo);
        imgSingolo.setOnClickListener(v -> {
            toTime();
        });

        ImageView imgCoppia = getView().findViewById(R.id.imgViewCoppia);
        imgCoppia.setOnClickListener(v -> {
            toTime();
        });

        ImageView imgGruppo = getView().findViewById(R.id.imgViewGruppo);
        imgGruppo.setOnClickListener(v -> {
            toTime();
        });
    }


    private void toTime() {
        assert getFragmentManager() != null;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_main, new TimeFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}


/*
* Cose da fare:
*
* 1) Inserire le immagini
*
* */
