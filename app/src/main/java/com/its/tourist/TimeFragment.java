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
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class TimeFragment extends Fragment {

    private Calendar myCalendar;

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

        setDateCalendar();

        TextView txtStartTime = Objects.requireNonNull(getView()).findViewById(R.id.timeBegin);
        TextView txtEndTime = getView().findViewById(R.id.timeEnd);
        setTime(txtStartTime);
        setTime(txtEndTime);

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
        TextView txtCalendar = Objects.requireNonNull(getView()).findViewById(R.id.txtData);
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
            Intent intent = new Intent(getActivity(), MapActivity.class);
            startActivity(intent);
        });
    }

}

/*
* Cose da fare:
*
* 1) Verificare che le info inserite siano correte
*
* */
