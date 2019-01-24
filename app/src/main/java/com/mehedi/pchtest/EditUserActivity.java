package com.mehedi.pchtest;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


public class EditUserActivity extends AppCompatActivity {
    private static final String TAG = "TCH_TEST";
    public static final String EXTRA_ID =
            "com.mehedi.pchtest.EXTRA_ID";
    public static final String EXTRA_FIRST_NAME =
            "com.mehedi.pchtest.EXTRA_NAME";
    public static final String EXTRA_LAST_NAME =
            "com.mehedi.pchtest.EXTRA_LAST_NAME";
    public static final String EXTRA_PICTURE =
            "com.mehedi.pchtest.EXTRA_PICTURE";
    public static final String EXTRA_WAGE =
            "com.mehedi.pchtest.EXTRA_WAGE";
    public static final String EXTRA_NUMBER_OF_DAYS =
            "com.mehedi.pchtest.EXTRA_NUMBER_OF_DAYS";

    private TextView textName;
    private EditText dailyWage;
    String id;
    String firstName;
    String lastName;
    String picture;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        textName = findViewById(R.id.text_view_user_name2);
        dailyWage = findViewById(R.id.edit_text_wage);
        imageView = findViewById(R.id.circleView2);


        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher_foreground);

        Intent intent = getIntent();

        setTitle("Edit User");

        //get the data from the mainactivity and dislay to the user
        picture = intent.getStringExtra(EXTRA_PICTURE);

        //load the image with glide library
        Glide.with(this)
                .load(picture)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);

        id = intent.getStringExtra(EXTRA_ID);
        firstName = intent.getStringExtra(EXTRA_FIRST_NAME);
        lastName = intent.getStringExtra(EXTRA_LAST_NAME);
        textName.setText(firstName + " " + lastName);
        dailyWage.setText(intent.getStringExtra(EXTRA_WAGE));

    }

    private void saveUser() {

        Log.d(TAG, "user clicked the save data icon");
        String wage = dailyWage.getText().toString();

        //checking if the wage is empty
        if (wage.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a wage for the user", Toast.LENGTH_SHORT).show();
            return;
        }
        //send the new data back to the previous activity
        Intent data = new Intent();
        data.putExtra(EXTRA_FIRST_NAME, firstName);
        data.putExtra(EXTRA_LAST_NAME, lastName);
        data.putExtra(EXTRA_PICTURE, picture);
        data.putExtra(EXTRA_ID, id);
        data.putExtra(EXTRA_WAGE, wage);

        setResult(RESULT_OK, data);
        //finishing the activity so it is not in the back stack
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle save icon clicks here. if this icon is clicked it
        // will save the data of the user

        switch (item.getItemId()) {
            case R.id.save_user:
                saveUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}