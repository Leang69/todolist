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
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginFragment extends Fragment {
    TextView createAccount;
    MaterialButton buttonLogin;
    TextInputLayout emailLayout;
    TextInputLayout passwordLayout;

    String token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        token = prefs.getString("token", "No name defined");
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonLogin = view.findViewById(R.id.btn_login);
        emailLayout = view.findViewById(R.id.login_email);
        passwordLayout = view.findViewById(R.id.login_password);
        createAccount = view.findViewById(R.id.crete_account);

        emailLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount.setTextColor(getContext().getResources().getColor(R.color.teal_200));
                showFragment(new CreateAccountFragment());
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "onClick: login");

                RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()).getApplicationContext());
                String url = getString(R.string.url);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("email", emailLayout.getEditText().getText());
                    jsonObject.put("password", passwordLayout.getEditText().getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                CircularProgressIndicator progressBar = view.findViewById(R.id.login_progress);
                progressBar.setVisibility(CircularProgressIndicator.VISIBLE);
                buttonLogin.setVisibility(MaterialButton.GONE);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "login", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("message").equals("Login wrong email")) {
                                emailLayout.setError(response.getString("message"));
                                emailLayout.setErrorEnabled(true);

                                buttonLogin.setVisibility(MaterialButton.VISIBLE);
                                progressBar.setVisibility(CircularProgressIndicator.GONE);
                            } else if (response.getString("message").equals("Login wrong password")) {
                                passwordLayout.setError(response.getString("message"));
                                passwordLayout.setErrorEnabled(true);

                                buttonLogin.setVisibility(MaterialButton.VISIBLE);
                                progressBar.setVisibility(CircularProgressIndicator.GONE);
                            } else if (response.getString("message").equals("ok")) {
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
                        progressBar.setVisibility(CircularProgressIndicator.GONE);
                        buttonLogin.setVisibility(MaterialButton.VISIBLE);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> header = new HashMap<>();
                        header.put("Accept", "application/json");
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

            ;
        });
    }


    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_auth_container, fragment);
        fragmentTransaction.commit();
    }

    ;
}
