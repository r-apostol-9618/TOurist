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
        global.setHandlerBudget(false);

        gestionePicker();
        toPeople();

    }


    private void gestionePicker(){
        //seekbar
        RubberRangePicker rubberRangePicker;
        // textView seekbar start / end
        TextView seekbarStart;
        TextView seekbarEnd;

        //textView seekbar
        rubberRangePicker = getView().findViewById(R.id.seekbar);

        seekbarStart = getView().findViewById(R.id.txtSeekbarStart);
        seekbarEnd = getView().findViewById(R.id.txtSeekbarEnd);

        seekbarStart.setText(String.valueOf(0));
        seekbarEnd.setText(String.valueOf(0));

        rubberRangePicker.setOnRubberRangePickerChangeListener(new RubberRangePicker.OnRubberRangePickerChangeListener() {
            @Override
            public void onProgressChanged(RubberRangePicker rubberRangePicker, int startThumbValue, int endThumbValue, boolean b) {
                //Gestione seekbar doppia

                if(b && startThumbValue != Integer.parseInt(seekbarStart.getText().toString())){
                    seekbarStart.setText(String.valueOf(startThumbValue));
                }
                if(b && endThumbValue != Integer.parseInt(seekbarEnd.getText().toString())){
                    seekbarEnd.setText(String.valueOf(endThumbValue));
                }/*
                startThumbValue = rubberRangePicker.getCurrentStartValue();
                endThumbValue = rubberRangePicker.getCurrentEndValue();

                rubberRangePicker.setCurrentStartValue(startThumbValue);
                rubberRangePicker.setCurrentEndValue(endThumbValue);

                seekbarStart.setText(endThumbValue);
                seekbarEnd.setText(endThumbValue);*/

            }

            @Override
            public void onStartTrackingTouch(RubberRangePicker rubberRangePicker, boolean b) {

            }

            @Override
            public void onStopTrackingTouch(RubberRangePicker rubberRangePicker, boolean b) {

            }
        });

    }

    private void toPeople(){
        Button avanti = Objects.requireNonNull(getView()).findViewById(R.id.btnAvanti);

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
