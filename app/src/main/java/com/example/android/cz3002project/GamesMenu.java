package com.example.android.cz3002project;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * This class is used to handle activity_games_menu which is the menu that displays the list
 * of games
 */
public class GamesMenu extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_games_menu, menu);
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
    public void onBackPressed()
    {
        Intent intent = new Intent(this,MainMenu.class);
        startActivity(intent);
    }

    public void PlayGame2(View view)
    {
        // play Shape Tracing
        Intent intent = new Intent(GamesMenu.this, DrawingGameActivity.class);
        startActivity(intent);
    }

    public void PlayGame3(View view)
    {
        // play Word Voicing Game
        Intent intent = new Intent(GamesMenu.this, WordVoicing.class);
        startActivity(intent);
    }

    public void ViewStatistics(View view)
    {
        // View user's statistics
        Intent intent = new Intent(GamesMenu.this, Statistics.class);
        startActivity(intent);
    }
}
