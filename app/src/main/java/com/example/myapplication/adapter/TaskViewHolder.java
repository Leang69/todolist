package com.example.myapplication.adapter;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.model.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TaskViewHolder extends RecyclerView.ViewHolder
{

    public TextView taskTitle;
    public TextView taskDate;
    public MaterialCheckBox delete;
    public CircularProgressIndicator circularProgressIndicator;


    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);
        taskTitle = itemView.findViewById(R.id.taskName);
        taskDate = itemView.findViewById(R.id.taskDate);
        delete = itemView.findViewById(R.id.task_delete);
        circularProgressIndicator = itemView.findViewById(R.id.delete_progress);
    }

}


