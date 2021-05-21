package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.fragment.CreateTaskFragment;
import com.example.myapplication.fragment.EditTaskFragment;
import com.example.myapplication.model.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {
    private ArrayList<Task> tasks;
    Context context;
    String token;

    public TaskAdapter(ArrayList<Task> tasks, String token) {
        this.tasks = tasks;
        this.token = token;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.view_list_holder, parent, false);
        context = parent.getContext();
        return new TaskViewHolder(itemView);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task current = tasks.get(position);
        Date date = new Date(Long.parseLong(current.getTaskDate()) * 1000);
        DateFormat format = new SimpleDateFormat("E dd MMMM yyyy HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String dataFormated = format.format(date);
        holder.taskTitle.setText(current.getTaskTitle());
        holder.taskDate.setText("Date: " + dataFormated);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment(new EditTaskFragment(current,current.getId()));
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.circularProgressIndicator.setVisibility(CircularProgressIndicator.VISIBLE);
                holder.delete.setVisibility(MaterialCheckBox.GONE);
                deleteTask(current.getId()+"", position,holder.circularProgressIndicator,holder.delete);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    private void deleteTask(String id, int position,CircularProgressIndicator circularProgressIndicator,MaterialCheckBox materialCheckBox) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "task_delete?id="+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                tasks.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, tasks.size());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("test", "token: "+token);
                Log.d("test", "delete error: "+error);
                circularProgressIndicator.setVisibility(View.GONE);
                materialCheckBox.setVisibility(MaterialCheckBox.VISIBLE);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Accept", "application/json");
                header.put("Content-Type", "application/json; charset=UTF-8");
                header.put("Authorization", "Bearer " + token);
                return header;
            }
        };
        queue.add(stringRequest);
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    };
}
