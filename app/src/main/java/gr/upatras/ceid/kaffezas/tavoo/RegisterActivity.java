package gr.upatras.ceid.kaffezas.tavoo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// -------------------------------------------------------------------------------------------------
// This activity is used for the handling of the registration of a new user. It displays the form --
// and sends the data to the server, via SessionManager. -------------------------------------------
// -------------------------------------------------------------------------------------------------
public class RegisterActivity extends Activity { // ------------------------------------------------

    // Declaration of variables used to check whether a user is logged-in. -------------------------
    SharedPreferences sharedPreferences;
    Integer loggedInUserID;
    String activityTag;

    // Declaration of items that are used throughout the registration process. ---------------------
    EditText newUserName, newUserPassword;
    EditText newUserValidatePassword;
    String tempUserName, tempUserPassword;
    SessionManager sessionManager = new SessionManager();
    ProgressDialog progressDialog;

    // ---------------------------------------------------------------------------------------------
    @Override // Main method that executes when the activity starts. -------------------------------
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
        loggedInUserID = sharedPreferences.getInt("user_id", 0);
        activityTag = getString(R.string.activ_reg_tag);
        AppServices.loggingAction(loggedInUserID, activityTag, getString(R.string.act_create_activ_tag), 0);

        setRegisterFormHints();
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
    // Method for setting the hinds in the EditText fields of the form. ----------------------------
    public void setRegisterFormHints() {
        newUserName = (EditText) findViewById(R.id.insert_username);
        newUserName.setHint(R.string.reg_hint_username);
        newUserPassword = (EditText) findViewById(R.id.insert_password);
        newUserPassword.setHint(R.string.reg_hint_password);
        newUserValidatePassword = (EditText) findViewById(R.id.reenter_password);
        newUserValidatePassword.setHint(R.string.reg_hint_re_password);
    }

    // ---------------------------------------------------------------------------------------------
    // Click-handler for the registration button. Performs basic checks and if everything is as it -
    // should be, it sends the inserted data to the server, via RegisterTask. ----------------------
    public void registerButtonClickHandler(View view) {
        if (newUserName.getText().toString().equals("")) {
            AppServices.displayToast(this, "Πρέπει να εισάγετε ένα όνομα χρήστη");
        } else if (newUserPassword.getText().toString().equals("")) {
            AppServices.displayToast(this, "Πρέπει να εισάγετε έναν κωδικό");
        } else if (newUserValidatePassword.getText().toString().equals("")) {
            AppServices.displayToast(this, "Πρέπει να επιβεβαιώσετε τον κωδικό");
        } else {
            if (newUserPassword.getText().toString().equals(newUserValidatePassword.getText().toString())) {
                tempUserName = newUserName.getText().toString();
                tempUserPassword = newUserPassword.getText().toString();
                RegisterTask registerTask = new RegisterTask();
                registerTask.execute();
            } else {
                AppServices.displayToast(this, "Οι κωδικοί που έχετε εισαγάγει δεν ταιριάζουν μεταξύ τους");
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // AsyncTask for sending the user information to the server and receiving its response. It -----
    // also displays a ProgressDialog for letting the user know. -----------------------------------
    private class RegisterTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setMessage("Γίνεται καταχώρηση...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;
            try {
                List<NameValuePair> parameters = new ArrayList<>();
                parameters.add(new BasicNameValuePair("username", tempUserName));
                parameters.add(new BasicNameValuePair("password", tempUserPassword));

                JSONObject jObject = sessionManager.sendRequest(ConnectionManager.getRegisterURL(), "POST", parameters);

                success = jObject.getInt(ConnectionManager.getTagSuccess());
                if (success == 1) {
                    finish();
                    return jObject.getString(ConnectionManager.getTagMessage());
                } else {
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
                AppServices.displayToast(RegisterActivity.this, result);
            }
        }
    }
}