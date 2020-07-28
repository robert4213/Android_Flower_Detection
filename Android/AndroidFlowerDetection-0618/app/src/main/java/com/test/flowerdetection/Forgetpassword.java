package com.test.flowerdetection;

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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Forgetpassword extends AppCompatActivity {
    Button btnChangPassword;
    Context mContext;
    public static String postUrl = "http://10.0.2.2:5000/user/change_password";
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);
        mContext = this;
        btnChangPassword = findViewById(R.id.btnChangPassword);
        btnChangPassword.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                changePassword(v);
            }
        });
    }

    private void changePassword(View v) {
        EditText currPassword = findViewById(R.id.currPassword);
        EditText newPassword = findViewById(R.id.newPassword);
        EditText newPassowrd2 = findViewById(R.id.rePassword);

        String currPass = currPassword.getText().toString().trim();
        String newPass = newPassword.getText().toString().trim();
        String newPass2 = newPassowrd2.getText().toString().trim();
        if (currPass.length() == 0 || newPass.length() == 0 || newPass2.length() == 0 ) {
            Toast.makeText(getApplicationContext(), "Something is wrong. Please check your inputs.", Toast.LENGTH_LONG).show();
        } else {
            JSONObject registrationForm = new JSONObject();
            try {
                // registrationForm.put("subject", "register");
               // User user = SharedPrefManager.getInstance(mContext).getUser();

                registrationForm.put("current_password", currPass);
                registrationForm.put("new_password", newPass);
                registrationForm.put("new_password2",newPass2);
                registrationForm.put("session_id",SharedPrefManager.getInstance(mContext).getUser().getId());
                System.out.println(SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn());
                System.out.println(SharedPrefManager.getInstance(getApplicationContext()).getUser().getEmail());
                System.out.println(SharedPrefManager.getInstance(getApplicationContext()).getUser().getId());
                System.out.println("////");
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
                                    System.out.println(result.toString());
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
