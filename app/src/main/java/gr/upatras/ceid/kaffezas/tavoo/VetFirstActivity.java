package gr.upatras.ceid.kaffezas.tavoo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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

// -------------------------------------------------------------------------------------------------
// This activity includes the map that shows the vets and displays basic info about them. ----------
// It is placed in the first tab of VetActivity. ---------------------------------------------------
// -------------------------------------------------------------------------------------------------
public class VetFirstActivity extends FragmentActivity { // ----------------------------------------

    // Declaration of variables used to check whether a user is logged-in. -------------------------
    SharedPreferences sharedPreferences;
    Integer loggedInUserID;
    String activityTag;

    // Declaration of several lists that are used throughout this activity. ------------------------
    List<Vet> vetList;
    List<MarkerOptions> markerOptionsList;
    List<List<HashMap<String, String>>> routes = null;

    // Declaration of the map. ---------------------------------------------------------------------
    private GoogleMap vetMap;

    // Declarations with regards to specifying current location. -----------------------------------
    LatLng currentLocationLatLng;
    Double currentLocationLat, currentLocationLng;

    // Declarations with regards to specifying the nearest play location. --------------------------
    LatLng nearestVetLatLng;
    Vet nearestVet;
    int nearestVetPosition;

    // Declaration of variables used to save values during parsing, etc. ---------------------------
    JSONObject jObject;
    String distance = "";
    String duration = "";

    // Declarations for use in the info box at the bottom, and not only. ---------------------------
    TextView vetName, vetAddress;
    ImageButton vetCallButton;
    LatLng vetLatLng;
    String vetPhoneNumber;
    TextView vetDistance, vetDuration;

    // Declarations for marker icons. --------------------------------------------------------------
    BitmapDescriptor currentLocationIcon;
    BitmapDescriptor vetIcon;
    BitmapDescriptor nearestVetIcon;

    // ---------------------------------------------------------------------------------------------
    @Override // Main method that executes when the activity starts. -------------------------------
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_first);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(VetFirstActivity.this);
        loggedInUserID = sharedPreferences.getInt("user_id", 0);
        activityTag = getString(R.string.activ_vet_fir_tag);
        AppServices.loggingAction(loggedInUserID, activityTag, getString(R.string.act_create_activ_tag), 0);

        markerOptionsList = new ArrayList<>();
        if (ConnectionManager.isOnline(this)) {
            populateVetList();
        }
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
    // Method for getting vet data and creating the respective list. -------------------------------
    private void populateVetList() {
        GetVetsTask getVetsTask = new GetVetsTask();
        getVetsTask.execute(ConnectionManager.getVetsURL());
    }

    // ---------------------------------------------------------------------------------------------
    // Method that checks and sets the map, if needed. ---------------------------------------------
    private void setUpMapIfNeeded() {
        if (vetMap == null) {
            vetMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.vet_map)).getMap();
            if (vetMap != null) {
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

            // Finding the nearest vet to the current location (strictly based on distance).
            findNearestVet();

            // Placing all other markers on the map.
            placeVetMarkers();

            // Starting a task to get the route to the nearest vet.
            getRoute(currentLocationLatLng, nearestVetLatLng);

            // Updating name, address and telephone number of the vet currently targeted.
            updateBasicVetInfo(nearestVet);

            // Specifying the actions to be performed when a marker is clicked.
            setMarkerClickListeners();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Method for specifying the icons of the markers. ---------------------------------------------
    private void specifyMarkerIcons() {
        currentLocationIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_current_location_marker);
        vetIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_vet_marker);
        nearestVetIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_nearest_vet_marker);
    }

    // ---------------------------------------------------------------------------------------------
    // Method for finding current location of the user. --------------------------------------------
    private boolean specifyCurrentLocation() {
        vetMap.setMyLocationEnabled(true);

        Log.d("VetFirstActivity", "defining current location...");
        final String locationProvider = LocationManager.NETWORK_PROVIDER;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocationLat = location.getLatitude();
                currentLocationLng = location.getLongitude();
                Log.d("VetFirstActivity", "onLocationChanged just set current location (" + currentLocationLat + ", " + currentLocationLng + ")");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("VetFirstActivity", "onStatusChanged");
            }

            @Override
            public void onProviderEnabled(String provider) {
                LocationManager tempLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location tempMyLocation = tempLocationManager.getLastKnownLocation(locationProvider);
                currentLocationLat = tempMyLocation.getLatitude();
                currentLocationLng = tempMyLocation.getLongitude();
                Log.d("VetFirstActivity", "onProviderEnabled just set current location (" + currentLocationLat + ", " + currentLocationLng + ")");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("VetFirstActivity", "onProviderDisabled");
                AppServices.displayToast(VetFirstActivity.this, "Η λειτουργία εντοπισμού της τοποθεσίας σας δεν είναι ενεργοποιημένη");
            }
        };

        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);

        if ((currentLocationLat == null) && (currentLocationLng == null)) {
            Location myLocation = locationManager.getLastKnownLocation(locationProvider);
            if (myLocation != null) {
                currentLocationLat = myLocation.getLatitude();
                currentLocationLng = myLocation.getLongitude();
                Log.d("VetFirstActivity", "myLocation (" + currentLocationLat + ", " + currentLocationLng + ")");
            }
        }

        if ((currentLocationLat == null) && (currentLocationLng == null)) {
            AppServices.displayToast(VetFirstActivity.this, "Αδυναμία προσδιορισμού της τοποθεσίας σας");
            return false;
        } else {
            currentLocationLatLng = new LatLng(currentLocationLat, currentLocationLng);
            vetMap.addMarker(new MarkerOptions().position(currentLocationLatLng).title("Βρίσκεστε εδώ").icon(currentLocationIcon));
            Log.d("VetFirstActivity", "marker of current location is set on map");
            // Stop listening for updates of the current location.
            locationManager.removeUpdates(locationListener);
            return true;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Method for finding the nearest vet. ---------------------------------------------------------
    private void findNearestVet() {
        Double tempVetLat, tempVetLng;
        LatLng tempVetLatLng;
        String tempVetName;
        float minimumDistance = 0;
        nearestVetPosition = 0;
        Log.d("VetFirstActivity", "ready to read the data from the list");
        if (vetList != null) {
            for (int i = 0; i < vetList.size(); i++) {
                tempVetLat = vetList.get(i).getLat();
                tempVetLng = vetList.get(i).getLng();
                tempVetName = vetList.get(i).getName();
                Log.d("VetFirstActivity", "(#" + i + ") " + tempVetName + "(" + tempVetLat + ", " + tempVetLng + ")");
                markerOptionsList.add(new MarkerOptions().position(new LatLng(tempVetLat, tempVetLng)).title(tempVetName));
                Log.d("VetFirstActivity", "added an item to markerOptionsList");

                tempVetLatLng = new LatLng(tempVetLat, tempVetLng);

                float[] distance_results = new float[2];
                Location.distanceBetween(currentLocationLatLng.latitude, currentLocationLatLng.longitude, tempVetLatLng.latitude, tempVetLatLng.longitude, distance_results);
                Log.d("VetFirstActivity", "distance: " + distance_results[0] + " m - initial bearing: " + distance_results[1] + " degrees");

                if (i == 0) {
                    minimumDistance = distance_results[0];
                } else if (minimumDistance > distance_results[0]) {
                    minimumDistance = distance_results[0];
                    nearestVetPosition = i;
                }
            }
        } else {
            Log.d("VetFirstActivity", "empty vetList");
        }
        nearestVet = vetList.get(nearestVetPosition);
        nearestVetLatLng = new LatLng(nearestVet.getLat(), nearestVet.getLng());
        Log.d("VetFirstActivity", "nearest vet is: " + nearestVet.getName() + "(" + nearestVet.getTelephone() + ")");
    }

    // ---------------------------------------------------------------------------------------------
    // Method for placing the markers for each vet. ------------------------------------------------
    private void placeVetMarkers() {
        for (int i = 0; i < markerOptionsList.size(); i++) {
            if (i != nearestVetPosition) {
                vetMap.addMarker(markerOptionsList.get(i).icon(vetIcon).alpha(0.6f));
            } else {
                vetMap.addMarker(markerOptionsList.get(i).icon(nearestVetIcon));
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Method for getting the route from an initial location to a target location. -----------------
    private void getRoute(LatLng initLocationLatLng, LatLng destLocationLatLng) {
        GetRouteTask getRouteTask = new GetRouteTask();
        Log.d("VetFirstActivity", ConnectionManager.getDirectionsURL(initLocationLatLng, destLocationLatLng));
        getRouteTask.execute(ConnectionManager.getDirectionsURL(initLocationLatLng, destLocationLatLng));
        vetMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destLocationLatLng, 14.0f));
    }

    // ---------------------------------------------------------------------------------------------
    // Method for updating the info of the vet that we're focused on. ------------------------------
    private void updateBasicVetInfo(final Vet vet) {
        vetName = (TextView) findViewById(R.id.vet_fir_name);
        vetName.setText(vet.getName());
        vetAddress = (TextView) findViewById(R.id.vet_fir_address);
        vetAddress.setText(vet.getAddress());
        vetCallButton = (ImageButton) findViewById(R.id.vet_fir_call_button);
        vetPhoneNumber = "tel:" + vet.getTelephone();
        vetCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppServices.loggingAction(loggedInUserID, activityTag, getString(R.string.act_vet_call_tag), vet.getVetID());
                Intent call_intent = new Intent(Intent.ACTION_CALL, Uri.parse(vetPhoneNumber));
                startActivity(call_intent);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // Method for setting listeners on every marker on the map. ------------------------------------
    private void setMarkerClickListeners() {
        vetMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                vetLatLng = null;
                if (!marker.getTitle().equals("Βρίσκεστε εδώ")) {
                    Log.d("VetFirstActivity", "marker " + marker.getTitle() + " is not the current location");
                    for (Vet vet : vetList) {
                        if (marker.getTitle().equals(vet.getName())) {
                            AppServices.loggingAction(loggedInUserID, activityTag, getString(R.string.act_vet_tap_tag), vet.getVetID());
                            vetLatLng = new LatLng(vet.getLat(), vet.getLng());
                            updateBasicVetInfo(vet);
                        }
                    }

                    // Updating the markers and plotting the route to the new vet.
                    updateMarkersAndRoute(marker);
                    return true;
                } else {
                    Log.d("VetFirstActivity", "marker " + marker.getTitle() + " is the current location, not proceeding");
                    return false;
                }
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // Method for updating the icons of the markers and plotting the new route. --------------------
    private void updateMarkersAndRoute(Marker marker) {
        Log.d("VetFirstActivity", ">> Marker clicked: " + marker.getTitle());
        vetMap.clear();
        int temp_i = 0;
        for (int i = 0; i < markerOptionsList.size(); i++) {
            if (marker.getTitle().equals(markerOptionsList.get(i).getTitle())) {
                temp_i = i;
                Log.d("VetFirstActivity", ">> Position in list:" + temp_i);
            }
        }
        for (int i = 0; i < markerOptionsList.size(); i++) {
            if ((i != nearestVetPosition) && (i != temp_i)) {
                vetMap.addMarker(markerOptionsList.get(i).icon(vetIcon).alpha(0.6f));
            } else if ((i != nearestVetPosition) && (i == temp_i)) {
                vetMap.addMarker(markerOptionsList.get(i).icon(vetIcon).alpha(1f));
            } else {
                vetMap.addMarker(markerOptionsList.get(i).icon(nearestVetIcon));
            }
        }
        vetMap.addMarker(new MarkerOptions().position(currentLocationLatLng).title("Βρίσκεστε εδώ").icon(currentLocationIcon));
        GetRouteTask markerGetRouteTask = new GetRouteTask();
        markerGetRouteTask.execute(ConnectionManager.getDirectionsURL(currentLocationLatLng, vetLatLng));
        vetMap.animateCamera(CameraUpdateFactory.newLatLng(vetLatLng));
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
                Log.d("VetFirstActivity", "successfully parsed routes data");
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
            AppServices.displayToast(VetFirstActivity.this, "Προέκυψε κάποιο σφάλμα κατά τον προσδιορισμό της διαδρομής");
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

            Log.d("VetFirstActivity", "distance (" + distance + ") and duration (" + duration + ") are set");
            vetMap.addPolyline(polylineOptions);
            updateDistanceAndDurationInfo(distance, duration);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Method for updating distance and duration info. ---------------------------------------------
    private void updateDistanceAndDurationInfo(String distance, String duration) {
        vetDistance = (TextView) findViewById(R.id.vet_fir_info_distance);
        vetDistance.setText(distance);
        vetDuration = (TextView) findViewById(R.id.vet_fir_info_duration);
        vetDuration.setText(duration);
    }

    // ---------------------------------------------------------------------------------------------
    // AsyncTask for retrieving and parsing the data of the vets. ----------------------------------
    private class GetVetsTask extends AsyncTask<String, String, String> {
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
                vetList = VetJSONParser.parseFeed(result);
                Log.d("VetFirstActivity", "successfully parsed vet data");
            } catch (JSONException e) {
                Log.d("VetFirstActivity", "failed to parse vet data");
                e.printStackTrace();
            }
            Log.d("VetFirstActivity", "before setting the map");
            setUpMapIfNeeded();
        }
    }
}