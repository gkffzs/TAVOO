package gr.upatras.ceid.kaffezas.tavoo;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

// -------------------------------------------------------------------------------------------------
// This activity includes the map that shows where the play locations are and displays basic info --
// about them. It is placed in the first tab of VetActivity.----------------------------------------
// -------------------------------------------------------------------------------------------------
public class PlayLocationActivity extends FragmentActivity { // ------------------------------------

    // Declaration of several lists that are used throughout this Activity. ------------------------
    List<PlayLocation> playLocationList;
    List<MarkerOptions> markerOptionsList;
    List<List<HashMap<String, String>>> routes = null;

    // Declaration of the map. ---------------------------------------------------------------------
    private GoogleMap playMap;

    // Declarations with regards to specifying current location. -----------------------------------
    LatLng currentLocationLatLng;
    Double currentLocationLat, currentLocationLng;

    // Declarations with regards to specifying the nearest play location. --------------------------
    LatLng nearestPlayLocationLatLng;
    PlayLocation nearestPlayLocation;
    int nearestPlayLocationPosition;

    // Declaration of variables used to save values during parsing, etc. ---------------------------
    JSONObject jObject;
    String distance = "";
    String duration = "";

    // Declarations for use in the info box at the bottom, and not only. ---------------------------
    Integer playLocationID;
    TextView playLocationName, playLocationAddress;
    LatLng playLocationLatLng;
    Button playLocationCheckInButton;
    Button playLocationRatingButton;
    TextView playLocationDistance, playLocationDuration, playLocationNumber;

    // Declarations to be used for session management and controls. --------------------------------
    SharedPreferences sharedPreferences;
    Integer loggedInUserID;
    String activityTag;

    // Declarations for marker icons. --------------------------------------------------------------
    BitmapDescriptor currentLocationIcon;
    BitmapDescriptor playLocationIcon;
    BitmapDescriptor nearestPlayLocationIcon;

    // ---------------------------------------------------------------------------------------------
    @Override // Main method that executes when the activity starts. -------------------------------
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_location);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PlayLocationActivity.this);
        loggedInUserID = sharedPreferences.getInt("user_id", 0);
        activityTag = getString(R.string.activ_play_loc_tag);
        AppServices.loggingAction(loggedInUserID, activityTag, getString(R.string.act_create_activ_tag), 0);

        markerOptionsList = new ArrayList<>();
        populatePlayLocationList();
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
    // Method for getting play location data and creating the respective list. ---------------------
    private void populatePlayLocationList() {
        GetPlayLocationsTask getPlayLocationsTask = new GetPlayLocationsTask();
        getPlayLocationsTask.execute(ConnectionManager.getPlayLocationsURL());
    }

    // ---------------------------------------------------------------------------------------------
    // Method that checks and sets the map, if needed.  --------------------------------------------
    private void setUpMapIfNeeded() {
        if (playMap == null) {
            playMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.play_map)).getMap();
            if (playMap != null) {
                setUpMap();
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // The method that actually sets up the map. ---------------------------------------------------
    private void setUpMap() {
        // Setting icons for the markers.
        specifyMarkerIcons();

        // Specifying the current location of the user.
        if (specifyCurrentLocation()) {

            // Finding the nearest play location to the current location (strictly based on distance).
            findNearestPlayLocation();

            // Placing all other markers on the map.
            placePlayLocationMarkers();

            // Starting a task to get the route to the nearest play location.
            getRoute(currentLocationLatLng, nearestPlayLocationLatLng);

            // Updating name, address and ID of the location currently targeted.
            updateBasicPlayLocationInfo(nearestPlayLocation);

            // Updating the number of users that have checked in that location.
            getNumberOfCheckedUsers(playLocationID);

            // Updating check-in button.
            updateCheckInButton(loggedInUserID);

            // Updating check-in button.
            updateRatingButton(loggedInUserID, playLocationID);

            // Specifying the behavior of a long-click on the map.
            setMapLongClickListener(loggedInUserID);

            // Specifying the actions to be performed when a marker is clicked.
            setMarkerClickListeners();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Method for specifying the icons of the markers. ---------------------------------------------
    private void specifyMarkerIcons() {
        currentLocationIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_current_location_marker);
        playLocationIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_play_location_marker);
        nearestPlayLocationIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_nearest_play_location_marker);
    }

    // ---------------------------------------------------------------------------------------------
    // Method for finding current location of the user. --------------------------------------------
    private boolean specifyCurrentLocation() {
        playMap.setMyLocationEnabled(true);

        Log.d("PlayLocationActivity", "defining current location...");
        final String locationProvider = LocationManager.NETWORK_PROVIDER;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocationLat = location.getLatitude();
                currentLocationLng = location.getLongitude();
                Log.d("PlayLocationActivity", "onLocationChanged just set current location (" + currentLocationLat + ", " + currentLocationLng + ")");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("PlayLocationActivity", "onStatusChanged");
            }

            @Override
            public void onProviderEnabled(String provider) {
                LocationManager tempLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location tempMyLocation = tempLocationManager.getLastKnownLocation(locationProvider);
                currentLocationLat = tempMyLocation.getLatitude();
                currentLocationLng = tempMyLocation.getLongitude();
                Log.d("PlayLocationActivity", "onProviderEnabled just set current location (" + currentLocationLat + ", " + currentLocationLng + ")");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("PlayLocationActivity", "onProviderDisabled");
                AppServices.displayToast(PlayLocationActivity.this, "Η λειτουργία εντοπισμού της τοποθεσίας σας δεν είναι ενεργοποιημένη");
            }
        };

        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);

        if ((currentLocationLat == null) && (currentLocationLng == null)) {
            Location myLocation = locationManager.getLastKnownLocation(locationProvider);
            if (myLocation != null) {
                currentLocationLat = myLocation.getLatitude();
                currentLocationLng = myLocation.getLongitude();
                Log.d("PlayLocationActivity", "myLocation (" + currentLocationLat + ", " + currentLocationLng + ")");
            }
        }

        if ((currentLocationLat == null) && (currentLocationLng == null)) {
            AppServices.displayToast(PlayLocationActivity.this, "Αδυναμία προσδιορισμού της τοποθεσίας σας");
            return false;
        } else {
            currentLocationLatLng = new LatLng(currentLocationLat, currentLocationLng);
            playMap.addMarker(new MarkerOptions().position(currentLocationLatLng).title("Βρίσκεστε εδώ").icon(currentLocationIcon));
            Log.d("PlayLocationActivity", "marker of current location is set on map");
            // Stop listening for updates of the current location.
            locationManager.removeUpdates(locationListener);
            return true;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Method for setting a long-click listener on the map. ----------------------------------------
    private void setMapLongClickListener(final Integer userID) {
        playMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(200);

                if (userID == 0) {
                    Log.d("PlayLocationActivity", "the user is not logged in");
                    AppServices.displayToast(PlayLocationActivity.this, "Δεν έχετε συνδεθεί");
                } else {
                    displayAddPlayLocationDialog(latLng);
                }
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // Method for displaying a dialog to add a new play location. ----------------------------------
    private void displayAddPlayLocationDialog(final LatLng newLatLng) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_play_location, null);

        final EditText newPlayLocationName = (EditText) dialogView.findViewById(R.id.dialog_add_play_location_name);
        TextView newPlayLocationLatLng = (TextView) dialogView.findViewById(R.id.dialog_add_play_location_lat_lng);
        newPlayLocationLatLng.setText("(" + newLatLng.latitude + ", " + newLatLng.longitude + ")");

        final AlertDialog.Builder rateDialog = new AlertDialog.Builder(this);
        rateDialog.setTitle("Προσθήκη τοποθεσίας");
        rateDialog.setCancelable(true);
        rateDialog.setView(dialogView);
        rateDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("PlayLocationActivity", "new play location name: " + newPlayLocationName.getText().toString());
                addNewPlayLocation(newPlayLocationName.getText().toString(), newLatLng);
                dialog.dismiss();
            }
        })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        rateDialog.create();
        rateDialog.show();
    }

    // ---------------------------------------------------------------------------------------------
    // Method for adding a new play location on the map. -------------------------------------------
    private void addNewPlayLocation(String newPlayLocationName, LatLng newPlayLocationLatLng) {
        AddPlayLocationTask addPlayLocationTask = new AddPlayLocationTask();
        addPlayLocationTask.execute(ConnectionManager.getAddPlayLocationURL(newPlayLocationName, newPlayLocationLatLng));
    }

    // ---------------------------------------------------------------------------------------------
    // AsyncTask for adding a new play location. ---------------------------------------------------
    private class AddPlayLocationTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectionManager.getData(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                jObject = new JSONObject(result);
                AppServices.displayToast(PlayLocationActivity.this, jObject.getString("message"));
                Log.d("PlayLocationActivity", "successfully added a new play location");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Recreating the activity in order to make the new location visible.
            PlayLocationActivity.this.recreate();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Method for finding the nearest play location. -----------------------------------------------
    private void findNearestPlayLocation() {
        Double tempPlayLocationLat, tempPlayLocationLng;
        LatLng tempPlayLocationLatLng;
        String tempPlayLocationName;
        float minimumDistance = 0;
        nearestPlayLocationPosition = 0;
        Log.d("PlayLocationActivity", "ready to read the data from the list");
        if (playLocationList != null) {
            for (int i = 0; i < playLocationList.size(); i++) {
                tempPlayLocationLat = playLocationList.get(i).getLat();
                tempPlayLocationLng = playLocationList.get(i).getLng();
                tempPlayLocationName = playLocationList.get(i).getName();
                Log.d("PlayLocationActivity", "(#" + i + ") " + tempPlayLocationName + "(" + tempPlayLocationLat + ", " + tempPlayLocationLng + ")");
                markerOptionsList.add(new MarkerOptions().position(new LatLng(tempPlayLocationLat, tempPlayLocationLng)).title(tempPlayLocationName));

                tempPlayLocationLatLng = new LatLng(tempPlayLocationLat, tempPlayLocationLng);

                float[] distanceResults = new float[2];
                Location.distanceBetween(currentLocationLatLng.latitude, currentLocationLatLng.longitude, tempPlayLocationLatLng.latitude, tempPlayLocationLatLng.longitude, distanceResults);
                Log.d("PlayLocationActivity", "distance: " + distanceResults[0] + " m - initial bearing: " + distanceResults[1] + " degrees");

                if (i == 0) {
                    minimumDistance = distanceResults[0];
                } else if (minimumDistance > distanceResults[0]) {
                    minimumDistance = distanceResults[0];
                    nearestPlayLocationPosition = i;
                }
            }
        } else {
            Log.d("PlayLocationActivity", "empty playLocationList");
        }
        nearestPlayLocation = playLocationList.get(nearestPlayLocationPosition);
        nearestPlayLocationLatLng = new LatLng(nearestPlayLocation.getLat(), nearestPlayLocation.getLng());
        Log.d("PlayLocationActivity", "nearest PlayLocation is: " + nearestPlayLocation.getName());
    }

    // ---------------------------------------------------------------------------------------------
    // Method for placing the markers for each play location. --------------------------------------
    private void placePlayLocationMarkers() {
        for (int i = 0; i < markerOptionsList.size(); i++) {
            if (i != nearestPlayLocationPosition) {
                playMap.addMarker(markerOptionsList.get(i).icon(playLocationIcon).alpha(0.6f));
            } else {
                playMap.addMarker(markerOptionsList.get(i).icon(nearestPlayLocationIcon));
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Method for getting the route from an initial location to a target location. -----------------
    private void getRoute(LatLng initLocationLatLng, LatLng destLocationLatLng) {
        GetRouteTask getRouteTask = new GetRouteTask();
        Log.d("PlayLocationActivity", ConnectionManager.getDirectionsURL(initLocationLatLng, destLocationLatLng));
        getRouteTask.execute(ConnectionManager.getDirectionsURL(initLocationLatLng, destLocationLatLng));
        playMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destLocationLatLng, 14.0f));
    }

    // ---------------------------------------------------------------------------------------------
    // Method for getting the number of users that have checked-in at a location. ------------------
    private void getNumberOfCheckedUsers(Integer playLocationID) {
        playLocationNumber = (TextView) findViewById(R.id.play_loc_info_checked);
        CheckedUsersTask checkedUsersTask = new CheckedUsersTask();
        Log.d("PlayLocationActivity", "getting users that have checked in location #" + playLocationID);
        checkedUsersTask.execute(ConnectionManager.getCheckedUsersURL(playLocationID));
    }

    // ---------------------------------------------------------------------------------------------
    // Method for setting listeners on every marker on the map. ------------------------------------
    private void setMarkerClickListeners() {
        playMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                playLocationLatLng = null;
                if (!marker.getTitle().equals("Βρίσκεστε εδώ")) {
                    Log.d("PlayLocationActivity", "marker " + marker.getTitle() + " is not the current location");
                    for (final PlayLocation playLocation : playLocationList) {
                        if (marker.getTitle().equals(playLocation.getName())) {
                            AppServices.loggingAction(loggedInUserID, activityTag, getString(R.string.act_play_loc_tap_tag), playLocation.getPlayLocationID());
                            playLocationLatLng = new LatLng(playLocation.getLat(), playLocation.getLng());
                            updateBasicPlayLocationInfo(playLocation);
                            getNumberOfCheckedUsers(playLocationID);
                            updateCheckInButton(loggedInUserID);
                            updateRatingButton(loggedInUserID, playLocationID);
                        }
                    }

                    // Updating the markers and plotting the route to the new location.
                    updateMarkersAndRoute(marker);
                    return true;
                } else {
                    Log.d("PlayLocationActivity", "marker " + marker.getTitle() + " is the current location, not proceeding");
                    return false;
                }
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // Method for updating the info of the location that we're focused on. -------------------------
    private void updateBasicPlayLocationInfo(PlayLocation playLocation) {
        playLocationName = (TextView) findViewById(R.id.play_loc_name);
        playLocationName.setText(playLocation.getName());
        // Temporary fix for displaying the latitude and longitude of the location.
        String tempAddress = "(" + Double.toString(playLocation.getLat()) + ", " + Double.toString(playLocation.getLng()) + ")";
        playLocationAddress = (TextView) findViewById(R.id.play_loc_address);
        playLocationAddress.setText(tempAddress);
        playLocationID = playLocation.getPlayLocationID();
    }

    // ---------------------------------------------------------------------------------------------
    // Method for updating distance and duration info. ---------------------------------------------
    private void updateDistanceAndDurationInfo(String distance, String duration) {
        playLocationDistance = (TextView) findViewById(R.id.play_loc_info_distance);
        playLocationDistance.setText(distance);
        playLocationDuration = (TextView) findViewById(R.id.play_loc_info_duration);
        playLocationDuration.setText(duration);
    }

    // ---------------------------------------------------------------------------------------------
    // Method for updating the check-in button. ----------------------------------------------------
    private void updateCheckInButton(final Integer userID) {
        playLocationCheckInButton = (Button) findViewById(R.id.play_loc_check_in_button);
        final int playLocationWhereUserCheckedInID = sharedPreferences.getInt("check_in_location_id", 0);
        for (PlayLocation tempPlayLocation : playLocationList) {
            if (tempPlayLocation.getName().equals(playLocationName.getText())) {
                if (tempPlayLocation.getPlayLocationID() == playLocationWhereUserCheckedInID) {
                    setCheckInButtonToCheckOut();
                } else {
                    setCheckInButtonToCheckIn();
                }
            }
        }

        playLocationCheckInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (playLocationCheckInButton.getText().toString().equals("Check-in")) {
                    if (userID == 0) {
                        Log.d("PlayLocationActivity", "the user is not logged in");
                        AppServices.displayToast(PlayLocationActivity.this, "Δεν έχετε συνδεθεί");
                    } else {
                        if ((playLocationWhereUserCheckedInID != 0) && (playLocationWhereUserCheckedInID != playLocationID)) {
                            Log.d("PlayLocationActivity", "the user has already done one check-in");
                            AppServices.displayToast(PlayLocationActivity.this, "Έχετε κάνει ήδη check-in κάπου αλλού");
                        } else {
                            AppServices.loggingAction(userID, activityTag, getString(R.string.act_play_loc_checkin_tag), playLocationID);
                            performCheckIn(userID, playLocationID);

                            editor.putInt("check_in_location_id", playLocationID);
                            editor.apply();
                            Log.d("PlayLocationActivity", "just saved check_in_location_id: " + sharedPreferences.getInt("check_in_location_id", 0));

                            setCheckInButtonToCheckOut();
                            getNumberOfCheckedUsers(playLocationID);
                        }
                    }
                } else if (playLocationCheckInButton.getText().toString().equals("Check-out")) {
                    AppServices.loggingAction(userID, activityTag, getString(R.string.act_play_loc_checkout_tag), playLocationID);
                    performCheckOut(userID, playLocationID);

                    editor.remove("check_in_location_id");
                    editor.apply();
                    Log.d("PlayLocationActivity", "just deleted check_in_location_id: " + sharedPreferences.getInt("check_in_location_id", 0));

                    setCheckInButtonToCheckIn();
                    getNumberOfCheckedUsers(playLocationID);
                }
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // Method for changing the appearance of the button to check-in. -------------------------------
    private void setCheckInButtonToCheckIn() {
        playLocationCheckInButton.setText("Check-in");
        playLocationCheckInButton.setBackgroundResource(R.drawable.check_in_button_background);
    }

    // ---------------------------------------------------------------------------------------------
    // Method for changing the appearance of the button to check-out. ------------------------------
    private void setCheckInButtonToCheckOut() {
        playLocationCheckInButton.setText("Check-out");
        playLocationCheckInButton.setBackgroundResource(R.drawable.check_out_button_background);
    }

    // ---------------------------------------------------------------------------------------------
    // Method for updating the rating button. ------------------------------------------------------
    private void updateRatingButton(final Integer userID, final Integer playLocationID) {
        playLocationRatingButton = (Button) findViewById(R.id.play_loc_rating_button);
        getRating(playLocationID);
        playLocationRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userID == 0) {
                    Log.d("PlayLocationActivity", "the user is not logged in");
                    AppServices.displayToast(PlayLocationActivity.this, "Δεν έχετε συνδεθεί");
                } else {
                    displayRateDialog(userID, playLocationID);
                    getRating(playLocationID);
                }
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // Method for displaying the dialog for rating. ------------------------------------------------
    private void displayRateDialog(final Integer userID, final Integer playLocationID) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_rating, null);

        final RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.ratingBar);

        final AlertDialog.Builder rateDialog = new AlertDialog.Builder(this);
        rateDialog.setTitle("Βαθμολογήστε την τοποθεσία");
        rateDialog.setCancelable(true);
        rateDialog.setView(dialogView);
        rateDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("PlayLocationActivity", "the user just rated: " + ratingBar.getRating());
                performRating(userID, playLocationID, (int) ratingBar.getRating());
                getRating(playLocationID);
                dialog.dismiss();
            }
        })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        rateDialog.create();
        rateDialog.show();
    }

    // ---------------------------------------------------------------------------------------------
    // Method for sending a rating for a play location. --------------------------------------------
    private void performRating(Integer userID, Integer playLocationID, Integer rating) {
        RatePlayLocationTask ratePlayLocationTask = new RatePlayLocationTask();
        ratePlayLocationTask.execute(ConnectionManager.getRateURL(userID, playLocationID, rating));
    }

    // ---------------------------------------------------------------------------------------------
    // AsyncTask for rating a play location. -------------------------------------------------------
    private class RatePlayLocationTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectionManager.getData(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                jObject = new JSONObject(result);
                AppServices.displayToast(PlayLocationActivity.this, jObject.getString("message"));
                Log.d("PlayLocationActivity", "successfully rated a location");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Method for getting the rating for a play location. ------------------------------------------
    private void getRating(Integer playLocationID) {
        GetRatingTask getRatingTask = new GetRatingTask();
        getRatingTask.execute(ConnectionManager.getRatingURL(playLocationID));
    }

    // ---------------------------------------------------------------------------------------------
    // AsyncTask for getting the average rating of a play location. --------------------------------
    private class GetRatingTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectionManager.getData(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                jObject = new JSONObject(result);
                String tempRating = jObject.getString("rating");
                Log.d("PlayLocationActivity", "successfully retrieved the rate (" + tempRating + ")");
                if (tempRating.equals("null")) {
                    playLocationRatingButton.setText(R.string.play_loc_info_rating_placeholder);
                } else {
                    playLocationRatingButton.setText(tempRating);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Method for updating the icons of the markers and plotting the new route. --------------------
    private void updateMarkersAndRoute(Marker marker) {
        Log.d("PlayLocationActivity", ">> Marker clicked: " + marker.getTitle());
        playMap.clear();
        int temp_i = 0;
        for (int i = 0; i < markerOptionsList.size(); i++) {
            if (marker.getTitle().equals(markerOptionsList.get(i).getTitle())) {
                temp_i = i;
                Log.d("PlayLocationActivity", ">> Position in list: " + temp_i);
            }
        }
        for (int i = 0; i < markerOptionsList.size(); i++) {
            if ((i != nearestPlayLocationPosition) && (i != temp_i)) {
                playMap.addMarker(markerOptionsList.get(i).icon(playLocationIcon).alpha(0.6f));
            } else if ((i != nearestPlayLocationPosition) && (i == temp_i)) {
                playMap.addMarker(markerOptionsList.get(i).icon(playLocationIcon).alpha(1f));
            } else {
                playMap.addMarker(markerOptionsList.get(i).icon(nearestPlayLocationIcon));
            }
        }
        playMap.addMarker(new MarkerOptions().position(currentLocationLatLng).title("Βρίσκεστε εδώ").icon(currentLocationIcon));
        GetRouteTask markerGetRouteTask = new GetRouteTask();
        markerGetRouteTask.execute(ConnectionManager.getDirectionsURL(currentLocationLatLng, playLocationLatLng));
        playMap.animateCamera(CameraUpdateFactory.newLatLng(playLocationLatLng));
    }

    // ---------------------------------------------------------------------------------------------
    // Method for checking in a play location. -----------------------------------------------------
    private void performCheckIn(Integer userID, Integer locationID) {
        Log.d("PlayLocationActivity", ConnectionManager.getCheckInURL(userID, locationID));
        CheckInTask checkInTask = new CheckInTask();
        checkInTask.execute(ConnectionManager.getCheckInURL(userID, locationID));
        startAutoCheckOut();
    }

    // ---------------------------------------------------------------------------------------------
    // Method for starting the alarm to auto-check-out from a location. ----------------------------
    private void startAutoCheckOut() {
        Log.d("PlayLocationActivity", "startAutoCheckOut()");
        Intent autoCheckOutIntent = new Intent(getBaseContext(), AutoCheckOutReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 1, autoCheckOutIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Long delay = TimeUnit.HOURS.toMillis(3L);
        Long time = System.currentTimeMillis() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC, time, pendingIntent);
            Log.d("PlayLocationActivity", "startAutoCheckOut() - alarm set (>18)");
        } else {
            alarmManager.set(AlarmManager.RTC, time, pendingIntent);
            Log.d("PlayLocationActivity", "startAutoCheckOut() - alarm set (<19)");
        }
    }

    // ---------------------------------------------------------------------------------------------
    // AsyncTask for performing check-in at a location. --------------------------------------------
    private class CheckInTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectionManager.getData(params[0]);
        }

        protected void onPostExecute(String result) {
            try {
                jObject = new JSONObject(result);
                Log.d("PlayLocationActivity", "valid check-in (success=" + jObject.getInt("success") + ")");
                AppServices.displayToast(PlayLocationActivity.this, "Κάνατε επιτυχώς check-in!");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Method for checking out of a play location. -------------------------------------------------
    private void performCheckOut(Integer userID, Integer locationID) {
        Log.d("PlayLocationActivity", ConnectionManager.getCheckOutURL(userID, locationID));
        CheckOutTask checkOutTask = new CheckOutTask();
        checkOutTask.execute(ConnectionManager.getCheckOutURL(userID, locationID));
        cancelAutoCheckOut();
    }

    // ---------------------------------------------------------------------------------------------
    // Method for canceling the alarm to auto-check-out from a location. ---------------------------
    private void cancelAutoCheckOut() {
        Log.d("PlayLocationActivity", "cancelAutoCheckOut()");
        Intent autoCheckOutIntent = new Intent(getBaseContext(), AutoCheckOutReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 1, autoCheckOutIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Log.d("PlayLocationActivity", "just canceled the alarm");
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
                jObject = new JSONObject(result);
                Log.d("PlayLocationActivity", "valid check-out (success=" + jObject.getInt("success") + ")");
                AppServices.displayToast(PlayLocationActivity.this, "Κάνατε επιτυχώς check-out!");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // AsyncTask for finding out how many users have checked-in at a specific location. ------------
    private class CheckedUsersTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectionManager.getData(params[0]);
        }

        protected void onPostExecute(String result) {
            try {
                jObject = new JSONObject(result);
                Log.d("PlayLocationActivity", "successfully retrieved number (" + jObject.getInt("checked_users") + ")");
                playLocationNumber.setText(String.valueOf(jObject.getInt("checked_users")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // AsyncTask for retrieving and parsing the directions between two locations. ------------------
    private class GetRouteTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectionManager.getData(params[0]);
        }

        protected void onPostExecute(String result) {
            try {
                jObject = new JSONObject(result);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parseFeed(jObject);
                Log.d("PlayLocationActivity", "successfully parsed routes data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            drawRoute(routes);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Method that draws the route between two locations on the map. -------------------------------
    private void drawRoute(List<List<HashMap<String, String>>> routes) {
        ArrayList<LatLng> points;
        PolylineOptions polylineOptions = null;

        if (routes == null) {
            AppServices.displayToast(PlayLocationActivity.this, "Προέκυψε κάποιο σφάλμα κατά τον προσδιορισμό της διαδρομής");
        } else {
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<>();
                polylineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {
                        distance = point.get("distance");
                        continue;
                    } else if (j == 1) {
                        duration = point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                polylineOptions.addAll(points);
                polylineOptions.width(4);
                polylineOptions.color(Color.rgb(48, 48, 48));
            }

            Log.d("PlayLocationActivity", "distance (" + distance + ") and duration (" + duration + ") are set");
            playMap.addPolyline(polylineOptions);
            updateDistanceAndDurationInfo(distance, duration);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // AsyncTask for retrieving and parsing the data of the play locations. ------------------------
    private class GetPlayLocationsTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectionManager.getData(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            playLocationList = PlayLocationXMLParser.parseFeed(result);
            Log.d("PlayLocationActivity", "parsed the data and loading the map...");
            setUpMapIfNeeded();
        }
    }
}