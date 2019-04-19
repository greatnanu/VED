package in.co.itshubham.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExitRegister extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String time = String.valueOf(Calendar.getInstance().getTimeInMillis());
    AlertDialog.Builder builder;
    Boolean isRecording = false;
    AlertDialog dialog;
    private ProgressDialog progressDialog;
    private MediaRecorder mediaRecorder;
    private final int IMG_REQUEST_CAMERA = 1;
    private final int IMG_REQUEST_CHOOSE = 2;
    private RequestOptions requestOptions =new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.loading)
            .error(R.drawable.loading);
    private List<Profile> lstProfile = new ArrayList<>();
    private JsonArrayRequest ArrayRequest;
    private RequestQueue requestQueue;
    private Bitmap bitmap;
    String AudioSavePathInDevice = null;
    private ImageView imageView;
    private EditText name, studentName, studentDetails, mobile, address;
    private AutoCompleteTextView purpose;
    private Button submit,tick;
    Dialog about;
    String[] purpose_items = new String[]{"Father", "Mother", "Brother", "Sister", "Cousin", "GrandParent", "Neighbour", "Others"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getProfile();
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView schoolName = headerView.findViewById(R.id.schoolName);
        schoolName.setText(SharedPrefManager.getInstance(this).getKeyUserSchoolName());
        TextView userName = headerView.findViewById(R.id.userName);
        userName.setText(SharedPrefManager.getInstance(this).getUsername());
        Menu menu = navigationView.getMenu();

        // find MenuItem you want to change
        MenuItem validity = menu.findItem(R.id.validity);

        // set new title to the MenuItem
        validity.setTitle("Your App will expire in "+SharedPrefManager.getInstance(this).getVALIDITY()+" Days");
        navigationView.setNavigationItemSelectedListener(this);
        about= new Dialog(ExitRegister.this);
        about.setContentView(R.layout.aboutus);
        final TextView frag_name = about.findViewById(R.id.about_name);
        final TextView frag_mobile = about.findViewById(R.id.about_mobile);
        final TextView frag_desc = about.findViewById(R.id.about_desc);
        final TextView frag_address = about.findViewById(R.id.about_address);
        final TextView frag_email = about.findViewById(R.id.about_email);
        final ImageView frag_photo = about.findViewById(R.id.about_photo);
        frag_name.setText(Profile.getPerson_name());
        frag_mobile.setText(Profile.getMobile());
        frag_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    String number=mData.get(vHolder.getAdapterPosition()).getMobile();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+Profile.getMobile()));
                startActivity(callIntent);
            }
        });
        frag_desc.setText(Profile.getDesc());
        frag_email.setText(Profile.getEmail());
        frag_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.fromParts(
                        "mailto",Profile.getEmail(), null));
                startActivity(Intent.createChooser(intent, "Send email..."));
            }
        });
        frag_address.setText(Profile.getAddress());
//        frag_photo.setImageResource(R.drawable.address);
        Glide.with(getApplicationContext()).load(Profile.getImage_url()).apply(requestOptions).into(frag_photo);
        Objects.requireNonNull(about.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tick = findViewById(R.id.checkboxExit);
        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tick.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                name.setVisibility(View.VISIBLE);
                studentName.setVisibility(View.VISIBLE);
                studentDetails.setVisibility(View.VISIBLE);
                mobile.setVisibility(View.VISIBLE);
                purpose.setVisibility(View.VISIBLE);
                address.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);
                recordAudio();
            }
        });
        imageView = findViewById(R.id.groupPhoto);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                LayoutInflater factory = LayoutInflater.from(v.getContext());
                final View view = factory.inflate(R.layout.pickphoto, null);
                alertDialog.setView(view);
                ImageButton camera = view.findViewById(R.id.cameraPick);
                ImageButton gallary = view.findViewById(R.id.galleryPick);
                dialog = alertDialog.create();
                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takephoto();
                        dialog.dismiss();
                    }
                });
                gallary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        choosePhoto();
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
        name = findViewById(R.id.name);
        studentName = findViewById(R.id.studentName);
        studentDetails = findViewById(R.id.studentDetails);
        mobile = findViewById(R.id.mobile);
        address = findViewById(R.id.address);
        submit = findViewById(R.id.register);
        purpose = findViewById(R.id.relation);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line
                , purpose_items);
        purpose.setAdapter(adapter);
        progressDialog = new ProgressDialog(this);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isNetworkAvailable()) {
                        if (ImageToString(bitmap) != "0") {
                            if (!TextUtils.isEmpty(name.getText().toString())) {
                                if (!TextUtils.isEmpty(studentName.getText().toString())) {
                                    if (!TextUtils.isEmpty(studentDetails.getText().toString())) {
                                        if (!TextUtils.isEmpty(purpose.getText().toString())) {
                                            if (!TextUtils.isEmpty(mobile.getText().toString())) {
                                                if (isValidNumber(mobile.getText().toString())) {
                                                    if (!TextUtils.isEmpty(address.getText().toString())) {
                                                        registerUser();
                                                        uploadVideo();
                                                        clear();
                                                        uncheck();
                                                        isRecording=false;

                                                    } else {
                                                        Toast.makeText(ExitRegister.this, "Address is Empty", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(ExitRegister.this, "Mobile Number is not in roght format", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(ExitRegister.this, "Mobile Number isEmpty", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(ExitRegister.this, "No Relation found", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(ExitRegister.this, "No Student Details Entered", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(ExitRegister.this, "Enter Student Name going out of the school", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(ExitRegister.this, "Enter Name", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ExitRegister.this, "Image Not Selected Yet", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        builder.setMessage("Internet not connected").show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.entry_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            SharedPrefManager.getInstance(this).logout();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.entryRecord) {
            startActivity(new Intent(this, EntryRecord.class));
        } else if (id == R.id.exitRecord) {
            startActivity(new Intent(this, ExitRecord.class));
        } else if (id == R.id.entryRegister) {
            startActivity(new Intent(this, EntryRegister.class));

        } else if (id == R.id.exitRegister) {
            startActivity(new Intent(this, ExitRegister.class));

        } else if (id == R.id.update) {
            builder.setTitle("Update Password");
            final EditText password = new EditText(ExitRegister.this);
            password.setTextColor(Color.WHITE);
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(password);

// Set up the buttons
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!TextUtils.isEmpty(password.getText())) {
                        editPassword(password.getText().toString().trim());

                    } else {
                        Toast.makeText(ExitRegister.this, "New Password is Empty", Toast.LENGTH_SHORT).show();
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
        }else if (id == R.id.aboutUs) {
            ExitRegister.this.about.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRecording==true){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaRecorder.resume();
                Toast.makeText(this, "Recording resumed", Toast.LENGTH_SHORT).show();
                Log.d("shubham","Reco Resumed");
            }
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (isRecording == false) {
//            recordAudio();
//
//        }
//
//    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isRecording==true){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaRecorder.resume();
                Toast.makeText(this, "Recording Restarted", Toast.LENGTH_SHORT).show();
                Log.d("shubham","Activity Restarted");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecording==true){
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            Toast.makeText(this, "Recording Failed", Toast.LENGTH_SHORT).show();
            Log.d("shubham","Activity Destroyed");

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (submit.VISIBLE==View.VISIBLE){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (isRecording==true){
                    mediaRecorder.pause();
                    Toast.makeText(this, "Recording Paused", Toast.LENGTH_SHORT).show();
                    Log.d("shubham","Activity Paused");
                }

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (submit.VISIBLE==View.VISIBLE){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (isRecording==true){
                    mediaRecorder.pause();
//                    isRecording=false;
                    Toast.makeText(this, "Recording Stopped", Toast.LENGTH_SHORT).show();
                    Log.d("shubham","Activity Stopped");
                }

            }
        }
    }

    public void recordAudio() {
        AudioSavePathInDevice =
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                        time + "AudioRecording.3gp";
        MediaRecorderReady();
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(ExitRegister.this, "Recording started",
                Toast.LENGTH_LONG).show();
    }

    private boolean isValidNumber(String email) {
        String EMAIL_PATTERN = "[0-9]{10}";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void clear() {
        isRecording=false;
        imageView.setImageResource(R.drawable.userphoto);
        name.setText("");
        studentName.setText("");
        studentDetails.setText("");
        mobile.setText("");
        purpose.setText("");
        address.setText("");
    }

    public void takephoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, IMG_REQUEST_CAMERA);
        }
    }

    public void choosePhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST_CHOOSE);
    }

    public String ImageToString(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imgBytes = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(imgBytes, Base64.DEFAULT);
        } else {
            return "0";
        }
    }

    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == IMG_REQUEST_CAMERA && resultCode == RESULT_OK) {
            try {
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == IMG_REQUEST_CHOOSE && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.e("Image Selection Error", e.getMessage());
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void uploadVideo() {
        class UploadVideo extends AsyncTask<Void, Void, String> {

//            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                uploading = ProgressDialog.show(New_Entry.this, "Uploading File", "Please wait...", false, false);
            }

            @Override

            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                uploading.dismiss();
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.uploadVideo(AudioSavePathInDevice);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }

    private void editPassword(String password) {
        final String PASSWORD = password;
        final String User_name = SharedPrefManager.getInstance(getApplicationContext()).getUsername();
        progressDialog.setMessage("Updating  Password...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_UPDATEPASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(ExitRegister.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(ExitRegister.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", User_name);
                params.put("password", PASSWORD);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
        logout();
    }

    private void logout() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SharedPrefManager.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(ExitRegister.this, MainActivity.class));
    }

    private void registerUser() {
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        isRecording = false;
        final String Audio = time + "AudioRecording.3gp";
        final String Name = name.getText().toString().trim();
        final String StudentName = studentName.getText().toString().trim();
        final String StudentDetails = studentDetails.getText().toString().trim();
        final String Purpose = purpose.getText().toString().trim();
        final String Mobile = mobile.getText().toString().trim();
        final String Address = address.getText().toString().trim();
        final String User_name = SharedPrefManager.getInstance(getApplicationContext()).getUsername();

        progressDialog.setMessage("Registering user...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_EXIT_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(ExitRegister.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(ExitRegister.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("image", ImageToString(bitmap));
                params.put("name", Name);
                params.put("studentName", StudentName);
                params.put("studentDetails", StudentDetails);
                params.put("mobile", Mobile);
                params.put("relation", Purpose);
                params.put("address", Address);
                params.put("username", User_name);
                params.put("audio", Audio);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
//            uploadVideo();
    }
    public void uncheck(){
        tick.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        studentName.setVisibility(View.GONE);
        studentDetails.setVisibility(View.GONE);
        mobile.setVisibility(View.GONE);
        purpose.setVisibility(View.GONE);
        address.setVisibility(View.GONE);
        submit.setVisibility(View.GONE);
    }
    public void getProfile(){
        lstProfile.clear();
        ArrayRequest = new JsonArrayRequest(Constants.URL_GETPROFILE, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {

                    try {
                        jsonObject = response.getJSONObject(i);
                        Profile profile = new Profile();
                        profile.setPerson_name(jsonObject.getString("name"));
                        profile.setEmail(jsonObject.getString("email"));
                        profile.setMobile(jsonObject.getString("mobile"));
                        profile.setAddress(jsonObject.getString("address"));
                        profile.setImage_url(jsonObject.getString("image_url"));
                        profile.setDesc(jsonObject.getString("description"));
                        lstProfile.add(profile);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.getMessage());

            }
        });

        requestQueue = Volley.newRequestQueue(ExitRegister.this);
        requestQueue.add(ArrayRequest);
    }
}
