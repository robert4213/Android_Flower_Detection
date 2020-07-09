package com.test.flowerdetection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.btnRegister) Button btnRegister;
    ProgressDialog loading;

    Context mContext;
    //public static String postUrl = "http://10.0.2.2:5000/user/login";
    public static String postUrl;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        mContext = this;
        postUrl = this.getString(R.string.posturl) + "/user/login";


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
                requestLogin(v);
                startActivity(new Intent(mContext, MainActivity.class));
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, RegisterActivity.class));
            }
        });
    }

    public void requestLogin(View v) {
        System.out.println("//////" + "hello world");
        EditText emailView = findViewById(R.id.etEmail);
        EditText passwordView = findViewById(R.id.etPassword);

        String email = emailView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();

        System.out.println("/////////email:" + email);
        if (email.length() == 0 || password.length() == 0) {
            Toast.makeText(getApplicationContext(), "Something is wrong. Please check your inputs.", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject loginForm = new JSONObject();
        try {
            loginForm.put("subject", "login");
            loginForm.put("email", email);
            loginForm.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), loginForm.toString());

        postRequest(postUrl, body);
    }
    public void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();
                Log.d("FAIL", e.getMessage());

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseTextLogin = findViewById(R.id.responseTextLogin);
                       responseTextLogin.setText("Failed to Connect to Server. Please Try Again.");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseTextLogin = findViewById(R.id.responseTextLogin);
                        try {
                            String loginResponseString = response.body().string().trim();
                            Log.d("LOGIN", "Response from the server : " + loginResponseString);
                            if (loginResponseString.equals("success")) {
                                Log.d("LOGIN", "Successful Login");
                                finish();//finishing activity and return to the calling activity.
                            } else if (loginResponseString.equals("failure")) {
                                responseTextLogin.setText("Login Failed. Invalid username or password.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            responseTextLogin.setText("Something went wrong. Please try again later.");
                        }
                    }
                });
            }
        });
    }
}
