package gr.upatras.ceid.kaffezas.tavoo;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;

// -------------------------------------------------------------------------------------------------
// This class provides some methods that can be used throughout the application. -------------------
// -------------------------------------------------------------------------------------------------
public class AppServices { // ----------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // Method that displays a custom Toast message to the user. ------------------------------------
    public static Toast displayToast(Context contextFrom, String messageToBeDisplayed) {
        Toast toast = Toast.makeText(contextFrom, messageToBeDisplayed, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        return toast;
    }

    // ---------------------------------------------------------------------------------------------
    // Method that reads a file that is stored locally (i.e. /raw/vets.json). ----------------------
    public static String readLocalFile(Context contextFrom, Integer resourceID) {
        InputStream inputStream = contextFrom.getResources().openRawResource(resourceID);
        Writer writer = new StringWriter();
        char[] buffer = new char[2048];

        try {
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writer.toString();
    }

    // ---------------------------------------------------------------------------------------------
    // Method that is used for logging the actions of a user. --------------------------------------
    public static void loggingAction(Integer userID, String activityTag, String actionTag, Integer relatedID) {
        Long currentTimestamp = Calendar.getInstance().getTimeInMillis();
        LoggingTask loggingTask = new LoggingTask();
        loggingTask.execute(ConnectionManager.getLoggingURL(userID, activityTag, actionTag, relatedID, currentTimestamp));
    }

    // ---------------------------------------------------------------------------------------------
    // AsyncTask for retrieving and parsing the directions between two locations. ------------------
    private static class LoggingTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectionManager.getData(params[0]);
        }

        protected void onPostExecute(String result) {
        }
    }

}