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
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USER = "user";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_NAME = "name";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Button logOutButton;
    Button signInButton;
    TextView userNameText;
    JSONParser jParser = new JSONParser();
    private static String url_read_user = "http://10.27.44.239/read_user.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        String name = preferences.getString("Name", "");
        logOutButton = (Button) findViewById(R.id.homeButtonLogOut);
        signInButton = (Button) findViewById(R.id.homeButtonSignIn);
        userNameText = (TextView) findViewById(R.id.homeTVUserName);
        if(!name.equalsIgnoreCase("")) {
            signInButton.setText("Change User");
            logOutButton.setVisibility(View.VISIBLE);
            userNameText.setText(name);
        }
        else {
            logOutButton.setVisibility(View.GONE);
            userNameText.setText("");
        }
        //new LoadAllProducts().execute();

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
        if(!name.equalsIgnoreCase("")) {
            signInButton.setText("Change User");
            logOutButton.setVisibility(View.VISIBLE);
            userNameText.setText(name);
        }
        else {
            logOutButton.setVisibility(View.GONE);
            userNameText.setText("");
        }
    }

    public void SignUp(View view)
    {
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void LogIn(View view)
    {
        Intent intent = new Intent(MainActivity.this, LogIn.class);
        startActivity(intent);
    }

    public void SkipNow(View view) {
        Intent intent = new Intent(MainActivity.this, MainMenu.class);
        startActivity(intent);
    }

    public void LogOut(View view)
    {
        editor.putString("Name", "");
        editor.apply();
        finish();
        startActivity(getIntent());
    }

    class LoadAllProducts extends AsyncTask<String, String, String> {
        String password = null, name = null;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            params.add(new BasicNameValuePair("email","\"sample@gmail.com\""));
            JSONObject json = jParser.makeHttpRequest(url_read_user, "GET", params);

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    JSONArray user = json.getJSONArray(TAG_USER);

                    // looping through All Products
                    for (int i = 0; i < user.length(); i++) {
                        JSONObject c = user.getJSONObject(i);

                        // Storing each json item in variable
                        password = c.getString(TAG_PASSWORD);
                        name = c.getString(TAG_NAME);


                    }

                } else {
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
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                }
            });

        }

    }
}
