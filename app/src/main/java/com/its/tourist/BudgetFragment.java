package com.its.tourist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jem.rubberpicker.RubberRangePicker;

public class BudgetFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_budget, container, false);
    }

    public BudgetFragment(){ }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){

        super.onViewCreated(view, savedInstanceState);

        GlobalVariable backPeople = GlobalVariable.getInstance();
        backPeople.setBackPeople(false);

        //gestione picker

        RubberRangePicker rubberRangePicker = new RubberRangePicker(getContext());
        int startThumbValue = rubberRangePicker.getCurrentStartValue();
        int endThumbValue = rubberRangePicker.getCurrentEndValue();


        rubberRangePicker.setCurrentStartValue(startThumbValue + 10);
        rubberRangePicker.setCurrentEndValue(endThumbValue + 10);
        TextView txtStart = getView().findViewById(R.id.txtSeekbarStart);
        TextView txtEnd = getView().findViewById(R.id.txtSeekbarEnd);
        txtStart.setText(startThumbValue+"a");
        txtEnd.setText(endThumbValue+"b");


        //fine gestione picker
        Button avanti = getView().findViewById(R.id.btnAvanti);

        avanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_main, new PeopleFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }

}

/*
 * Cose da fare:
 *
 * 1) Modificare i valori delle TextView in base alla seekbar (Prova: vedere conteggio di caratteri in tempo reale)
 * 2) Prova: Salvataggio in singleton per i 3 fragment
 *
*/
