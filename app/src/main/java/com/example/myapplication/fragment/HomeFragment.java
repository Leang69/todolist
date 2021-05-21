package com.example.myapplication.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.adapter.TaskAdapter;
import com.example.myapplication.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    FloatingActionButton addTask;
    String token;
    ArrayList<Task> tasks = new ArrayList<>();
    RecyclerView recyclerView;
    TaskAdapter adapter;
    CircularProgressIndicator circularProgressIndicator;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Todo List");

        addTask = view.findViewById(R.id.btn_addTask);
        circularProgressIndicator = view.findViewById(R.id.show_progress);
        circularProgressIndicator.setVisibility(CircularProgressIndicator.VISIBLE);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment(new CreateTaskFragment());
            }
        });

        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        token = prefs.getString("token", "No name defined");


        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TaskAdapter(tasks,token);
        recyclerView.setAdapter(adapter);
        showTask(tasks);



    };


    private void showTask(ArrayList<Task> task) {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = getString(R.string.url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + "task_show", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = response.getJSONArray("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                };
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject tmp = null;
                        try {
                            tmp = jsonArray.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            int id = tmp.getInt("id");
                            String title = tmp.getString("title");
                            String date = tmp.getString("date");
                            String location = tmp.getString("location");
                            String detail = tmp.getString("detail");
                            String partner = tmp.getString("partner");
                            task.add(new Task(id, title, date, location, detail, partner));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    adapter.notifyDataSetChanged();
                    circularProgressIndicator.setVisibility(CircularProgressIndicator.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                circularProgressIndicator.setVisibility(CircularProgressIndicator.GONE);
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
