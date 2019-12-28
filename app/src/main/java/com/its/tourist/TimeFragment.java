package com.its.tourist;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class TimeFragment extends Fragment {

    private Calendar myCalendar;
    private TextView txtCalendar,txtStartTime,txtEndTime;
    private int hour,minute;

    public TimeFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtCalendar = Objects.requireNonNull(getView()).findViewById(R.id.txtData);
        txtStartTime = getView().findViewById(R.id.timeBegin);
        txtEndTime = getView().findViewById(R.id.timeEnd);
        myCalendar = Calendar.getInstance();
        hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        minute = myCalendar.get(Calendar.MINUTE);

        disableDalleAlle();
        setTime(txtStartTime,true);
        setTime(txtEndTime,false);
        setDateCalendar();

        toMap();

    }

    @SuppressLint("SetTextI18n")
    private void setDateCalendar(){
        txtCalendar.setText(myCalendar.get(Calendar.DAY_OF_MONTH)+"/"+myCalendar.get(Calendar.MONTH)+"/"+myCalendar.get(Calendar.YEAR));
        DatePickerDialog.OnDateSetListener date = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateLabelCalendar();
        };
        ImageView calendar = Objects.requireNonNull(getView()).findViewById(R.id.imgViewCalendar);
        calendar.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), date,
                    myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();

        });

    }

    private void updateLabelCalendar(){
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALIAN);
        txtCalendar.setText(sdf.format(myCalendar.getTime()));
    }

    @SuppressLint("DefaultLocale")
    private void setTime(TextView txtTime, boolean start){
        if (start) {
            txtTime.setText(String.format("%02d:%02d", hour, minute));
        }
        txtTime.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), (timePicker, selectedHour, selectedMinute) -> {
                String textTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                txtTime.setText(textTime);
            },hour,minute,true);
            timePickerDialog.show();
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void disableDalleAlle(){
        Switch allDay = Objects.requireNonNull(getView()).findViewById(R.id.switchGiorno);
        allDay.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked) {
                txtStartTime.setEnabled(false);
                txtEndTime.setEnabled(false);
                txtStartTime.setText("00:00");
                txtEndTime.setText("23:59");
            } else {
                txtStartTime.setEnabled(true);
                txtEndTime.setEnabled(true);
                txtStartTime.setText(String.format("%02d:%02d", hour, minute));
                txtEndTime.setText("00:00");
            }
        });
    }

    private void toMap(){
        Button avanti = Objects.requireNonNull(getView()).findViewById(R.id.btnAvanti3);
        avanti.setOnClickListener(v -> {
            if(timeRangeError()){
                Toast.makeText(getActivity(), "Inserisci un range temporale valido!", Toast.LENGTH_SHORT).show();
            }else{
                permessi();
            }
        });
    }

    private void permessi(){
        Dexter.withActivity(getActivity())
                .withPermission(ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        toMapActivity();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(response.isPermanentlyDenied()){
                            new AlertDialog
                                    .Builder(Objects.requireNonNull(getActivity()))
                                    .setTitle("Accesso ai permessi")
                                    .setMessage("L'accesso alla localizzazione è permanentemente negato. \nRecarsi nelle impostazioni per attivare il servizio")
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton("OK", (dialog, which) -> getActivity().finish())
                                    .show();
                        } else {
                            Toast.makeText(getActivity(), "Permesso negato", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void toMapActivity(){
        Bundle bundle = this.getArguments();
        GlobalVariable globalVariable = GlobalVariable.getInstance();
        assert bundle != null;
        globalVariable.setBudgetStart(bundle.getInt("startBudget"));
        globalVariable.setBudgetEnd(bundle.getInt("endBudget"));
        globalVariable.setTypePerson(bundle.getString("numberOfPeople"));
        globalVariable.setCalendarDay(txtCalendar.getText().toString());
        globalVariable.setTimeStart(txtStartTime.getText().toString());
        globalVariable.setTimeEnd(txtEndTime.getText().toString());
        Intent intent = new Intent(getActivity(), MapActivity.class);
        startActivity(intent);
    }

    private boolean timeRangeError(){
        return txtStartTime.getText().toString().equals(txtEndTime.getText().toString());
    }

}
