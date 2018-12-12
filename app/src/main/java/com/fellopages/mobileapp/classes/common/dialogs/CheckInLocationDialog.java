/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 */

package com.fellopages.mobileapp.classes.common.dialogs;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.CheckInAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnCheckInLocationResponseListener;
import com.fellopages.mobileapp.classes.common.utils.CheckInList;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckInLocationDialog extends Dialog implements TextWatcher,
        AdapterView.OnItemClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener,
        DialogInterface.OnDismissListener {


    private Context mContext;
    private AppConstant mAppConst;
    private EditText mSearchLocationBox;
    private TextView mRemoveLocationButton, mSubmitLocation;
    private ProgressBar mProgressBar;
    private String searchText = null, mDefaultLocation = "", mNewLocation;
    private StringBuilder mNearbySearchUrl, mTextSearchUrl;
    private double mLongitude, mLatitude;
    private int loadingTime = 0;
    private boolean isDefaultLocationRequest = false, isSendRequest = true, isOpenCheckIn;
    private ArrayList<CheckInList> mCheckInList;
    private Bundle mBundle;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation, mCurrentLocation;
    private LocationRequest myLocationRequest;
    private CheckInAdapter mCheckInAdapter;
    private OnCheckInLocationResponseListener mOnCheckInLocationResponseListener;
    private JSONObject mLocationObject;

    // Timer to detect user stopped typing
    private final long SEARCH_TRIGGER_DELAY_IN_MS = 1000;
    private final int TRIGGER_SERACH = 1;

    private LinearLayout mMyLocationButton;

    /**
     * Public constructor
     * @param context context of calling activity.
     * @param bundle bundle which contains all the data.
     */
    public CheckInLocationDialog(Context context, Bundle bundle) {
        super(context, R.style.Theme_LocationDialog);
        this.mBundle = bundle;
        mContext = context;
        mAppConst = new AppConstant(mContext);
        mOnCheckInLocationResponseListener = (OnCheckInLocationResponseListener) mContext;
        initializeDialog();
    }


    /**
     * Method to initialize Dialog with custom view.
     */
    public void initializeDialog() {

        setContentView(R.layout.search_list_data_view);
        int width = (int)(mContext.getResources().getDisplayMetrics().widthPixels*0.95);
        int height = (int)(mContext.getResources().getDisplayMetrics().heightPixels*0.90);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setOnDismissListener(this);
        getWindow().setLayout(width, height);
        getWindow().getAttributes().windowAnimations = R.style.customDialogAnimation;

        // Setting adapter.
        mCheckInList = new ArrayList<>();
        mCheckInAdapter = new CheckInAdapter(mContext, R.layout.checkin_list, mCheckInList);
        ListView locationsListView = (ListView) findViewById(R.id.listView);
        locationsListView.setAdapter(mCheckInAdapter);
        locationsListView.setOnItemClickListener(this);

        // Getting views.
        findViewById(R.id.toolbar).setVisibility(View.GONE);
        TextView searchIcon = (TextView) findViewById(R.id.searchIcon);
        searchIcon.setVisibility(View.VISIBLE);
        searchIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        searchIcon.setText("\uf002");

        mProgressBar = (ProgressBar) findViewById(R.id.loadingBar);
        mSearchLocationBox = (EditText) findViewById(R.id.searchBox);
        mSearchLocationBox.setHint(mContext.getResources().getString(R.string.search_for_places));
        mSearchLocationBox.addTextChangedListener(this);

        mMyLocationButton = (LinearLayout) findViewById(R.id.my_location_view);

        mRemoveLocationButton = (TextView) findViewById(R.id.removeLocationButton);
        mRemoveLocationButton.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        mRemoveLocationButton.setText("\uf00d");
        mSubmitLocation = (TextView) findViewById(R.id.submitLocation);
        mSubmitLocation.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        mSubmitLocation.setText("\uf00c");

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }

        // Setting title of the dialog and other respective value.
        isDefaultLocationRequest = mBundle.getBoolean(ConstantVariables.CHANGE_DEFAULT_LOCATION);
        if (!isDefaultLocationRequest) {
            setTitle(mContext.getResources().getString(R.string.title_activity_check_in));
            isOpenCheckIn = mBundle.getBoolean("openCheckIn");
            Bundle locationsBundle = mBundle.getBundle("locationsBundle");
            if (locationsBundle != null && !locationsBundle.isEmpty()) {
                String mSelectedLocationValue = locationsBundle.getString("locationLabel");
                if (mSelectedLocationValue != null && !mSelectedLocationValue.isEmpty()) {
                    mSearchLocationBox.setText(mSelectedLocationValue);
                    mSearchLocationBox.setSelection(mSelectedLocationValue.length());
                }
            }
            if (AppConstant.isDeviceLocationEnable == 1 && AppConstant.mLocationType.equals("notspecific") && isOpenCheckIn) {
                mMyLocationButton.setVisibility(View.VISIBLE);
                findViewById(R.id.separator_search).setVisibility(View.VISIBLE);
                mMyLocationButton.setOnClickListener(this);
            } else {
                mMyLocationButton.setVisibility(View.GONE);
                findViewById(R.id.separator_search).setVisibility(View.GONE);
            }

            // Ask for device location gps when it's not enabled
            if (!GlobalFunctions.isLocationEnabled(mContext))
                GlobalFunctions.requestForDeviceLocation(mContext);

        } else {
            setTitle(mContext.getResources().getString(R.string.search_location_title));

            isSendRequest = false;
            mDefaultLocation = PreferencesUtils.getDefaultLocation(mContext);
            mSearchLocationBox.setText(mDefaultLocation);
            mSearchLocationBox.setSelection(mDefaultLocation.length());

            if (AppConstant.isDeviceLocationEnable == 1 && AppConstant.mLocationType.equals("notspecific")) {
                mMyLocationButton.setVisibility(View.VISIBLE);
                findViewById(R.id.separator_search).setVisibility(View.VISIBLE);
                mMyLocationButton.setOnClickListener(this);
            } else {
                mMyLocationButton.setVisibility(View.GONE);
                findViewById(R.id.separator_search).setVisibility(View.GONE);
            }
        }

        mSubmitLocation.setOnClickListener(this);
        mRemoveLocationButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(mAppConst.checkManifestPermission(Manifest.permission.ACCESS_FINE_LOCATION))
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        createLocationRequest();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        if (location != null && (searchText == null || searchText.isEmpty())) {
            mProgressBar.setVisibility(View.VISIBLE);
            if (isOpenCheckIn){
                mMyLocationButton.setVisibility(View.GONE);
                findViewById(R.id.separator_search).setVisibility(View.GONE);
            }
            mLongitude = mCurrentLocation.getLongitude();
            mLatitude = mCurrentLocation.getLatitude();

            mNearbySearchUrl = new StringBuilder(UrlUtil.PLACES_API_BASE
                    + "/nearbysearch" + "/json");
            mNearbySearchUrl.append("?location=");
            mNearbySearchUrl.append(Double.toString(mLatitude));
            mNearbySearchUrl.append(",");
            mNearbySearchUrl.append(Double.toString(mLongitude));
            mNearbySearchUrl.append("&radius=500");
            mNearbySearchUrl.append("&key=" + mContext.getResources().getString(R.string.places_api_key));

            // Creating a new non-ui thread task to download json data
            PlacesTask placesTask = new PlacesTask();

            // Invokes the "doInBackground()" method of the class PlaceTask
            placesTask.execute(mNearbySearchUrl.toString());

        }
        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        String types = "";
        CheckInList checkInList = mCheckInList.get(i);
        final String label = checkInList.getmLocationLabel();
        final String placeId = checkInList.getmPlaceId();
        String locationIcon = checkInList.getmLocationIcon();
        String vicinity = checkInList.getmVicinity();
        Double latitude = checkInList.getLatitude();
        Double longitude = checkInList.getLongitude();
        JSONArray typesArray = checkInList.getmTypesArray();

        final JSONObject checkInObject = new JSONObject();

        try {
            checkInObject.put("label", label);
            checkInObject.put("name", label);
            checkInObject.put("icon", locationIcon);
            checkInObject.put("vicinity", vicinity);
            checkInObject.put("latitude", latitude);
            checkInObject.put("longitude", longitude);
            checkInObject.put("place_id", placeId);

            // Send Comma separated types
            if(typesArray != null){
                for(int j = 0; j < typesArray.length(); j++){
                    if (j < typesArray.length() - 1)
                        types += typesArray.getString(j) + ",";
                    else
                        types += typesArray.getString(j);
                }
            }
            checkInObject.put("types", types);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!isDefaultLocationRequest) {
            Bundle bundle = new Bundle();
            bundle.putString("placeId", placeId);
            bundle.putString("locationLabel", label);
            bundle.putString("locationObject", checkInObject.toString());
            if (mOnCheckInLocationResponseListener != null) {
                isOpenCheckIn = false;
                mOnCheckInLocationResponseListener.onCheckInLocationChanged(bundle);
                dismiss();
            }

        } else {

            if(checkInList.getmFormattedAddress()!= null && !checkInList.getmFormattedAddress().isEmpty()) {
                mNewLocation = checkInList.getmFormattedAddress();
            } else {
                mNewLocation = vicinity;
            }
            try {
                if(mNewLocation != null) {
                    mLocationObject = new JSONObject();
                    mLocationObject.put("latitude", latitude);
                    mLocationObject.put("longitude", longitude);
                    mLocationObject.put("placeId", placeId);
                    mLocationObject.put("formatted_address", mNewLocation);
                    mLocationObject.put("location", mNewLocation);

                    mSearchLocationBox.setText(mNewLocation);
                    mSearchLocationBox.setSelection(mNewLocation.length());

                    setDefaultLocation();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        loadingTime++;
        if (loadingTime >=2) {
            isSendRequest = true;
        }

        if (charSequence.length() > 0) {
            mRemoveLocationButton.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        } else {
            mRemoveLocationButton.setVisibility(View.GONE);
        }

        if (charSequence.length() == 0 && isDefaultLocationRequest) {
            mSubmitLocation.setVisibility(View.VISIBLE);
        } else {
            mSubmitLocation.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        searchText = editable.toString();
        handler.removeMessages(TRIGGER_SERACH);
        handler.sendEmptyMessageDelayed(TRIGGER_SERACH, SEARCH_TRIGGER_DELAY_IN_MS);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.removeLocationButton:
                mAppConst.hideKeyboard();
                mSearchLocationBox.setText("");
                mRemoveLocationButton.setVisibility(View.GONE);
                break;

            case R.id.my_location_view:
                getDeviceLocation();
                break;

            default:
                setDefaultLocation();
                break;
        }
    }

    private void setDefaultLocation() {
        mNewLocation = mSearchLocationBox.getText().toString();

        if (mNewLocation.equals(mDefaultLocation)) {
            mBundle.putBoolean("isNewLocation", false);
        }

        if (mLocationObject != null) {
            mBundle.putString("locationObject", mLocationObject.toString());
        }

        PreferencesUtils.updateDashBoardData(mContext,
                PreferencesUtils.DASHBOARD_DEFAULT_LOCATION,
                mNewLocation);

        try {
            JSONObject userDetail = (!mAppConst.isLoggedOutUser() && PreferencesUtils.getUserDetail(mContext) != null ) ? new JSONObject(PreferencesUtils.getUserDetail(mContext)) : null;
            if (userDetail != null){
                if (!mNewLocation.isEmpty()) {
                    userDetail.put(PreferencesUtils.USER_LOCATION_LATITUDE, mLocationObject.optDouble("latitude"));
                    userDetail.put(PreferencesUtils.USER_LOCATION_LONGITUDE, mLocationObject.optDouble("longitude"));
                } else {
                    userDetail.put(PreferencesUtils.USER_LOCATION_LATITUDE, 0);
                    userDetail.put(PreferencesUtils.USER_LOCATION_LONGITUDE, 0);
                }
                PreferencesUtils.updateUserDetails(mContext, userDetail.toString());
                mBundle.putInt("user_id", userDetail.optInt("user_id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mOnCheckInLocationResponseListener != null) {
            mOnCheckInLocationResponseListener.onCheckInLocationChanged(mBundle);
            dismiss();
        }
    }

    private void getDeviceLocation() {
        if (!mAppConst.checkManifestPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            mAppConst.requestForManifestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                    ConstantVariables.ACCESS_FINE_LOCATION);
        } else if (mCurrentLocation != null) {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(mCurrentLocation.getLatitude(),
                        mCurrentLocation.getLongitude(), 1);

                if (addresses != null) {
                    Address address = addresses.get(0);
                    ArrayList<String> addressFragments = new ArrayList<String>();

                    // Fetch the address lines using getAddressLine and join them.
                    for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        addressFragments.add(address.getAddressLine(i));
                    }
                    mNewLocation = TextUtils.join(System.getProperty("line.separator"), addressFragments);
                    mSearchLocationBox.setText(mNewLocation);

                    mLocationObject = new JSONObject();
                    mLocationObject.put("country", address.getCountryName());
                    mLocationObject.put("state", address.getAdminArea());
                    mLocationObject.put("zipcode", address.getPostalCode());
                    mLocationObject.put("city", address.getSubAdminArea());
                    mLocationObject.put("countryCode", address.getCountryCode());
                    mLocationObject.put("address", address.getLocality());
                    mLocationObject.put("formatted_address", mNewLocation);
                    mLocationObject.put("location", mNewLocation);
                    mLocationObject.put("latitude", address.getLatitude());
                    mLocationObject.put("longitude", address.getLongitude());

                    setDefaultLocation();
                }
            } catch (IOException | IndexOutOfBoundsException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            requestForDeviceLocation();
        }

    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (mOnCheckInLocationResponseListener != null && isOpenCheckIn) {
            mOnCheckInLocationResponseListener.onCheckInLocationChanged(null);
        }
    }

    public void createLocationRequest() {
        myLocationRequest = new LocationRequest();
        myLocationRequest.setInterval(10000);
        myLocationRequest.setFastestInterval(5000);
        myLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    protected void startLocationUpdates() {
        if(mAppConst.checkManifestPermission(Manifest.permission.ACCESS_FINE_LOCATION))
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, myLocationRequest, this);
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    /** A class, to download Google Places */
    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            mProgressBar.setVisibility(View.GONE);
            if (mSearchLocationBox.getText().length() > 0) {
                mRemoveLocationButton.setVisibility(View.VISIBLE);
            }
            if (data != null) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if(jsonObject.has("error_message")){
                        SnackbarUtils.displaySnackbarLongWithListener(findViewById(R.id.searchListLayout),
                                jsonObject.optString("error_message"),
                                new SnackbarUtils.OnSnackbarDismissListener() {
                                    @Override
                                    public void onSnackbarDismissed() {
                                         dismiss();
                                    }
                                });
                    }else{
                        if (mCheckInAdapter != null) {
                            mCheckInAdapter.clear();
                        }
                        if (jsonObject != null && jsonObject.length() != 0) {
                            JSONArray locationResults = jsonObject.getJSONArray("results");
                            for (int i = 0; i < locationResults.length(); i++) {

                                JSONObject placeObject = locationResults.getJSONObject(i);
                                JSONObject locationObject = placeObject.optJSONObject("geometry").optJSONObject(
                                        "location");
                                double latitude = locationObject.optDouble("lat");
                                double longitude = locationObject.optDouble("lng");
                                String formattedAddress = placeObject.optString("formatted_address");

                                String placeId = placeObject.getString("place_id");
                                String locationLabel = placeObject.getString("name");
                                String locationIcon = placeObject.getString("icon");
                                JSONArray typesArray = placeObject.optJSONArray("types");
                                String vicinity = placeObject.optString("vicinity", "");

                                if (vicinity != null && !vicinity.isEmpty()) {
                                    mCheckInList.add(new CheckInList(locationIcon, placeId, locationLabel,
                                            formattedAddress, latitude, longitude, vicinity, typesArray));
                                } else {
                                    mCheckInList.add(new CheckInList(locationIcon, placeId, locationLabel,
                                            formattedAddress, latitude, longitude, formattedAddress, typesArray));
                                }

                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mCheckInAdapter.notifyDataSetChanged();
            }
        }

    }

    /* Check If User Stopped Typing till 1 second */

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TRIGGER_SERACH && isSendRequest) {

                if( searchText != null && !searchText.isEmpty()) {
                    mTextSearchUrl = new StringBuilder(UrlUtil.PLACES_API_BASE
                            + "/textsearch" + "/json");
                    try {
                        mTextSearchUrl.append("?query=").append(URLEncoder.encode(searchText, "utf8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONObject userDetail = (!mAppConst.isLoggedOutUser()) ? new JSONObject(PreferencesUtils.getUserDetail(mContext)) : null;
                        if (mLatitude == 0.0 && userDetail != null){
                            mLatitude = userDetail.optDouble(PreferencesUtils.USER_LOCATION_LATITUDE);
                            mLongitude = userDetail.optDouble(PreferencesUtils.USER_LOCATION_LONGITUDE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mTextSearchUrl.append("&location=");
                    mTextSearchUrl.append(Double.toString(mLatitude));
                    mTextSearchUrl.append(",");
                    mTextSearchUrl.append(Double.toString(mLongitude));
                    mTextSearchUrl.append("&radius=50000");
                    mTextSearchUrl.append("&key=" + mContext.getResources().getString(R.string.places_api_key));

                    mProgressBar.setVisibility(View.VISIBLE);
                    mRemoveLocationButton.setVisibility(View.GONE);

                    // Creating a new non-ui thread task to download json data
                    PlacesTask placesTask = new PlacesTask();

                    // Invokes the "doInBackground()" method of the class PlaceTask
                    placesTask.execute(mTextSearchUrl.toString());
                } else if (mAppConst.checkManifestPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    searchText = null;
                    onLocationChanged(mLastLocation);
                }
            }
        }
    };

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ConstantVariables.ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestForDeviceLocation();
                }
                break;
        }
    }
    public void requestForDeviceLocation() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(myLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                            if (!isOpenCheckIn){
                                createLocationRequest();
                                startLocationUpdates();
                                getDeviceLocation();
                            }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult((Activity) mContext, ConstantVariables.PERMISSION_GPS_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ConstantVariables.PERMISSION_GPS_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        if (isOpenCheckIn) {
                            mMyLocationButton.setVisibility(View.GONE);
                            findViewById(R.id.separator_search).setVisibility(View.GONE);
                        }
                        startLocationUpdates();
                        break;

                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.for_better_results_enable_gps), Toast.LENGTH_LONG).show();
                        break;

                    default:
                        break;
                }
                break;
        }
    }
}
