package com.example.android.cz3002project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Button logOutButton;
    Button signInButton;
    Button signUpButton;
    Button skipNowButton;
    Button enterButton;
    TextView userNameText;

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

        // If user is currently logged in into the system
        if(!name.equalsIgnoreCase("")) {
            signInButton.setVisibility(View.GONE);
            signUpButton.setVisibility(View.GONE);
            skipNowButton.setVisibility(View.GONE);
            logOutButton.setVisibility(View.VISIBLE);
            enterButton.setVisibility(View.VISIBLE);
            userNameText.setText("Hi, " + name + "!");
        }
        // is user is not logged in into the system
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

        // if user is currently logged in into the system
        if(!name.equalsIgnoreCase("")) {
            signInButton.setVisibility(View.GONE);
            signUpButton.setVisibility(View.GONE);
            skipNowButton.setVisibility(View.GONE);
            logOutButton.setVisibility(View.VISIBLE);
            enterButton.setVisibility(View.VISIBLE);
            userNameText.setText("Hi, " + name + "!");
        }
        // is user is not logged in into system
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
        // exit from application
        moveTaskToBack(true);

    }

    public void SignUp(View view)
    {
        // Register for new user account
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void LogIn(View view)
    {
        // Log in
        Intent intent = new Intent(MainActivity.this, LogIn.class);
        startActivity(intent);
    }

    public void SkipNow(View view) {
        // Proceed without logging in
        Intent intent = new Intent(MainActivity.this, MainMenu.class);
        startActivity(intent);
    }

    public void Enter(View view) {
        // Proceed to Main Menu
        Intent intent = new Intent(MainActivity.this, MainMenu.class);
        startActivity(intent);
    }

    public void LogOut(View view)
    {
        // Log Out
        editor.putString("Name", "");
        editor.putString("Email", "");
        editor.apply();
        finish();
        startActivity(getIntent());
    }


}
