package com.mehedi.pchtest;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mehedi.pchtest.adapter.UserAdapter;
import com.mehedi.pchtest.viewModel.UserViewModel;
import com.mehedi.pchtest.volley.VolleySingleton;
import com.mehedi.pchtest.db.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UserAdapter.OnEditButtonClickListener {

    private UserViewModel userViewModel;
    public static final int EDIT_USER_REQUEST = 1;
    private static final String TAG = "TCH_TEST";
    UserAdapter recyclerAdapter;
    private RequestQueue mQueue;
    List<User> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //call this method to load the user data from internet
        loadUserData();

        recyclerAdapter = new UserAdapter(this, this);

        //pass the correct viewModel instance.where the Activity is passed to this
        // ViewModel's lifecycle should be scoped to.
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        //observing the live data. if the data changed it will be notified immmediately
        userViewModel.getAllUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                recyclerAdapter.setData(users);
            }
        });

        //binding the user data to adapter
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

    }

    //check if network is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //insert the data from the internet into the database
    public void insertUserData(List<User> users) {

        for (User user : users) {

            userViewModel.insert(user);
        }

    }

    public void loadUserData() {

        //get an instance of the volley requestque from the singleton class
        mQueue = VolleySingleton.getInstance(this).getRequestQueue();

        //check if internet connection is available. otherwise do not proceed
        if (isNetworkAvailable()) {

            userList = new ArrayList<User>();
            String url = "http://www.mocky.io/v2/5c4650753100004c0005f340";


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                //Callback will be call when get response
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        Log.d(TAG, "response" + response.toString());
                        JSONArray resultsArray = response.getJSONArray("userList");
                        //loop through the jsonarray to get all the user data
                        for (int i = 0; i < resultsArray.length(); i++) {
                            JSONObject singleObject = resultsArray.getJSONObject(i);
                            String id = singleObject.getString("id");

                            //use sharepreference to check if this id already exist
                            //if the id already exist on the db. it shoudl not insert new data

                            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userID", Context.MODE_PRIVATE);
                            if (sharedPref.contains(id)) {
                                //this user data is already inserted to database. no need to insert again
                                Log.d(TAG, "data already exist. do not insert ");

                            } else {
                                //this user data is never inserted. insert data
                                Log.d(TAG, "data does not exist in db. inserting data");
                                String firstName = singleObject.getString("name");
                                String firstNameInitial = firstName.substring(0, 1) + ".";
                                String lastName = singleObject.getString("lastName");
                                String pictureUrl = singleObject.getString("picture");
                                String dailyWageString = singleObject.getString("dailyWage");
                                int dailyWage = Integer.parseInt(dailyWageString);
                                int numberOfDaysRequired = 10000 / dailyWage;

                                User result = new User(id, firstNameInitial, lastName, pictureUrl, dailyWage, numberOfDaysRequired);

                                //create the each user instance and add it to the arraylist

                                userList.add(result);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(id, id);
                                editor.apply();
                            }


                        }

                        // if the Id never downloaded, add the user data
                        if (userList.size() > 0) {
                            insertUserData(userList);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Json data fetch" + error.getMessage());
                }
            });

            mQueue.add(jsonObjectRequest);

        } else {
            Log.d(TAG, "device is not connected to internet");
            Toast toast = Toast.makeText(this, "PLEASE CONNECT TO INTERNET", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }


    }


    @Override
    public void onEditButtonClicked(User user) {

        //edit button is pressed. new activity will be opened to provide the user edit option
        //data is transferred to load the new activity

        Intent intent = new Intent(MainActivity.this, EditUserActivity.class);
        intent.putExtra(EditUserActivity.EXTRA_ID, user.getId());
        intent.putExtra(EditUserActivity.EXTRA_FIRST_NAME, user.getFirstName());
        intent.putExtra(EditUserActivity.EXTRA_LAST_NAME, user.getLastName());
        intent.putExtra(EditUserActivity.EXTRA_PICTURE, user.getPicture());
        intent.putExtra(EditUserActivity.EXTRA_WAGE, user.getDailyWage());
        intent.putExtra(EditUserActivity.EXTRA_NUMBER_OF_DAYS, user.getNumberOfDays());
        startActivityForResult(intent, EDIT_USER_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_USER_REQUEST && resultCode == RESULT_OK) {

            String firstName = data.getStringExtra(EditUserActivity.EXTRA_FIRST_NAME);
            String lastName = data.getStringExtra(EditUserActivity.EXTRA_LAST_NAME);
            String id = data.getStringExtra(EditUserActivity.EXTRA_ID);
            String picture = data.getStringExtra(EditUserActivity.EXTRA_PICTURE);
            int wage = Integer.parseInt(data.getStringExtra(EditUserActivity.EXTRA_WAGE));
            //update the database with the new data
            User user = new User(id, firstName, lastName, picture, wage, 10000 / wage);
            userViewModel.update(user);

            Toast.makeText(this, "User wage updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User data not changed", Toast.LENGTH_SHORT).show();
        }

    }


}

