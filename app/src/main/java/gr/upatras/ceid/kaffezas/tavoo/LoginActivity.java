package gr.upatras.ceid.kaffezas.tavoo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// -------------------------------------------------------------------------------------------------
// This activity is used for the handling of the login process of a user. It displays the form and -
// sends the data to the server, via SessionManager. -----------------------------------------------
// -------------------------------------------------------------------------------------------------
public class LoginActivity extends Activity { // ---------------------------------------------------

    // Declaration of variables used to check whether a user is logged-in. -------------------------
    SharedPreferences sharedPreferences;
    Integer loggedInUserID;
    String activityTag;

    // Declaration of items that are used throughout the registration process. ---------------------
    EditText userName, userPassword;
    String tempUserName, tempUserPassword;
    SessionManager sessionManager = new SessionManager();
    ProgressDialog progressDialog;

    // ---------------------------------------------------------------------------------------------
    @Override // Main method that executes when the activity starts. -------------------------------
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        loggedInUserID = sharedPreferences.getInt("user_id", 0);
        activityTag = getString(R.string.activ_log_tag);
        AppServices.loggingAction(loggedInUserID, activityTag, getString(R.string.act_create_activ_tag), 0);

        userName = (EditText) findViewById(R.id.insert_username);
        userName.setHint(R.string.log_hint_username);
        userPassword = (EditText) findViewById(R.id.insert_password);
        userPassword.setHint(R.string.log_hint_password);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppServices.loggingAction(loggedInUserID, activityTag, getString(R.string.act_start_activ_tag), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppServices.loggingAction(loggedInUserID, activityTag, getString(R.string.act_resume_activ_tag), 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppServices.loggingAction(loggedInUserID, activityTag, getString(R.string.act_pause_activ_tag), 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppServices.loggingAction(loggedInUserID, activityTag, getString(R.string.act_stop_activ_tag), 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppServices.loggingAction(loggedInUserID, activityTag, getString(R.string.act_stop_activ_tag), 0);
    }


    // ---------------------------------------------------------------------------------------------
    // Click-handler for the login button. Starts a new LoginTask. ---------------------------------
    public void loginButtonClickHandler(View view) {
        tempUserName = userName.getText().toString();
        tempUserPassword = userPassword.getText().toString();
        LoginTask loginTask = new LoginTask();
        loginTask.execute();
    }

    // ---------------------------------------------------------------------------------------------
    // Click-handler for the register button. Leads to RegisterActivity. ---------------------------
    public void registerButtonClickHandler(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    // ---------------------------------------------------------------------------------------------
    // AsyncTask for sending the user information to the server and receiving its response. It -----
    // also displays a ProgressDialog for letting the user know. -----------------------------------
    class LoginTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Γίνεται σύνδεση...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;
            try {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("username", tempUserName));
                params.add(new BasicNameValuePair("password", tempUserPassword));

                Log.d("LoginActivity", "starting request");
                JSONObject jObject = sessionManager.sendRequest(ConnectionManager.getLoginURL(), "POST", params);
                Log.d("LoginActivity", "login attempt " + jObject.toString());

                success = jObject.getInt(ConnectionManager.getTagSuccess());
                if (success == 1) {
                    Log.d("LoginActivity", "successful login! " + jObject.toString());

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username", tempUserName);
                    Log.d("LoginActivity", tempUserName + " has ID #" + jObject.getInt(ConnectionManager.getTagUserID()));
                    editor.putInt("user_id", jObject.getInt(ConnectionManager.getTagUserID()));
                    editor.apply();
                    Log.d("LoginActivity", "successfully saved in SharedPreferences");

                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(mainIntent);
                    return jObject.getString(ConnectionManager.getTagMessage());
                } else {
                    Log.d("LoginActivity", "login failed! " + jObject.toString());
                    return jObject.getString(ConnectionManager.getTagMessage());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result != null) {
                AppServices.displayToast(LoginActivity.this, result);
            }
        }
    }
}