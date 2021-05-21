package com.example.myapplication.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccountFragment extends Fragment {
    TextView login;
    TextInputLayout usernamelayout;
    TextInputLayout emaillayout;
    TextInputLayout passswordlayout;
    TextInputLayout confirmpasswordlayout;

    MaterialButton createAccountBtn;
    CircularProgressIndicator circularProgressIndicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_account,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        login = view.findViewById(R.id.create_to_login);
        usernamelayout = view.findViewById(R.id.create_account_name);
        emaillayout = view.findViewById(R.id.create_account_email);
        passswordlayout = view.findViewById(R.id.create_account_password);
        confirmpasswordlayout = view.findViewById(R.id.create_account_password_confirm);

        usernamelayout.getEditText().addTextChangedListener(changeListener(usernamelayout));
        emaillayout.getEditText().addTextChangedListener(changeListener(emaillayout));
        passswordlayout.getEditText().addTextChangedListener(changeListener(passswordlayout));
        confirmpasswordlayout.getEditText().addTextChangedListener(changeListener(confirmpasswordlayout));

        createAccountBtn = view.findViewById(R.id.create_account_btn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setTextColor(getContext().getResources().getColor(R.color.teal_200));
                showFragment(new LoginFragment());
            }
        });

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "onClick: login");

                String username = Objects.requireNonNull(usernamelayout.getEditText()).getText().toString();
                String email = Objects.requireNonNull(emaillayout.getEditText()).getText().toString();
                String password = Objects.requireNonNull(passswordlayout.getEditText()).getText().toString();
                String confirmpassword = Objects.requireNonNull(confirmpasswordlayout.getEditText()).getText().toString();

                if (!password.equals(confirmpassword)){
                    confirmpasswordlayout.setErrorEnabled(true);
                    passswordlayout.setErrorEnabled(true);
                    confirmpasswordlayout.setError("Password and Confirm password not match");
                    passswordlayout.setError("Password and Confirm password not match");
                    return;
                }

                if (username.equals("")){
                    usernamelayout.setError("Empty");
                    usernamelayout.setErrorEnabled(true);
                    return;
                }

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", username);
                    jsonObject.put("email", email);
                    jsonObject.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                circularProgressIndicator = view.findViewById(R.id.create_progress);
                circularProgressIndicator.setVisibility(CircularProgressIndicator.VISIBLE);
                createAccountBtn.setVisibility(MaterialButton.GONE);

                RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()).getApplicationContext());
                String url = getString(R.string.url);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url+"register", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("message").equals("Email already register")){
                                emaillayout.setError(response.getString("message"));
                                emaillayout.setErrorEnabled(true);
                                circularProgressIndicator.setVisibility(CircularProgressIndicator.GONE);
                                createAccountBtn.setVisibility(MaterialButton.VISIBLE);
                            }else if (response.getString("message").equals("ok")){
                                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor =
                                        getActivity().getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE).edit();
                                editor.putString("token", response.getString("token"));
                                editor.apply();

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("test", "onResponse: "+error);
                    }
                }){@Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<>();
                    header.put("Accept","application/json");
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
            };
        });
    }


    private TextWatcher changeListener(TextInputLayout textInputLayout){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    };

    private void showFragment(Fragment fragment){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_auth_container,fragment);
        fragmentTransaction.commit();
    };
}
