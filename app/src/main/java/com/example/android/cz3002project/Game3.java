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
import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
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

public class Game3 extends ActionBarActivity {
    TextView mStatusView;
    MediaRecorder mRecorder;
    Thread runner;
    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;
    public int seconds = 10;

    private ProgressBar progressBar;
    private double progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    private double highestScore;

    final Runnable updater = new Runnable(){

        public void run(){
            //updateDb();
        };
    };
    final Handler mHandler = new Handler();


    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    String email;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private static String url_update_score3 = "http://10.27.44.239/update_score3.php";
    private static String url_read_user = "http://10.27.44.239/read_user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game3);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        email = preferences.getString("Email", "");


        highestScore = 0;
        mStatusView = (TextView) findViewById(R.id.status);


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

                        if(seconds==0) {
                            t.cancel();
                            if (CheckNetworkConnection.checknetwork(getApplicationContext()))
                                new UpdateScore3().execute();
                            else
                                Toast.makeText(Game3.this, "No Internet Connection to upload your score!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        seconds -= 1;
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


        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (seconds > 0) {
                    if(seconds == 0) {
                        break;
                    }


                    // process some tasks
                    progressBarStatus = doSomeTasks();

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


    public double doSomeTasks() {

        double currentScore = soundDb(0.7746)/92.0 * 100.00;
        if(currentScore > 100)
            currentScore = 100;
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

            //mEMA = 0.0;
        }

    }
    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void updateTv(){
        mStatusView.setText(Double.toString((getAmplitudeEMA())) + " dB");
    }
    public void updateDb() {
        mStatusView.setText(Double.toString(soundDb(0.7746)));
    }



    public double soundDb(double ampl){
        return  20 * Math.log10(getAmplitude() / ampl);
    }
    public double getAmplitude() {
        if (mRecorder != null) {

            return  (mRecorder.getMaxAmplitude());}
        else
            return 0;

    }
    public double getAmplitudeEMA() {
        double amp =  getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }


    class UpdateScore3 extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Game3.this);
            pDialog.setMessage("Updating Score for Game 3...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
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

                    average_game3 = Double.parseDouble(jo.getString("average_game3"));
                    play_time3 = Double.parseDouble(jo.getString("play_time3"));
                    total = average_game3 * play_time3 + highestScore;
                    average_game3 = total / (play_time3 + 1);

                    // closing this screen
                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Sending Data
            params.add(new BasicNameValuePair("score3", ((Double)highestScore).toString()));
            params.add(new BasicNameValuePair("average_game3", (average_game3.toString())));
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_score3,
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
                    // closing this screen
                    finish();
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
            pDialog.dismiss();
            Toast.makeText(Game3.this, "Your Score:\n" + String.format("%.2f", highestScore), Toast.LENGTH_LONG).show();
            finish();
            Intent i = new Intent(Game3.this, GamesMenu.class);
            startActivity(i);
        }

    }
}
