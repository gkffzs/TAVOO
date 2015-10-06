package gr.upatras.ceid.kaffezas.tavoo;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.json.JSONException;

import java.util.Comparator;
import java.util.List;

// -------------------------------------------------------------------------------------------------
// This activity displays all vets in an alphabetically-ordered list. ------------------------------
// It is placed in the second tab of VetActivity. --------------------------------------------------
// -------------------------------------------------------------------------------------------------
public class VetSecondActivity extends ListActivity { // -------------------------------------------

    // Declaration of variables used to check whether a user is logged-in. -------------------------
    SharedPreferences sharedPreferences;
    Integer loggedInUserID;
    String activityTag;

    // Declaration of a List where the retrieved Vet items will be saved. --------------------------
    List<Vet> vetList;

    // ---------------------------------------------------------------------------------------------
    @Override // Main method that executes when the activity starts. -------------------------------
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_second);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(VetSecondActivity.this);
        loggedInUserID = sharedPreferences.getInt("user_id", 0);
        activityTag = getString(R.string.activ_vet_sec_tag);
        AppServices.loggingAction(loggedInUserID, activityTag, getString(R.string.act_create_activ_tag), 0);

        try {
            String tempFile = AppServices.readLocalFile(VetSecondActivity.this, R.raw.vets);
            vetList = VetJSONParser.parseFeed(tempFile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateListDisplay();
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
    // Method for updating the vets' list, particularly ordering it alphabetically. ----------------
    protected void updateListDisplay() {
        VetAdapter adapter = new VetAdapter(this, R.layout.item_vet, vetList);
        adapter.sort(new Comparator<Vet>() {
            @Override
            public int compare(Vet lhs, Vet rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        setListAdapter(adapter);
    }
}