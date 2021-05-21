package com.example.myapplication.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class CreateTaskFragment extends Fragment {
    TextInputLayout inputDateLayout;
    TextInputLayout inputTimeLayout;
    TextInputLayout inputTitleLayout;
    TextInputLayout inputDetailLayout;
    TextInputLayout inputLocationLayout;
    TextInputLayout inputPartnerLayout;
    MaterialDatePicker datePicker;
    MaterialTimePicker timePicker;
    FragmentManager fragmentManager;

    TextView cancel_add;
    CircularProgressIndicator circularProgressIndicator;
    Button addTaskDone;

    String token;


    Long dateTimeStamp = 0l;
    Long hourTimeStamp = 0l;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MaterialDatePicker.Builder datePickerBuilder = MaterialDatePicker.Builder.datePicker();
        datePickerBuilder.setTitleText("Select date");
        datePicker = datePickerBuilder.build();

        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(10)
                .build();

        fragmentManager = getFragmentManager();
        return inflater.inflate(R.layout.fragment_create_task, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(R.string.toolBarAddTask);

        inputTitleLayout = view.findViewById(R.id.addTask_title);
        inputDetailLayout = view.findViewById(R.id.addTask_detail);
        inputDateLayout = view.findViewById(R.id.addTask_date);
        inputTimeLayout = view.findViewById(R.id.addTask_time);
        inputLocationLayout = view.findViewById(R.id.addTask_location);
        inputPartnerLayout = view.findViewById(R.id.addTask_partner);
        cancel_add = view.findViewById(R.id.cancel_add_button);

        addTaskDone = view.findViewById(R.id.addTask_done);
        circularProgressIndicator = view.findViewById(R.id.addTask_progress);

        inputDateLayout.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (!datePicker.isAdded()) {
                        datePicker.show(fragmentManager, null);
                    }
                    ;
                }
            }
        });
        inputDateLayout.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!datePicker.isAdded()) {
                    datePicker.show(fragmentManager, null);
                }
                ;
            }
        });

        inputTimeLayout.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (!timePicker.isAdded()) {
                        timePicker.show(fragmentManager, null);
                    }
                    ;
                }
            }
        });

        inputTimeLayout.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timePicker.isAdded()) {
                    timePicker.show(fragmentManager, null);
                }
                ;
            }
        });

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(selection);
                dateTimeStamp = selection / 1000;
                Log.d("test", "onClick: " + dateTimeStamp);
                String dateText = new SimpleDateFormat("E dd MMMM yyyy").format(calendar.getTime());
                inputDateLayout.getEditText().setText(dateText);
            }
        });

        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hourTimeStamp = (long) (timePicker.getHour() * 60 * 60 + timePicker.getMinute() * 60);
                Log.d("test", "onClick: " + hourTimeStamp);
                inputTimeLayout.getEditText().setText("" + timePicker.getHour() + ":" + timePicker.getMinute());
            }
        });

        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        token = prefs.getString("token", "No name defined");

        cancel_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment(new HomeFragment());
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        addTaskDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskTitle = inputTitleLayout.getEditText().getText().toString();
                String taskDetail = inputDetailLayout.getEditText().getText().toString();
                String taskPartner = inputPartnerLayout.getEditText().getText().toString();
                String taskLocation = inputLocationLayout.getEditText().getText().toString();
                String taskDate = inputDateLayout.getEditText().getText().toString();
                String taskTime = inputTimeLayout.getEditText().getText().toString();

                Boolean taskCheck = true;

                if (taskTitle.equals("")) {
                    inputTitleLayout.setError("Require field");
                    inputTitleLayout.setErrorEnabled(true);
                    taskCheck = false;
                } else {
                    inputTitleLayout.setErrorEnabled(false);
                }

                if (taskDate.equals("")) {
                    inputDateLayout.setError("Require field");
                    inputDateLayout.setErrorEnabled(true);
                    taskCheck = false;
                } else {
                    inputDateLayout.setErrorEnabled(false);
                }

                if (taskTime.equals("")) {
                    inputTimeLayout.setError("Require field");
                    inputTimeLayout.setErrorEnabled(true);
                    taskCheck = false;
                } else {
                    inputTimeLayout.setErrorEnabled(false);
                }

                if (taskCheck) {
                    inputTitleLayout.getEditText().setText("");
                    inputDateLayout.getEditText().setText("");
                    inputTimeLayout.getEditText().setText("");

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("title", taskTitle);
                        jsonObject.put("detail", taskDetail);
                        jsonObject.put("date", dateTimeStamp += hourTimeStamp);
                        jsonObject.put("partner", taskPartner);
                        jsonObject.put("location", taskLocation);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    circularProgressIndicator.setVisibility(CircularProgressIndicator.VISIBLE);
                    addTaskDone.setVisibility(MaterialButton.GONE);
                    dateTimeStamp += hourTimeStamp;
                    Log.d("test", "onClick: " + dateTimeStamp);
                    createTask(jsonObject);
                } else {
                    circularProgressIndicator.setVisibility(CircularProgressIndicator.GONE);
                    addTaskDone.setVisibility(MaterialButton.VISIBLE);
                }
            }
        });
    }

    private void createTask(JSONObject jsonObject) {
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()).getApplicationContext());
        String url = getString(R.string.url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "task_create", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                inputTitleLayout.getEditText().setText("");
                inputDateLayout.getEditText().setText("");
                inputTimeLayout.getEditText().setText("");
                inputLocationLayout.getEditText().setText("");
                inputDetailLayout.getEditText().setText("");
                inputPartnerLayout.getEditText().setText("");
                dateTimeStamp = 0L;
                hourTimeStamp = 0L;
                circularProgressIndicator.setVisibility(CircularProgressIndicator.GONE);
                addTaskDone.setVisibility(MaterialButton.VISIBLE);
                showFragment(new HomeFragment());
                Log.d("test", "onResponse: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("test", "onResponse: " + error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Accept", "application/json");
                header.put("Content-Type", "application/json; charset=UTF-8");
                header.put("Authorization", "Bearer " + token);

                return header;
            }

            @Override
            public byte[] getBody() {
                try {
                    return jsonObject.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            jsonObject, "utf-8");
                    return null;
                }
            }
        };

        queue.add(jsonObjectRequest);
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    };
}
