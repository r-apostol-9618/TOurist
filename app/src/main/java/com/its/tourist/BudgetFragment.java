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
    }


    private void gestionePicker(){
        //seekbar
        RubberRangePicker rubberRangePicker = Objects.requireNonNull(getView()).findViewById(R.id.seekbar);
        // textView seekbar start / end
        seekbarStart = getView().findViewById(R.id.txtSeekbarStart);
        seekbarEnd = getView().findViewById(R.id.txtSeekbarEnd);

        seekbarStart.setText(String.valueOf(0));
        seekbarEnd.setText(String.valueOf(1));

        //noinspection NullableProblems
        rubberRangePicker.setOnRubberRangePickerChangeListener(new RubberRangePicker.OnRubberRangePickerChangeListener() {
            @Override
            public void onProgressChanged(RubberRangePicker rubberRangePicker, int startThumbValue, int endThumbValue, boolean b) {
                //Gestione seekbar doppia
                if(b && (startThumbValue != Integer.parseInt(seekbarStart.getText().toString()) || endThumbValue != Integer.parseInt(seekbarEnd.getText().toString()))){
                    //Da riguardare
                    if(startThumbValue == endThumbValue || endThumbValue == 199){
                        endThumbValue += 1;
                    }
                    seekbarStart.setText(String.valueOf(startThumbValue));
                    seekbarEnd.setText(String.valueOf(endThumbValue));
                }

            }

            @Override
            public void onStartTrackingTouch(RubberRangePicker rubberRangePicker, boolean b) {

            }

            @Override
            public void onStopTrackingTouch(RubberRangePicker rubberRangePicker, boolean b) { }

        });

    }

    private void toTime(){
        Button avanti = Objects.requireNonNull(getView()).findViewById(R.id.btnAvanti);
        Button free = Objects.requireNonNull(getView()).findViewById(R.id.btnFree);
        avanti.setOnClickListener(v -> toTimeFragment(false));
        free.setOnClickListener(view -> toTimeFragment(true));
    }

    private void toTimeFragment(boolean free){
        assert getFragmentManager() != null;
        Bundle bundle = new Bundle();
        TimeFragment timeFragment = new TimeFragment();
        if(free){
            bundle.putInt("startBudget",0);
            bundle.putInt("endBudget",0);
        }else{
            bundle.putInt("startBudget",Integer.parseInt(seekbarStart.getText().toString()));
            bundle.putInt("endBudget",Integer.parseInt(seekbarEnd.getText().toString()));
        }
        timeFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_main, timeFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
