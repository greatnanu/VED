package in.co.itshubham.fragment;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity  {
    AlertDialog.Builder builder;
    private EditText username,password;
    private Button submit,forgetPassword;
    private ProgressDialog progressDialog;
    public static final int RequestPermissionCode = 1;
    Date d= new Date();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCenter.start(getApplication(), "bb3f8b2b-8f80-49fa-8faa-cdeaac867f28", Analytics.class, Crashes.class);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        forgetPassword = findViewById(R.id.forgetPassword);
        builder = new AlertDialog.Builder(this,android.R.style.Theme_Material_Dialog_Alert);
        submit = findViewById(R.id.login);
        progressDialog = new ProgressDialog(this,R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        progressDialog.setMessage("Please wait...");
        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, EntryRecord.class));
            return;
        }
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Get Password");
                final EditText username = new EditText(MainActivity.this);
                username.setHint("Enter Username ");
                username.setTextColor(Color.WHITE);
                username.setInputType(InputType.TYPE_CLASS_TEXT );
                builder.setView(username);

// Set up the buttons
                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(username.getText())) {
                            editPassword(username.getText().toString().trim());

                        } else {
                            Toast.makeText(MainActivity.this, "Username is Empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()){
                    if (isNetworkAvailable()){
                        if (!TextUtils.isEmpty(username.getText())){
                            if (!TextUtils.isEmpty(password.getText())){
                                userLogin();
                            }else{
                                Toast.makeText(MainActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(MainActivity.this, "Please Enter Username", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        builder.setMessage("Connect to Internet").show();
                    }

                }else {
                    requestPermission();
                }
            }
        });


    }
    private void userLogin(){
        final String Useraname = username.getText().toString().trim();
        final String Password = password.getText().toString().trim();
        String Serial =null;
        if (checkPermission()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Serial =Build.getSerial();
            }else{
                Serial =Build.SERIAL;
            }
        }else{
            requestPermission();
        }
        progressDialog.show();
        final String finalSerial = Serial;
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){
                                long date = obj.getLong("validity");
                                int i = (int) ((date / 86400000)-(d.getTime() / 86400000) ) ;
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .userLogin(
                                                obj.getInt("id"),
                                                obj.getString("username"),
                                                obj.getString("name"),
                                                String.valueOf(i)

                                        );
                                startActivity(new Intent(getApplicationContext(), EntryRecord.class));
                                finish();
                            }else{
                                builder.setMessage(obj.getString("message")).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        builder.setMessage(error.getMessage()).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", Useraname);
                params.put("password", Password);
                assert finalSerial != null;
                params.put("device", finalSerial);
                return params;
            }

        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(),
                CAMERA);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(),
                CALL_PHONE);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(),
                READ_PHONE_STATE);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED&&
                result2 == PackageManager.PERMISSION_GRANTED&&
                result3 == PackageManager.PERMISSION_GRANTED&&
                result4 == PackageManager.PERMISSION_GRANTED;

    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO,CAMERA,CALL_PHONE,READ_PHONE_STATE}, RequestPermissionCode);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean CameraPermission = grantResults[2] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean CallPermission = grantResults[3] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean ReadPhonePermission = grantResults[4] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission && CameraPermission&& CallPermission&& ReadPhonePermission) {
                    } else {
                        builder.setMessage("Permission Denied").show();
                    }
                }
                break;
        }
    }


    private void editPassword(String username) {
        final String USERNAME = username;
        progressDialog.setMessage("Sending Password to the user");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_FORGETPASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", USERNAME);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
