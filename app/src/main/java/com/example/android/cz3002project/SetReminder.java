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
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SetReminder extends ActionBarActivity {
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    String email;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private static String url_update_reminder_period = "http://10.27.44.239/update_reminder_period.php";
    NumberPicker np;
    private int reminderPeriod = 0;
    int status = 0; // 0 if failed, 1 if success

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_reminder);
        np = (NumberPicker) findViewById(R.id.setReminderNumberPickerNP);
        np.setMinValue(0);
        np.setMaxValue(30);

        np.setOnValueChangedListener(new OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub
                reminderPeriod = newVal;
            }
        });

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        email = preferences.getString("Email", "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_reminder, menu);
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

    public void SetReminder(View view) {
        if(!email.equalsIgnoreCase("")) {
            if (CheckNetworkConnection.checknetwork(getApplicationContext()))
                new UpdateReminderPeriod().execute();
            else
                Toast.makeText(SetReminder.this, "No Internet Connection!", Toast.LENGTH_LONG).show();
        }
    }

    class UpdateReminderPeriod extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SetReminder.this);
            pDialog.setMessage("Updating Reminder Period...");
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
            params.add(new BasicNameValuePair("reminder_period", ((Integer)reminderPeriod).toString()));
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_reminder_period,
                    "GET", params);

            // check log cat fro response
            //Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {
                    status = 1;
                } else {
                    status = 0;
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
            if(status == 1)
                Toast.makeText(SetReminder.this, "Reminder Duration has been updated", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(SetReminder.this, "Some error occurred...", Toast.LENGTH_LONG).show();
            finish();
            Intent i = new Intent(SetReminder.this, MainMenu.class);
            startActivity(i);
        }

    }
}
