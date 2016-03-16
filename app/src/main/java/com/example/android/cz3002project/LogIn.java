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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to handle Login Activity
 */
public class LogIn extends ActionBarActivity {
    EditText inputEmail;
    EditText inputPassword;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();

    // url to access the database with PHP
    private static String url_read_user = "http://10.27.44.239/read_user.php";
    private static final String TAG_SUCCESS = "success";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_in, menu);
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
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }


    public void UserLogIn(View view)
    {
        // Log In Process
        inputEmail = (EditText) findViewById(R.id.logInEditTextEmail);
        inputPassword = (EditText) findViewById(R.id.logInEditTextPassword);

        // check for internet connection first
        if (CheckNetworkConnection.checknetwork(getApplicationContext()))
            new UserLogInProcess().execute();
        else
            Toast.makeText(LogIn.this, "No Internet Connection!", Toast.LENGTH_LONG).show();

    }

    /**
     * Async Class to handle database query
     */
    class UserLogInProcess extends AsyncTask<String, String, String> {
        int status = 0; // 0: error, 1: successful, 2: failed
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LogIn.this);
            pDialog.setMessage("Log In...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Send data for login
         * */
        protected String doInBackground(String... args) {
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));


            // getting JSON Object
            // Retrieve the user data based on the email
            JSONObject json = jsonParser.makeHttpRequest(url_read_user,
                    "GET", params);

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    // retrieve the user data
                    JSONArray user = json.getJSONArray("user");
                    JSONObject jo = user.getJSONObject(0);
                    String retrievedPassword = jo.getString("password");

                    // if the password is correct:
                    if(retrievedPassword.compareTo(password) == 0) {
                        String retrievedName = jo.getString("name");
                        String retrievedEmail = jo.getString("email");
                        // update the shared preferences
                        editor.putString("Name", retrievedName);
                        editor.putString("Email", retrievedEmail);
                        editor.apply();
                        status = 1;
                    }
                    else {
                        // failed login
                        status = 2;
                    }
                } else {
                    // error occurred
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
            pDialog.dismiss();

            // give feedback based on login result
            if(status == 0)
                Toast.makeText(LogIn.this, "Some error occurred!", Toast.LENGTH_LONG).show();
            else if(status == 1)
                Toast.makeText(LogIn.this, "Login Successful!", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(LogIn.this, "Login Failed!", Toast.LENGTH_LONG).show();
            finish();

            // move to main activity
            Intent i = new Intent(LogIn.this, MainActivity.class);
            startActivity(i);
        }
    }
}
