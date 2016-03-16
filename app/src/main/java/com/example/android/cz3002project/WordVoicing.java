package com.example.android.cz3002project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is used to handle Word Voicing Activity
 */
public class WordVoicing extends ActionBarActivity {
    MediaRecorder mRecorder;
    Thread runner;
    public int seconds = 10;

    // progress bar animation
    private ProgressBar progressBar;
    private double progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    private double highestScore; // to keep track of highest score for this game

    final Runnable updater = new Runnable(){

        public void run(){
        };
    };
    final Handler mHandler = new Handler();

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    String email;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    // urls to handle database query with PHP
    private static String url_update_score3 = "http://10.27.44.239/update_score3.php";
    private static String url_read_user = "http://10.27.44.239/read_user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_voicing);

        // get shared preferences data
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        email = preferences.getString("Email", "");

        highestScore = 0;


        // start the audio input from microphone
        if (runner == null)
        {
            runner = new Thread(){
                public void run()
                {
                    while (runner != null)
                    {
                        try
                        {
                            Thread.sleep(1000);
                            Log.i("Noise", "Tock");
                        } catch (InterruptedException e) { };
                        mHandler.post(updater);
                    }
                }
            };
            runner.start();
            Log.d("Noise", "start runner()");
        }

        // Declare the timer
        final Timer t = new Timer();

        // Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // terminate the timer
                        if(seconds==0) {
                            t.cancel();
                            // if there is internet connection
                            if (CheckNetworkConnection.checknetwork(getApplicationContext()))
                                new UpdateScore3().execute();
                            else
                                Toast.makeText(WordVoicing.this, "No Internet Connection to upload your score!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        seconds -= 1;
                        // update the timer
                        TextView tv = (TextView) findViewById(R.id.game3TextViewTimer);
                        tv.setText(String.format("%02d", seconds));
                    }
                });
            }
        }, 0, 1000);


        // Animation bar

        // prepare for a progress bar dialog
        progressBar = (ProgressBar) findViewById(R.id.game3ProgressBarPB);
        progressBar.setProgress(0);

        progressBar.setMax(50);

        //reset progress bar status
        progressBarStatus = 0;

        // Thread to update progress bar animation
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (seconds > 0) {
                    if(seconds == 0) {
                        break;
                    }
                    // process some tasks
                    progressBarStatus = getScore();

                    // Update the progress bar
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress((int) progressBarStatus);
                        }
                    });
                }
            }
        });
        thread.start();
    }

    // get score from current second
    public double getScore() {
        double currentScore = soundDb(0.7746)/92.0 * 100.00;
        // to handle precision error
        if(currentScore > 100)
            currentScore = 100;

        // update highest score if current score is higher
        if(currentScore > highestScore)
            highestScore = currentScore;
        return soundDb(currentScore);
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
    public void onBackPressed()
    {
        seconds = 0;
    }

    public void onResume()
    {
        super.onResume();
        startRecorder();
    }

    public void onPause()
    {
        super.onPause();
        stopRecorder();
    }

    // to start the microphone input
    public void startRecorder(){
        if (mRecorder == null)
        {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try
            {
                mRecorder.prepare();
            }catch (java.io.IOException ioe) {
                android.util.Log.e("[Monkey]", "IOException: " + android.util.Log.getStackTraceString(ioe));

            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
            }
            try
            {
                mRecorder.start();
            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
            }
        }

    }

    // stop microphone
    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    // get sound decibel
    public double soundDb(double ampl){
        return  20 * Math.log10(getAmplitude() / ampl);
    }

    // get sound amplitude
    public double getAmplitude() {
        if (mRecorder != null) {
            return  (mRecorder.getMaxAmplitude());}
        else
            return 0;
    }

    /**
     * Async class to query database to update database
     */
    class UpdateScore3 extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(WordVoicing.this);
            pDialog.setMessage("Updating Score for Game 3...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Retrieving Data
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            Double average_game3 = 0.0, play_time3 = 0.0, total = 0.0;
            JSONObject prevData = jsonParser.makeHttpRequest(url_read_user, "GET", params);
            try {
                int success = prevData.getInt("success");

                if (success == 1) {
                    JSONArray user = prevData.getJSONArray("user");
                    JSONObject jo = user.getJSONObject(0);

                    // get user's previous score
                    average_game3 = Double.parseDouble(jo.getString("average_game3"));
                    play_time3 = Double.parseDouble(jo.getString("play_time3"));
                    total = average_game3 * play_time3 + highestScore;

                    // update it
                    average_game3 = total / (play_time3 + 1);

                } else {
                    // some error occurred
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Build the parameters
            params.add(new BasicNameValuePair("score3", ((Double)highestScore).toString()));
            params.add(new BasicNameValuePair("average_game3", (average_game3.toString())));

            // getting JSON Object
            // send data to update database and get the return value to check the status
            JSONObject json = jsonParser.makeHttpRequest(url_update_score3,
                    "GET", params);

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            Toast.makeText(WordVoicing.this, "Your Score:\n" + String.format("%.2f", highestScore), Toast.LENGTH_LONG).show();
            finish();
            // go to Games Menu activity
            Intent i = new Intent(WordVoicing.this, GamesMenu.class);
            startActivity(i);
        }
    }
}
