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


public class Statistics extends ActionBarActivity {
    SharedPreferences preferences;
    String email;
    SharedPreferences.Editor editor;

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static String url_read_user = "http://10.27.44.239/read_user.php";

    Double[] score = new Double[3];
    Double[] averageScore = new Double[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("Email", "");
        editor = preferences.edit();
        if(!email.equalsIgnoreCase(""))
            if (CheckNetworkConnection.checknetwork(getApplicationContext()))
                new RetrieveData().execute();
            else
                Toast.makeText(Statistics.this, "No Internet Connection!", Toast.LENGTH_LONG).show();
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
         * Creating product
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));


            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_read_user,
                    "GET", params);

            // check log cat fro response
            //Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {
                    // successfully created product
                    //Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
                    //startActivity(i);
                    JSONArray user = json.getJSONArray("user");
                    JSONObject jo = user.getJSONObject(0);
                    Log.e("Enter here", "success");
                    // retrieve score for each game
                    for(Integer i = 1;i<=3;++i)
                        score[i-1] = Double.parseDouble(jo.getString("score"+i.toString()));
                    Log.e("Score",score[2].toString());
                    // retrieve average score for each game
                    for(Integer i = 1;i<=3;++i)
                        averageScore[i-1] = Double.parseDouble(jo.getString("average_game"+i.toString()));

                    // closing this screen
                    //finish();
                } else {
                    // failed to create product
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
        Intent intent = new Intent(Statistics.this, MainMenu.class);
        startActivity(intent);
    }
}
