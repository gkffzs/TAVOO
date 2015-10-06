package gr.upatras.ceid.kaffezas.tavoo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

// -------------------------------------------------------------------------------------------------
// This is the main activity of the application, it's the landing activity from the splash screen --
// in the beginning of the application. Displays a welcome message and has a bottom row with -------
// buttons leading to the other important activities. ----------------------------------------------
// -------------------------------------------------------------------------------------------------
public class MainActivity extends Activity { // ----------------------------------------------------

    // Declaration of variables used to check whether a user is logged-in. -------------------------
    SharedPreferences sharedPreferences;
    Integer loggedInUserID;
    String activityTag;

    // ---------------------------------------------------------------------------------------------
    @Override // Main method that executes when the activity starts. -------------------------------
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        loggedInUserID = sharedPreferences.getInt("user_id", 0);
        activityTag = getString(R.string.activ_main_tag);
        AppServices.loggingAction(loggedInUserID, activityTag, getString(R.string.act_create_activ_tag), 0);
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
    @Override // Method that generates the menu when the activity starts. --------------------------
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // ---------------------------------------------------------------------------------------------
    @Override // Method that handles the different options of the menu. ----------------------------
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_login) {
            if (ConnectionManager.isOnline(this)) {
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
            }
        } else if (id == R.id.action_logout) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Integer loggedInUserID = sharedPreferences.getInt("user_id", 0);
            Integer checkInLocationID = sharedPreferences.getInt("check_in_location_id", 0);
            if (checkInLocationID != 0) {
                ConnectionManager.getCheckOutURL(loggedInUserID, checkInLocationID);
            }
            editor.clear();
            editor.apply();
            AppServices.displayToast(this, "Επιτυχής αποσύνδεση!");
        } else if (id == R.id.action_google_play_licence) {
            if (ConnectionManager.isOnline(this)) {
                Intent gpsLicenceIntent = new Intent(this, GooglePlayLicenceActivity.class);
                startActivity(gpsLicenceIntent);
            }
        } else if (id == R.id.action_app_licence) {
            Intent appLicenceIntent = new Intent(this, AppLicenceActivity.class);
            startActivity(appLicenceIntent);
        } else if (id == R.id.action_privacy_policy) {
            Intent privacyPolicyIntent = new Intent(this, PrivacyPolicyActivity.class);
            startActivity(privacyPolicyIntent);
        } else if (id == R.id.action_about) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
        }

        return true;
    }

    // ---------------------------------------------------------------------------------------------
    @Override // Method that specifies whether the login or logout option is available. ------------
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        String there_is_no_name = "none";
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String logged_in_username = sp.getString("username", there_is_no_name);
        if (logged_in_username.equals(there_is_no_name)) {
            menu.findItem(R.id.action_login).setVisible(true);
            menu.findItem(R.id.action_logout).setVisible(false);
        } else {
            menu.findItem(R.id.action_login).setVisible(false);
            menu.findItem(R.id.action_logout).setVisible(true);
        }

        return true;
    }

    // ---------------------------------------------------------------------------------------------
    // Method that handles the click on the profile button and leads to the respective activity. ---
    public void profileClickHandler(View view) {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent);
    }

    // ---------------------------------------------------------------------------------------------
    // Method that handles the click on the location button and leads to the respective activity. --
    public void locationClickHandler(View view) {
        if (ConnectionManager.isOnline(this)) {
            Intent playLocationIntent = new Intent(this, PlayLocationActivity.class);
            startActivity(playLocationIntent);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Method that handles the click on the vet button and leads to the respective activity. -------
    public void vetClickHandler(View view) {
        Intent vetIntent = new Intent(this, VetActivity.class);
        startActivity(vetIntent);
    }
}