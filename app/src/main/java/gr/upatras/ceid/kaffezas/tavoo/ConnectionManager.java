package gr.upatras.ceid.kaffezas.tavoo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

// -------------------------------------------------------------------------------------------------
// This class is responsible for the communication between the application and the server, for the -
// majority of the activities. It includes several methods and basic information about the server. -
// -------------------------------------------------------------------------------------------------
public class ConnectionManager { // ----------------------------------------------------------------

    // Declaration of the localhost server, where TAVOO API is placed. -----------------------------
    private static final String LOCALHOST_URL = "/TAVOO-API/";

    // Declaration of important tags that are used below. ------------------------------------------
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_USER_ID = "user_id";

    public static String getTagSuccess() {
        return TAG_SUCCESS;
    }

    public static String getTagMessage() {
        return TAG_MESSAGE;
    }

    public static String getTagUserID() {
        return TAG_USER_ID;
    }

    // ---------------------------------------------------------------------------------------------
    // Method for checking if the user is connected on the internet. -------------------------------
    public static boolean isOnline(Context contextFrom) {
        ConnectivityManager connectivityManager = (ConnectivityManager) contextFrom.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            AppServices.displayToast(contextFrom, "Κανένα διαθέσιμο δίκτυο");
            return false;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Method that takes a URL as input, and returns the response of the server in String format. --
    public static String getData(String fromURL) {
        BufferedReader bufferedReader = null;

        try {
            URL targetURL = new URL(fromURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) targetURL.openConnection();

            StringBuilder stringBuilder = new StringBuilder();
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Method that returns the URL for the login functionality. ------------------------------------
    public static String getLoginURL() {
        return LOCALHOST_URL + "login.php";
    }

    // ---------------------------------------------------------------------------------------------
    // Method that returns the URL for the register functionality. ---------------------------------
    public static String getRegisterURL() {
        return LOCALHOST_URL + "register.php";
    }

    // ---------------------------------------------------------------------------------------------
    // Method that returns the URL for the play locations. -----------------------------------------
    public static String getPlayLocationsURL() {
        return LOCALHOST_URL + "play_locations.php";
    }

    // ---------------------------------------------------------------------------------------------
    // Method that returns the URL for the vets. ---------------------------------------------------
    public static String getVetsURL() {
        return LOCALHOST_URL + "vets.json";
    }

    // ---------------------------------------------------------------------------------------------
    // Method that returns the URL for the request to Google Directions API. -----------------------
    public static String getDirectionsURL(LatLng ini, LatLng fin) {
        String iniString = "origin=" + ini.latitude + "," + ini.longitude;
        String finString = "destination=" + fin.latitude + "," + fin.longitude;
        String sensor = "sensor=false";
        String mode = "mode=walking";
        String parameters = iniString + "&" + finString + "&" + sensor + "&" + mode;
        String output = "json";
        return "http://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }

    // ---------------------------------------------------------------------------------------------
    // Method that returns the URL for the check-in functionality. ---------------------------------
    public static String getCheckInURL(Integer user_id, Integer location_id) {
        String userID = "user_id=" + user_id;
        String locationID = "location_id=" + location_id;
        String parameters = userID + "&" + locationID;
        return LOCALHOST_URL + "checkin.php" + "?" + parameters;
    }

    // ---------------------------------------------------------------------------------------------
    // Method that returns the URL for the check-out functionality. --------------------------------
    public static String getCheckOutURL(Integer user_id, Integer location_id) {
        String userID = "user_id=" + user_id;
        String locationID = "location_id=" + location_id;
        String parameters = userID + "&" + locationID;
        return LOCALHOST_URL + "checkout.php" + "?" + parameters;
    }

    // ---------------------------------------------------------------------------------------------
    // Method that returns the URL for getting the number of the users that have checked-in at the -
    // play location with the given ID. ------------------------------------------------------------
    public static String getCheckedUsersURL(Integer location_id) {
        String locationID = "location_id=" + location_id;
        String parameters = locationID;
        return LOCALHOST_URL + "checked_users.php" + "?" + parameters;
    }

    // ---------------------------------------------------------------------------------------------
    // Method that returns the URL for the rating functionality. -----------------------------------
    public static String getRateURL(Integer user_id, Integer location_id, Integer rating_num) {
        String userID = "user_id=" + user_id;
        String locationID = "location_id=" + location_id;
        String ratingNum = "rating=" + rating_num;
        String parameters = userID + "&" + locationID + "&" + ratingNum;
        return LOCALHOST_URL + "rate.php" + "?" + parameters;
    }

    // ---------------------------------------------------------------------------------------------
    // Method that returns the URL the retrieval of rating functionality. --------------------------
    public static String getRatingURL(Integer location_id) {
        String locationID = "location_id=" + location_id;
        String parameters = locationID;
        return LOCALHOST_URL + "rating.php" + "?" + parameters;
    }

    // ---------------------------------------------------------------------------------------------
    // Method that returns the URL for adding a new play location. ---------------------------------
    public static String getAddPlayLocationURL(String location_name, LatLng location_lat_lng) {
        String encodedName = null;
        try {
            encodedName = URLEncoder.encode(location_name, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String locationName = "name=" + encodedName;
        String locationLat = "lat=" + location_lat_lng.latitude;
        String locationLng = "lng=" + location_lat_lng.longitude;
        String parameters = locationName + "&" + locationLat + "&" + locationLng;
        Log.d("ConnectionManager", LOCALHOST_URL + "add_play_location.php" + "?" + parameters);
        return LOCALHOST_URL + "add_play_location.php" + "?" + parameters;
    }

    // ---------------------------------------------------------------------------------------------
    // Method that returns the URL for adding a new play location. ---------------------------------
    public static String getLoggingURL(Integer user_id, String activity_tag, String action_tag, Integer related_id, Long current_timestamp) {
        String userID = "user_id=" + user_id;
        String activityID = "activity_tag=" + activity_tag;
        String actionID = "action_tag=" + action_tag;
        String relatedID = "related_id=" + related_id;
        String timestamp = "cur_timestamp=" + current_timestamp;
        String parameters = userID + "&" + activityID + "&" + actionID + "&" + relatedID + "&" + timestamp;
        Log.d("ConnectionManager", LOCALHOST_URL + "logging.php" + "?" + parameters);
        return LOCALHOST_URL + "logging.php" + "?" + parameters;
    }

}
