package com.its.tourist;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class TimeFragment extends Fragment {

    private Calendar myCalendar;
    private TextView txtCalendar,txtStartTime,txtEndTime;

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

        txtCalendar = Objects.requireNonNull(getView()).findViewById(R.id.txtData);
        txtStartTime = getView().findViewById(R.id.timeBegin);
        txtEndTime = getView().findViewById(R.id.timeEnd);

        disableDalleAlle();
        setTime(txtStartTime);
        setTime(txtEndTime);
        setDateCalendar();

        toMap();

    }

    private void setDateCalendar(){
        myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateLabelCalendar();
        };

        ImageView calendar = Objects.requireNonNull(getView()).findViewById(R.id.imgViewCalendar);
        calendar.setOnClickListener(view ->
                new DatePickerDialog(Objects.requireNonNull(getActivity()), date,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

    }

    private void updateLabelCalendar(){
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALIAN);
        txtCalendar.setText(sdf.format(myCalendar.getTime()));
    }

    private void setTime(TextView txtTime){
        txtTime.setOnClickListener(view -> {
            int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = myCalendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), (timePicker, selectedHour, selectedMinute) -> {
                @SuppressLint("DefaultLocale") String textTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                txtTime.setText(textTime);
            },hour,minute,true);
            timePickerDialog.show();
        });
    }

    private void toMap(){
        Button avanti = Objects.requireNonNull(getView()).findViewById(R.id.btnAvanti3);
        avanti.setOnClickListener(v -> {
            if(isEmptyDate()){
                messageErrorSnack("Inserisci una data valida!");
            }else if(timeRangeError()){
                messageErrorSnack("Inserisci un range temporale valido!");
            }else{
                toMapActivity();
            }
        });
    }

    private void toMapActivity(){
        Bundle bundle = this.getArguments();
        GlobalVariable globalVariable = GlobalVariable.getInstance();
        if(bundle != null){
            globalVariable.setBudgetStart(bundle.getInt("startBudgetPeople"));
            globalVariable.setBudgetEnd(bundle.getInt("endBudgetPeople"));
            globalVariable.setTypePerson(bundle.getString("numberOfPeople"));
        }
        if (this.getArguments() != null){
            bundle.putInt("startBudgetPeople",this.getArguments().getInt("startBudget"));
            bundle.putInt("endBudgetPeople",this.getArguments().getInt("endBudget"));
        }
        globalVariable.setCalendarDay(txtCalendar.getText().toString());
        globalVariable.setTimeStart(txtStartTime.getText().toString());
        globalVariable.setTimeEnd(txtEndTime.getText().toString());
        Intent intent = new Intent(getActivity(), MapActivity.class);
        startActivity(intent);
    }

    private boolean isEmptyDate(){
        return txtCalendar.getText().toString().equals("gg/mm/aaaa");
    }

    private boolean timeRangeError(){
        return txtStartTime.getText().toString().equals(txtEndTime.getText().toString());
    }

    @SuppressLint("SetTextI18n")
    private void disableDalleAlle(){
        Switch allDay = Objects.requireNonNull(getView()).findViewById(R.id.switchGiorno);
        allDay.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked){
                txtStartTime.setEnabled(false);
                txtEndTime.setEnabled(false);
                txtStartTime.setText("00:00");
                txtEndTime.setText("23:59");
            }else{
                txtStartTime.setEnabled(true);
                txtEndTime.setEnabled(true);
                txtStartTime.setText("12:00");
                txtEndTime.setText("12:00");
            }
        });
    }

    private void messageErrorSnack(String txt){
        FrameLayout timeFrame = Objects.requireNonNull(getView()).findViewById(R.id.timeFrame);
        Snackbar.make(timeFrame,txt,Snackbar.LENGTH_LONG).show();
    }

}
