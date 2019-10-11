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

import java.util.Objects;

public class BudgetFragment extends Fragment {

    private TextView txtStart,txtEnd;
    private Button avanti;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_budget, container, false);
    }

    public BudgetFragment(){ }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        GlobalVariable global = GlobalVariable.getInstance();
        global.setBackPeople(true);

        gestionePicker();
        toPeople();

    }


    private void gestionePicker(){
        RubberRangePicker rubberRangePicker = new RubberRangePicker(Objects.requireNonNull(getContext()));
        int startThumbValue = rubberRangePicker.getCurrentStartValue();
        int endThumbValue = rubberRangePicker.getCurrentEndValue();

        rubberRangePicker.setCurrentStartValue(startThumbValue + 10);
        rubberRangePicker.setCurrentEndValue(endThumbValue + 10);
        txtStart = Objects.requireNonNull(getView()).findViewById(R.id.txtSeekbarStart);
        txtEnd = getView().findViewById(R.id.txtSeekbarEnd);
        String textStart = startThumbValue+"a";
        String textEnd = endThumbValue+"b";
        txtStart.setText(textStart);
        txtEnd.setText(textEnd);
    }

    private void toPeople(){
        avanti = Objects.requireNonNull(getView()).findViewById(R.id.btnAvanti);

        avanti.setOnClickListener(v -> {
            assert getFragmentManager() != null;
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_main, new PeopleFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
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
