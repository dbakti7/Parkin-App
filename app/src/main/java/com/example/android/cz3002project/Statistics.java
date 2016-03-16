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
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to handle Statistics Activity
 */
public class Statistics extends ActionBarActivity {
    SharedPreferences preferences;
    String email;
    SharedPreferences.Editor editor;

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();

    // url to handle database query with PHP
    private static String url_read_user = "http://10.27.44.239/read_user.php";

    // highest score and average score for each game
    Double[] score = new Double[3];
    Double[] averageScore = new Double[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // get shared preferences data
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("Email", "");
        editor = preferences.edit();

        // if user is logged in into the system
        if(!email.equalsIgnoreCase(""))
            // if there is internet connection, retrieve the statistics from database
            if (CheckNetworkConnection.checknetwork(getApplicationContext()))
                new RetrieveData().execute();
            else
                Toast.makeText(Statistics.this, "No Internet Connection!", Toast.LENGTH_LONG).show();
        // if user is not logged in into the system, show 0 for all scores
        else {
            for(int i = 0;i<3;++i) {
                score[i] = averageScore[i] = 0.0;
            }
            TextView updateScore;
            updateScore = (TextView) findViewById(R.id.statisticsTVScore1);
            updateScore.setText(updateScore.getText() + "\n" + String.format("%.2f", score[0]));
            updateScore = (TextView) findViewById(R.id.statisticsTVScore2);
            updateScore.setText(updateScore.getText() + "\n" + String.format("%.2f", score[1]));
            updateScore = (TextView) findViewById(R.id.statisticsTVScore3);
            updateScore.setText(updateScore.getText() + "\n" + String.format("%.2f", score[2]));

            updateScore = (TextView) findViewById(R.id.statisticsTVAvgScore1);
            updateScore.setText(updateScore.getText() + "\n" + String.format("%.2f", averageScore[0]));
            updateScore = (TextView) findViewById(R.id.statisticsTVAvgScore2);
            updateScore.setText(updateScore.getText() + "\n" + String.format("%.2f", averageScore[1]));
            updateScore = (TextView) findViewById(R.id.statisticsTVAvgScore3);
            updateScore.setText(updateScore.getText() + "\n" + String.format("%.2f", averageScore[2]));
            Toast.makeText(Statistics.this, "Log In to save your statistics...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
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
        Intent intent = new Intent(this,GamesMenu.class);
        startActivity(intent);
    }

    /**
     * Async class to handle database query, which is retrieving user scores
     */
    class RetrieveData extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Statistics.this);
            pDialog.setMessage("Retrieving Data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        /**
         * Getting user's data
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));

            // getting JSON Object
            // get user's data (scores for each game)
            JSONObject json = jsonParser.makeHttpRequest(url_read_user,
                    "GET", params);


            // check for success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {
                    JSONArray user = json.getJSONArray("user");
                    JSONObject jo = user.getJSONObject(0);

                    // retrieve score for each game
                    for(Integer i = 1;i<=3;++i)
                        score[i-1] = Double.parseDouble(jo.getString("score"+i.toString()));

                    // retrieve average score for each game
                    for(Integer i = 1;i<=3;++i)
                        averageScore[i-1] = Double.parseDouble(jo.getString("average_game"+i.toString()));

                } else {
                    // some error occurred
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done

            // display the highest score and average score for each game
            TextView updateScore;
            updateScore = (TextView) findViewById(R.id.statisticsTVScore1);
            updateScore.setText(updateScore.getText() + "\n" + String.format("%.2f", score[0]));
            updateScore = (TextView) findViewById(R.id.statisticsTVScore2);
            updateScore.setText(updateScore.getText() + "\n" + String.format("%.2f", score[1]));
            updateScore = (TextView) findViewById(R.id.statisticsTVScore3);
            updateScore.setText(updateScore.getText() + "\n" + String.format("%.2f", score[2]));

            updateScore = (TextView) findViewById(R.id.statisticsTVAvgScore1);
            updateScore.setText(updateScore.getText() + "\n" + String.format("%.2f", averageScore[0]));
            updateScore = (TextView) findViewById(R.id.statisticsTVAvgScore2);
            updateScore.setText(updateScore.getText() + "\n" + String.format("%.2f", averageScore[1]));
            updateScore = (TextView) findViewById(R.id.statisticsTVAvgScore3);
            updateScore.setText(updateScore.getText() + "\n" + String.format("%.2f", averageScore[2]));
            pDialog.dismiss();
        }

    }
    public void OK(View view)
    {
        // go to Main Menu
        Intent intent = new Intent(Statistics.this, MainMenu.class);
        startActivity(intent);
    }
}
