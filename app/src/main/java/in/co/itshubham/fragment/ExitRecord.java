package in.co.itshubham.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.SearchView;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExitRecord extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DatePickerFragment.DateDialogListener, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar = null;
    private RecyclerView myrv;
    AlertDialog.Builder builder;
    ExitRecordAdapter myAdapter;
    private JsonArrayRequest ArrayRequest;
    private List<ExitRecordModel> lstAnime = new ArrayList<>();
    private RequestQueue requestQueue;
    Date d = new Date();
    private RequestOptions requestOptions =new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.loading)
            .error(R.drawable.loading);
    private List<Profile> lstProfile = new ArrayList<>();
    private String url = "http://vedsoftwares.com/shubham/showDataExit.php?username=";
    private String urlAll = "http://vedsoftwares.com/shubham/showDataExitAll.php?username=";
    private String urlSearch = "http://vedsoftwares.com/shubham/searchExitUser.php?username=";
    private ProgressDialog progressDialog;
    Dialog about;
    SwipeRefreshLayout swipeRefreshLayout ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit_record);
        getProfile();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);;
        progressDialog = new ProgressDialog(this);
        myrv = findViewById(R.id.recyclerView);
        jsoncall(0);
        swipeRefreshLayout = findViewById(R.id.swipeExit);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, android.R.color.holo_green_dark,android.R.color.holo_orange_dark,android.R.color.holo_blue_dark);
        builder = new AlertDialog.Builder(ExitRecord.this, android.R.style.Theme_Material_Dialog_Alert);
        about= new Dialog(ExitRecord.this);
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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://vedsoftwares.com/shubham/csvexit.php?username=" + SharedPrefManager.getInstance(getApplicationContext()).getUsername())));
                } else {
                    Toast.makeText(ExitRecord.this, "Internet Not Connected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
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
        getMenuInflater().inflate(R.menu.entry_record, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.getAll) {
            lstAnime.clear();
            getAll();
        } else if (id == R.id.pickDate) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            DatePickerFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.show(fragmentManager, "datePicker");
        } else if (id == R.id.logout) {
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
            final EditText password = new EditText(ExitRecord.this);
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
                        Toast.makeText(ExitRecord.this, "New Password is Empty", Toast.LENGTH_SHORT).show();
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
            ExitRecord.this.about.show();

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
                            Toast.makeText(ExitRecord.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(ExitRecord.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(ExitRecord.this, MainActivity.class));
    }

    @Override
    public void onFinishDialog(Date date) {
        if ((int) (date.getTime() / 86400000) < (int) (d.getTime() / 86400000)) {
            int i = (int) ((d.getTime() / 86400000) - (date.getTime() / 86400000))-1 ;
            try {
                lstAnime.clear();
                jsoncall(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            builder.setMessage("Selected a future date So data not availble").show();
        }
    }

    public void jsoncall(int i) {
        lstAnime.clear();
        final String URL = url + SharedPrefManager.getInstance(ExitRecord.this).getUsername()+"&&day="+i;
        ArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        ExitRecordModel anime = new ExitRecordModel();
                        anime.setImage_url(jsonObject.getString("image_url"));
                        anime.setAudio_url(jsonObject.getString("audio_url"));
                        anime.setId(jsonObject.getString("id"));
                        anime.setName(jsonObject.getString("name"));
                        anime.setStudentName(jsonObject.getString("studentName"));
                        anime.setStudentDetails(jsonObject.getString("studentDetails"));
                        anime.setMobile(jsonObject.getString("mobile"));
                        anime.setRelation(jsonObject.getString("relation"));
                        anime.setAddress(jsonObject.getString("address"));
                        anime.setDate(jsonObject.getString("date"));
                        lstAnime.add(anime);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                setRvadapter(lstAnime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.getMessage());

            }
        });

        requestQueue = Volley.newRequestQueue(ExitRecord.this);
        requestQueue.add(ArrayRequest);
    }
    public  void getAll(){
        lstAnime.clear();
        final  String URLALL= urlAll+SharedPrefManager.getInstance(ExitRecord.this).getUsername();
        ArrayRequest = new JsonArrayRequest(URLALL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;


                for (int i = 0; i < response.length(); i++) {

                    try {

                        jsonObject = response.getJSONObject(i);
                        ExitRecordModel anime = new ExitRecordModel();
                        anime.setImage_url(jsonObject.getString("image_url"));
                        anime.setAudio_url(jsonObject.getString("audio_url"));
                        anime.setId(jsonObject.getString("id"));
                        anime.setName(jsonObject.getString("name"));
                        anime.setStudentName(jsonObject.getString("studentName"));
                        anime.setStudentDetails(jsonObject.getString("studentDetails"));
                        anime.setMobile(jsonObject.getString("mobile"));
                        anime.setRelation(jsonObject.getString("relation"));
                        anime.setAddress(jsonObject.getString("address"));
                        anime.setDate(jsonObject.getString("date"));
                        lstAnime.add(anime);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                setRvadapter(lstAnime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.getMessage());

            }
        });

        requestQueue = Volley.newRequestQueue(ExitRecord.this);
        requestQueue.add(ArrayRequest);

    }

    public void setRvadapter(List<ExitRecordModel> lst) {
        myAdapter = new ExitRecordAdapter(this, lst);
        myrv.setAdapter(myAdapter);
        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        myrv.setLayoutManager(manager);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        lstAnime.clear();
        searchUser(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        lstAnime.clear();
        searchUser(newText);
        return true;
    }
    public void searchUser(String name){
        lstAnime.clear();
        final String URL = urlSearch+SharedPrefManager.getInstance(ExitRecord.this).getUsername()+"&&name="+name;
        ArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        ExitRecordModel anime = new ExitRecordModel();
                        anime.setImage_url(jsonObject.getString("image_url"));
                        anime.setAudio_url(jsonObject.getString("audio_url"));
                        anime.setId(jsonObject.getString("id"));
                        anime.setName(jsonObject.getString("name"));
                        anime.setStudentName(jsonObject.getString("studentName"));
                        anime.setStudentDetails(jsonObject.getString("studentDetails"));
                        anime.setMobile(jsonObject.getString("mobile"));
                        anime.setRelation(jsonObject.getString("relation"));
                        anime.setAddress(jsonObject.getString("address"));
                        anime.setDate(jsonObject.getString("date"));
                        lstAnime.add(anime);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setRvadapter(lstAnime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.getMessage());

            }
        });

        requestQueue = Volley.newRequestQueue(ExitRecord.this);
        requestQueue.add(ArrayRequest);
    }

    @Override
    public void onRefresh() {
        lstAnime.clear();
        jsoncall(0);
        swipeRefreshLayout.setRefreshing(false);
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

        requestQueue = Volley.newRequestQueue(ExitRecord.this);
        requestQueue.add(ArrayRequest);
    }
}
