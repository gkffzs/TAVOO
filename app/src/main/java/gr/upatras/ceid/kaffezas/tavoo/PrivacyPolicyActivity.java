package gr.upatras.ceid.kaffezas.tavoo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

// -------------------------------------------------------------------------------------------------
// This activity is responsible for displaying the privacy policy of the application. --------------
// -------------------------------------------------------------------------------------------------
public class PrivacyPolicyActivity extends Activity { // -------------------------------------------

    // Declaration of variables used to check whether a user is logged-in. -------------------------
    SharedPreferences sharedPreferences;
    Integer loggedInUserID;
    String activityTag;

    // ---------------------------------------------------------------------------------------------
    @Override // Main method that executes when the activity starts. -------------------------------
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PrivacyPolicyActivity.this);
        loggedInUserID = sharedPreferences.getInt("user_id", 0);
        activityTag = getString(R.string.activ_pri_pol_tag);
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
}
