package com.example.myapplication.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.example.myapplication.R;
import com.example.myapplication.model.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class EditTaskFragment extends Fragment {
    TextInputLayout inputDateLayout;
    TextInputLayout inputTimeLayout;
    TextInputLayout inputTitleLayout;
    TextInputLayout inputDetailLayout;
    TextInputLayout inputLocationLayout;
    TextInputLayout inputPartnerLayout;
    MaterialDatePicker datePicker;
    MaterialTimePicker timePicker;
    FragmentManager fragmentManager;
    TextView textView;
    TextView cancel_edit;

    CircularProgressIndicator circularProgressIndicator;
    Button editTaskDone;

    String token;

    Long dateTimeStamp = 0L;
    Long hourTimeStamp = 0L;

    String exTitle;
    String exDetail;
    String exLocation;
    String exPartner;

    int id;

    Task currentTask;

    public EditTaskFragment(Task currentTask, int id) {
        this.currentTask = currentTask;
        this.id = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentManager = getFragmentManager();
        return inflater.inflate(R.layout.fragment_create_task, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Edit Task");

        inputTitleLayout = view.findViewById(R.id.addTask_title);
        inputDetailLayout = view.findViewById(R.id.addTask_detail);
        inputDateLayout = view.findViewById(R.id.addTask_date);
        inputTimeLayout = view.findViewById(R.id.addTask_time);
        inputLocationLayout = view.findViewById(R.id.addTask_location);
        inputPartnerLayout = view.findViewById(R.id.addTask_partner);
        textView = view.findViewById(R.id.update_error);
        cancel_edit = view.findViewById(R.id.cancel_add_button);
        editTaskDone = view.findViewById(R.id.addTask_done);
        editTaskDone.setText("Edit Done");


        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("E dd MMMM yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        @SuppressLint("SimpleDateFormat") DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

        String dateText = dateFormat.format(new Date(Long.parseLong(currentTask.getTaskDate()) * 1000));
        String timeText = timeFormat.format(new Date(Long.parseLong(currentTask.getTaskDate()) * 1000));

        MaterialDatePicker.Builder datePickerBuilder = MaterialDatePicker.Builder.datePicker();
        datePickerBuilder.setTitleText("Select date");
        datePickerBuilder.setSelection(Long.parseLong(currentTask.getTaskDate()) * 1000);
        datePicker = datePickerBuilder.build();

        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(Integer.parseInt(timeText.split(":")[0]))
                .setMinute(Integer.parseInt(timeText.split(":")[1]))
                .build();


        exTitle = currentTask.getTaskTitle();
        exDetail = !currentTask.getTaskDetail().equals("null") ? currentTask.getTaskDetail() : "";
        exLocation = !currentTask.getLocation().equals("null") ? currentTask.getLocation() : "";
        exPartner = !currentTask.getPartner().equals("null") ? currentTask.getPartner() : "";

        inputTitleLayout.getEditText().setText(exTitle);
        inputDateLayout.getEditText().setText(dateText);
        inputTimeLayout.getEditText().setText(timeText);
        inputDetailLayout.getEditText().setText(exDetail);
        inputLocationLayout.getEditText().setText(exLocation);
        inputPartnerLayout.getEditText().setText(exPartner);

        circularProgressIndicator = view.findViewById(R.id.addTask_progress);

        hourTimeStamp = Long.parseLong(currentTask.getTaskDate()) % 86400;
        dateTimeStamp = Long.parseLong(currentTask.getTaskDate()) - hourTimeStamp;


        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(selection);
                dateTimeStamp = selection / 1000;
                @SuppressLint("SimpleDateFormat") String dateText = new SimpleDateFormat("E dd MMMM yyyy").format(calendar.getTime());
                inputDateLayout.getEditText().setText(dateText);
            }
        });

        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                hourTimeStamp = (long) (timePicker.getHour() * 60 * 60 + timePicker.getMinute() * 60);
                inputTimeLayout.getEditText().setText("" + timePicker.getHour() + ":" + timePicker.getMinute());
            }
        });

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

        cancel_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment(new HomeFragment());
            }
        });

        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        token = prefs.getString("token", "No name defined");

    }

    @Override
    public void onStart() {
        super.onStart();
        editTaskDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskTitle = inputTitleLayout.getEditText().getText().toString();
                String taskDetail = inputDetailLayout.getEditText().getText().toString();
                String taskPartner = inputPartnerLayout.getEditText().getText().toString();
                String taskLocation = inputLocationLayout.getEditText().getText().toString();
                String taskDate = inputDateLayout.getEditText().getText().toString();
                String taskTime = inputTimeLayout.getEditText().getText().toString();

                boolean taskCheck = true;
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

                long total = dateTimeStamp + hourTimeStamp;
                boolean isTaskUpdate = false;
                if (taskCheck) {
                    if (!taskTitle.equals(exTitle)) {
                        isTaskUpdate = true;
                    } else if (!currentTask.getTaskDate().equals(total + "")) {
                        isTaskUpdate = true;
                    } else if (!taskDetail.equals(exDetail)) {
                        isTaskUpdate = true;
                    } else if (!taskLocation.equals(exLocation)) {
                        isTaskUpdate = true;
                    } else if (!taskPartner.equals(exPartner)) {
                        isTaskUpdate = true;
                    }
                }
                if (isTaskUpdate) {
                    textView.setVisibility(View.GONE);
                } else {
                    textView.setVisibility(View.VISIBLE);
                }

                if (isTaskUpdate && taskCheck) {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("id", id);
                        jsonObject.put("title", taskTitle);
                        jsonObject.put("detail", taskDetail);
                        jsonObject.put("date", total);
                        jsonObject.put("partner", taskPartner);
                        jsonObject.put("location", taskLocation);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    circularProgressIndicator.setVisibility(CircularProgressIndicator.VISIBLE);
                    editTaskDone.setVisibility(MaterialButton.GONE);
                    dateTimeStamp += hourTimeStamp;
                    createTask(jsonObject);
                } else {
                    circularProgressIndicator.setVisibility(CircularProgressIndicator.GONE);
                    editTaskDone.setVisibility(MaterialButton.VISIBLE);
                }
            }
        });
    }

    private void createTask(JSONObject jsonObject) {
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()).getApplicationContext());
        String url = getString(R.string.url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "task_update", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                circularProgressIndicator.setVisibility(CircularProgressIndicator.GONE);
                editTaskDone.setVisibility(MaterialButton.VISIBLE);
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

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public byte[] getBody() {
                return jsonObject.toString().getBytes(StandardCharsets.UTF_8);
            }
        };

        queue.add(jsonObjectRequest);
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    ;

}
