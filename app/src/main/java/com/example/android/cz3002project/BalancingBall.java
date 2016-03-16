package com.example.android.cz3002project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

public class BalancingBall extends Activity implements SensorEventListener{

    CustomDrawableView mCustomDrawableView = null;
    ShapeDrawable mDrawable = new ShapeDrawable();
    public float xPosition, xAcceleration,xVelocity = 0.0f;
    public float yPosition, yAcceleration,yVelocity = 0.0f;
    public float xmax,ymax;
    private Bitmap mBitmap;
    private SensorManager sensorManager = null;
    public float frameTime = 0.666f;
    public float CONS = 25;
    public float FRICTION = 100;
    public int seconds = 15;
    public int minutes = 0;
    public int SIZE_CONS = 100;
    public int SIZE_TARGET = 200;
    public double maxScore = 0;

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    String email;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    // urls to handle database query with PHP
    private static String url_update_score1 = "http://10.27.44.239/update_score1.php";
    private static String url_read_user = "http://10.27.44.239/read_user.php";


    public boolean isInside = false;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        //Set FullScreen & portrait
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // get shared preferences data
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        email = preferences.getString("Email", "");


        // Get a reference to a SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_FASTEST);

        mCustomDrawableView = new CustomDrawableView(this);
        setContentView(mCustomDrawableView);
        // setContentView(R.layout.activity_main);

        //Calculate Boundry
        Display display = getWindowManager().getDefaultDisplay();
        xmax = (float)display.getWidth();
        ymax = (float)display.getHeight();
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
                                new UpdateScore1().execute();
                            else
                                Toast.makeText(BalancingBall.this, "No Internet Connection to upload your score!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        seconds -= 1;
                    }
                });
            }
        }, 0, 1000);
    }

    // This method will update the UI on new sensor events
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                //Set sensor values as acceleration
                yAcceleration = sensorEvent.values[1]/CONS;
                xAcceleration = sensorEvent.values[2]/CONS;
                //if (Math.abs(xAcceleration) < 1/CONS){xAcceleration = 0;}
                //if (Math.abs(yAcceleration) < 1/CONS){yAcceleration = 0;}
                updateBall();
            }
        }
    }

    private boolean isInsideCircle (float x1, float y1, float radius1, float x2, float y2, float radius2){
        float maxThreshold = radius2 - radius1;
        float sqrThreshold = maxThreshold * maxThreshold;
        float xDiff = x1 - x2;
        float yDiff = y1 - y2;
        float sqrDist = (xDiff * xDiff) + (yDiff * yDiff);
        if (sqrDist < sqrThreshold){
            updateScore(sqrDist, sqrThreshold);
            return true;
        }
        else
            return false;
    }

    private void updateScore(float dist, float maxDist){
        double currentScore = ((maxDist - dist)/maxDist) * 100;
        if (currentScore > maxScore)
            maxScore = currentScore;
    }


    private void updateBall() {
        Log.e("Score: ", ((Double) maxScore).toString());

        //Calculate new speed
        xVelocity += (xAcceleration * frameTime);
        yVelocity += (yAcceleration * frameTime);
        xVelocity = updateFriction(xVelocity);
        yVelocity = updateFriction(yVelocity);
        float xS = (xVelocity/2)*frameTime;
        float yS = (yVelocity/2)*frameTime;

        //check if inside
        isInside = isInsideCircle(xPosition + SIZE_CONS/2, yPosition + SIZE_CONS/2, SIZE_CONS/2, xmax/2, ymax/2, SIZE_TARGET/2);

        //Add to position negative due to sensor
        //readings being opposite to what we want!
        xPosition -= xS;
        yPosition -= yS;

        // if (xPosition*xPosition + yPosition*yPosition < )
        if (xPosition > xmax - SIZE_CONS) {
            xVelocity *= -0.5;
            xPosition = xmax - SIZE_CONS;
        } else if (xPosition < 0) {
            xVelocity *= -0.5;
            xPosition = 0;
        }
        if (yPosition > ymax - SIZE_CONS) {
            yPosition = ymax - SIZE_CONS;
            yVelocity *=  -0.5;
        } else if (yPosition < 0) {
            yPosition = 0;
            yVelocity *= -0.5;
        }
    }

    public float updateFriction(float velocity){
        if (velocity > 0)
            return (float) (velocity - frameTime/FRICTION);
        else
            return (float) (velocity + frameTime/FRICTION);

    }


    // I've chosen to not implement this method
    public void onAccuracyChanged(Sensor arg0, int arg1)
    {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onStop()
    {
        // Unregister the listener
        sensorManager.unregisterListener(this);
        super.onStop();
    }

    public class CustomDrawableView extends View
    {
        public CustomDrawableView(Context context)
        {
            super(context);
            Bitmap ball = BitmapFactory.decodeResource(getResources(), R.mipmap.ball);
            final int dstWidth = SIZE_CONS;
            final int dstHeight = SIZE_CONS;
            mBitmap = Bitmap.createScaledBitmap(ball, dstWidth, dstHeight, true);

            mDrawable = new ShapeDrawable(new OvalShape());

            mDrawable.getPaint().setColor(0xff74AC23);
            mDrawable.setBounds(400, 600, 600, 700);
        }

        protected void onDraw(Canvas canvas)
        {
            int tRadius = SIZE_TARGET/2;
            RectF oval = new RectF(xmax/2-tRadius, ymax/2-tRadius, xmax/2+tRadius, ymax/2+tRadius
            ); // set bounds of rectangle
            Paint p = new Paint(); // set some paint options
            if (isInside)
                p.setColor(Color.GREEN);
            else
                p.setColor(Color.BLUE);

            canvas.drawOval(oval, p);
            final Bitmap bitmap = mBitmap;
            canvas.drawBitmap(bitmap, xPosition, yPosition, null);
            invalidate();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    /**
     * Async class to query database to update database
     */
    class UpdateScore1 extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BalancingBall.this);
            pDialog.setMessage("Updating Score for Game 1...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Retrieving Data
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            Double average_game1 = 0.0, play_time1 = 0.0, total = 0.0;
            JSONObject prevData = jsonParser.makeHttpRequest(url_read_user, "GET", params);
            try {
                int success = prevData.getInt("success");

                if (success == 1) {
                    JSONArray user = prevData.getJSONArray("user");
                    JSONObject jo = user.getJSONObject(0);

                    // get user's previous score
                    average_game1 = Double.parseDouble(jo.getString("average_game1"));
                    play_time1 = Double.parseDouble(jo.getString("play_time1"));
                    total = average_game1 * play_time1 + maxScore;

                    // update it
                    average_game1 = total / (play_time1 + 1);

                } else {
                    // some error occurred
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Build the parameters
            params.add(new BasicNameValuePair("score1", ((Double)maxScore).toString()));
            params.add(new BasicNameValuePair("average_game1", (average_game1.toString())));

            // getting JSON Object
            // send data to update database and get the return value to check the status
            JSONObject json = jsonParser.makeHttpRequest(url_update_score1,
                    "GET", params);

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            Toast.makeText(BalancingBall.this, "Your Score:\n" + String.format("%.2f", maxScore), Toast.LENGTH_LONG).show();
            finish();
            // go to Games Menu activity
            Intent i = new Intent(BalancingBall.this, GamesMenu.class);
            startActivity(i);
        }
    }
}