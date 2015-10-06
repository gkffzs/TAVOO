package gr.upatras.ceid.kaffezas.tavoo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

// -------------------------------------------------------------------------------------------------
// This class is responsible for the auto-check-out functionality in PlayLocationActivity. ---------
// -------------------------------------------------------------------------------------------------
public class AutoCheckOutReceiver extends BroadcastReceiver { // -----------------------------------

    // ---------------------------------------------------------------------------------------------
    @Override // Method that is executed when the alarm wakes. -------------------------------------
    public void onReceive(Context context, Intent intent) {
        Log.d("AutoCheckOutReceiver", "onReceive()");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Integer loggedInUserID = sharedPreferences.getInt("user_id", 0);
        Integer checkInLocationID = sharedPreferences.getInt("check_in_location_id", 0);
        Log.d("AutoCheckOutReceiver", "loggedInUserID: " + loggedInUserID);
        Log.d("AutoCheckOutReceiver", "checkInLocationID: " + checkInLocationID);

        CheckOutTask checkOutTask = new CheckOutTask();
        checkOutTask.execute(ConnectionManager.getCheckOutURL(loggedInUserID, checkInLocationID));

        editor.remove("check_in_location_id");
        editor.apply();
        Log.d("AutoCheckOutReceiver", "just deleted check_in_location_id");
    }

    // ---------------------------------------------------------------------------------------------
    // AsyncTask for performing check-out from a location. -----------------------------------------
    private class CheckOutTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectionManager.getData(params[0]);
        }

        protected void onPostExecute(String result) {
            try {
                JSONObject jObject = new JSONObject(result);
                Log.d("AutoCheckOutReceiver", "valid check-out (success=" + jObject.getInt("success") + ")");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
