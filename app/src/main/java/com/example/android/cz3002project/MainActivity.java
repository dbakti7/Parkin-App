package com.example.android.cz3002project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Button logOutButton;
    Button signInButton;
    Button signUpButton;
    Button skipNowButton;
    Button enterButton;
    TextView userNameText;
    JSONParser jParser = new JSONParser();
    private static String url_read_user = "http://10.27.44.239/read_user.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        String name = preferences.getString("Name", "");
        logOutButton = (Button) findViewById(R.id.homeButtonLogOut);
        signInButton = (Button) findViewById(R.id.homeButtonSignIn);
        signUpButton = (Button) findViewById(R.id.homeButtonRegister);
        skipNowButton = (Button) findViewById(R.id.homeButtonPlayGames);
        enterButton = (Button) findViewById(R.id.homeButtonEnter);
        userNameText = (TextView) findViewById(R.id.homeTVUserName);
        if(!name.equalsIgnoreCase("")) {
            signInButton.setVisibility(View.GONE);
            signUpButton.setVisibility(View.GONE);
            skipNowButton.setVisibility(View.GONE);
            logOutButton.setVisibility(View.VISIBLE);
            enterButton.setVisibility(View.VISIBLE);
            userNameText.setText(name);
        }
        else {
            signInButton.setVisibility(View.VISIBLE);
            signUpButton.setVisibility(View.VISIBLE);
            skipNowButton.setVisibility(View.VISIBLE);
            logOutButton.setVisibility(View.GONE);
            enterButton.setVisibility(View.GONE);
            userNameText.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
        String name = preferences.getString("Name", "");
        if(!name.equalsIgnoreCase("")) {
            signInButton.setVisibility(View.GONE);
            signUpButton.setVisibility(View.GONE);
            skipNowButton.setVisibility(View.GONE);
            logOutButton.setVisibility(View.VISIBLE);
            enterButton.setVisibility(View.VISIBLE);
            userNameText.setText(name);
        }
        else {
            signInButton.setVisibility(View.VISIBLE);
            signUpButton.setVisibility(View.VISIBLE);
            skipNowButton.setVisibility(View.VISIBLE);
            logOutButton.setVisibility(View.GONE);
            enterButton.setVisibility(View.GONE);
            userNameText.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    public void SignUp(View view)
    {
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void LogIn(View view)
    {
        Intent intent = new Intent(MainActivity.this, LogIn.class);
        startActivity(intent);
    }

    public void SkipNow(View view) {
        Intent intent = new Intent(MainActivity.this, MainMenu.class);
        startActivity(intent);
    }

    public void Enter(View view) {
        Intent intent = new Intent(MainActivity.this, MainMenu.class);
        startActivity(intent);
    }

    public void LogOut(View view)
    {
        editor.putString("Name", "");
        editor.putString("Email", "");
        editor.apply();
        finish();
        startActivity(getIntent());
    }


}
