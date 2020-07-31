package com.test.flowerdetection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.etName)
    EditText etNama;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    ProgressDialog loading;
    Context mContext;
    //public static String postUrl = "http://10.0.2.2:5000/user/register";
    public static String postUrl;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //getSupportActionBar().hide();

        ButterKnife.bind(this);
        mContext = this;
        postUrl = mContext.getString(R.string.posturl) + "/user/register";
        //mApiService = UtilsApi.getAPIService();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
                requestRegister(v);
            }
        });
    }

    public void requestRegister(View v) {
        EditText nameView = findViewById(R.id.etName);
        EditText mobileView = findViewById(R.id.etMobile);
        EditText emailView = findViewById(R.id.etEmail);
        EditText passwordView = findViewById(R.id.etPassword);
        EditText repasswordView = findViewById(R.id.etRePassword);

        String name = nameView.getText().toString().trim();
        String mobile = mobileView.getText().toString().trim();
        String email = emailView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();
        String password2 = repasswordView.getText().toString().trim();

        if (name.length() == 0 || mobile.length() == 0 || email.length() == 0 || password.length() == 0 || password2.length() == 0) {
            Toast.makeText(getApplicationContext(), "Something is wrong. Please check your inputs.", Toast.LENGTH_LONG).show();
        } else {
            JSONObject registrationForm = new JSONObject();
            try {
               // registrationForm.put("subject", "register");

                registrationForm.put("email", email);
                registrationForm.put("mobile", mobile);
                registrationForm.put("username",name);
                registrationForm.put("password", password);
                registrationForm.put("password2", password2);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), registrationForm.toString());

            postRequest(postUrl, body);
        }
    }

    public void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                Log.d("FAIL", e.getMessage());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseTextRegister);
                        responseText.setText("Failed to Connect to Server. Please Try Again.");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) {
                final TextView responseTextRegister = findViewById(R.id.responseTextRegister);
                try {
                    final String responseString = response.body().string().trim();
                    JSONObject result = new JSONObject(responseString);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            if (responseString.equals("success")) {
//                                responseTextRegister.setText("Registration completed successfully.");
//                                finish();
//                            } else if (responseString.equals("username")) {
//                                responseTextRegister.setText("Username already exists. Please chose another username.");
//                            } else {
//                                responseTextRegister.setText("Something went wrong. Please try again later.");
//                            }
                            try {
                                if(result.get("errno").equals("0")) {
                                    Log.d("LOGIN", "Successful Login");
                                    //  finish();//finishing activity and return to the calling activity.
                                    startActivity(new Intent(mContext, SignInActivity.class));
                                } else if(!result.get("errno").equals("0")) {
                                    System.out.println("////I am here");
                                    TextView responseText = findViewById(R.id.responseTextRegister);
                                    responseText.setText(result.get("errmsg").toString());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
