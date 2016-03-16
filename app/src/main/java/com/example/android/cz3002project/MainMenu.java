package com.example.android.cz3002project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainMenu extends ActionBarActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Button logOutButton;
    TextView userNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        String name = preferences.getString("Name", "");
        logOutButton = (Button) findViewById(R.id.menuButtonLogOut);
        userNameText = (TextView) findViewById(R.id.menuTVUserName);

        if(!name.equalsIgnoreCase("")) {
            logOutButton.setVisibility(View.VISIBLE);
            userNameText.setText(name);
        }
        else {
            logOutButton.setVisibility(View.GONE);
            userNameText.setText("");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the mainMenu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
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

//    @Override
//    public void onBackPressed() {
//    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void SetReminder(View view)
    {
        Intent intent = new Intent(MainMenu.this, SetReminder.class);
        startActivity(intent);
    }

    public void PlayGames(View view)
    {
        Intent intent = new Intent(MainMenu.this, GamesMenu.class);
        startActivity(intent);
    }

    public void LogOut(View view)
    {
        editor.putString("Name", "");
        editor.apply();
        Intent intent = new Intent(MainMenu.this, MainActivity.class);
        startActivity(intent);
    }
}
