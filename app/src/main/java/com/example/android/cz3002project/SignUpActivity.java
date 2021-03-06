package com.example.android.cz3002project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to handle Sign Up Acitivity
 */
public class SignUpActivity extends ActionBarActivity {
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    // attributes to get user data from text fields
    String inputName;
    String inputEmail;
    String inputPhoneNumber;
    String inputPassword;
    String inputRepeatPassword;

    // url to handle database query with PHP
    private static String url_create_user = "http://10.27.44.239/create_user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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

    // Handle user sign up process
    public void UserSignUp(View view)
    {
        // get the data from text fields
        inputName = ((EditText) findViewById(R.id.signUpEditTextName)).getText().toString();
        inputEmail = ((EditText) findViewById(R.id.signUpEditTextEmail)).getText().toString();
        inputPhoneNumber = ((EditText) findViewById(R.id.signUpEditTextPhoneNumber)).getText().toString();
        inputPassword = ((EditText) findViewById(R.id.signUpEditTextPassword)).getText().toString();
        inputRepeatPassword = ((EditText) (findViewById(R.id.signUpEditTextRepeatPassword))).getText().toString();

        // If some text fields are empty
        if(inputName.equals("") || inputEmail.equals("") || inputPhoneNumber.equals("")
                || inputPassword.equals("") || inputRepeatPassword.equals("")) {
            Toast.makeText(SignUpActivity.this, "All fields are required!", Toast.LENGTH_LONG).show();
            finish();
            startActivity(getIntent());
        }
        // if password and repeat password are matched
        else if(inputPassword.compareTo(inputRepeatPassword) == 0) {
            // if there is internet connection
            if (CheckNetworkConnection.checknetwork(getApplicationContext()))
                new CreateNewUser().execute();
            else
                Toast.makeText(SignUpActivity.this, "No Internet Connection!", Toast.LENGTH_LONG).show();
        }
        // if password and repeat password do not matched
        else {
            Toast.makeText(SignUpActivity.this, "Passwords do not match!", Toast.LENGTH_LONG).show();
            finish();
            startActivity(getIntent());
        }

    }

    /**
     * Async Class to handle database query to perform signing up
     */
    class CreateNewUser extends AsyncTask<String, String, String> {
        int status = 0; // 0 for failed login, 1 if success
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignUpActivity.this);
            pDialog.setMessage("Creating User Account..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Sending data
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", inputName));
            params.add(new BasicNameValuePair("email", inputEmail));
            params.add(new BasicNameValuePair("phone_number", inputPhoneNumber));
            params.add(new BasicNameValuePair("password", inputPassword));

            // getting JSON Object
            // send the data and get the return value to check the status
            JSONObject json = jsonParser.makeHttpRequest(url_create_user,
                    "GET", params);

            // check for success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {
                    status = 1; // success
                } else {
                    status = 0; // error occurred
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
                Toast.makeText(SignUpActivity.this, "User Account Successfully Created!", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(SignUpActivity.this, "Some error occurred!", Toast.LENGTH_LONG).show();
            finish();
            // go to Main Activity
            Intent i = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(i);
        }

    }
}
