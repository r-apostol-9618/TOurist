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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.jem.rubberpicker.RubberRangePicker;

import java.util.Objects;

public class BudgetFragment extends Fragment {

    private TextView seekbarEnd,seekbarStart;

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
        global.setBackPeople(false);

        gestionePicker();
        toTime();
        freeBudget();

    }


    private void gestionePicker(){
        //seekbar
        RubberRangePicker rubberRangePicker = Objects.requireNonNull(getView()).findViewById(R.id.seekbar);
        // textView seekbar start / end
        seekbarStart = getView().findViewById(R.id.txtSeekbarStart);
        seekbarEnd = getView().findViewById(R.id.txtSeekbarEnd);

        seekbarStart.setText(String.valueOf(0));
        seekbarEnd.setText(String.valueOf(0));

        //noinspection NullableProblems
        rubberRangePicker.setOnRubberRangePickerChangeListener(new RubberRangePicker.OnRubberRangePickerChangeListener() {
            @Override
            public void onProgressChanged(RubberRangePicker rubberRangePicker, int startThumbValue, int endThumbValue, boolean b) {
                //Gestione seekbar doppia
                if(b && (startThumbValue != Integer.parseInt(seekbarStart.getText().toString()) || endThumbValue != Integer.parseInt(seekbarEnd.getText().toString()))){
                    seekbarStart.setText(String.valueOf(startThumbValue));
                    seekbarEnd.setText(String.valueOf(endThumbValue));
                }

            }

            @Override
            public void onStartTrackingTouch(RubberRangePicker rubberRangePicker, boolean b) { }

            @Override
            public void onStopTrackingTouch(RubberRangePicker rubberRangePicker, boolean b) { }

        });

    }

    private void toTime(){
        Button avanti = Objects.requireNonNull(getView()).findViewById(R.id.btnAvanti);
        avanti.setOnClickListener(v -> {
            if(errorRangeValue()) {
                toTimeFragment();
            }else{
                messageErrorSnack();
            }
        });
    }

    private void freeBudget(){
        Button free = Objects.requireNonNull(getView()).findViewById(R.id.btnFree);
        free.setOnClickListener(v -> {
            toTimeFragmentWithFree("free");
        });
    }

    private void toTimeFragment(){
        assert getFragmentManager() != null;
        Bundle bundle = new Bundle();
        TimeFragment timeFragment = new TimeFragment();
        bundle.putInt("startBudget",Integer.parseInt(seekbarStart.getText().toString()));
        bundle.putInt("endBudget",Integer.parseInt(seekbarEnd.getText().toString()));
        timeFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_main, timeFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /* ho aggiunto solo questo pezzo per memorizzare se l'utente preme sul bottone Free o meno e cambiare il fragment dopo averlo premuto.
    *   se esiste un modo più semplice cambia pure */
    private void toTimeFragmentWithFree(String txtFree) {
        assert getFragmentManager() != null;
        Bundle bundle = new Bundle();
        TimeFragment timeFragment = new TimeFragment();
        bundle.putString("isFree", txtFree);
        timeFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_main, timeFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private boolean errorRangeValue(){
        return Integer.parseInt(seekbarEnd.getText().toString())>0 && !(seekbarStart.getText().toString().equals(seekbarEnd.getText().toString()));
    }

    private void messageErrorSnack(){
        FrameLayout budgetFrame = Objects.requireNonNull(getView()).findViewById(R.id.budgetFrame);
        Snackbar.make(budgetFrame,"Inserisci un range di valori valido!",Snackbar.LENGTH_LONG).show();
    }


}
