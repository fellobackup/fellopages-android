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

package com.fellopages.mobileapp.classes.common.activities;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng latLng;
    private double latitude, longitude;
    private String location, placeId;
    private Toolbar mToolbar;
    private AppConstant mAppConst;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        location = getIntent().getStringExtra("location");
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);
        placeId = getIntent().getStringExtra("place_id");
        position = getIntent().getIntExtra("position",0);


        /*
        Set Back Button on Action Bar
         */
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(getResources().getString(R.string.map_header_name) + ": " + location);

        CustomViews.createMarqueeTitle(this, mToolbar);

        mAppConst = new AppConstant(this);
        if(!mAppConst.checkManifestPermission(Manifest.permission.ACCESS_COARSE_LOCATION)){
            finish();
        }else{
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);

            mapFragment.getMapAsync(this);

            /**
             * Load the Map if latitude and longitude is coming from API.
             * Else Fetch the latitude and longitude from place_id coming from API
             * Else Fetch the latitude and longitude from location label
             */

            if(latitude != 0 && longitude != 0){
                latLng = new LatLng(latitude, longitude );
            } else if(placeId != null){
                try {
                    PlacesTask placesTask = new PlacesTask();
                    StringBuilder placeApiUrl = new StringBuilder(UrlUtil.PLACES_API_BASE + "/details/json");
                    placeApiUrl.append("?placeid=").append(URLEncoder.encode(placeId, "utf8"));
                    placeApiUrl.append("&key=" + getResources().getString(R.string.places_api_key));

                    placesTask.execute(placeApiUrl.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }else {
                latLng = getLatLngFromLocation(location);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(MapActivity.this)) {
                SoundUtil.playSoundEffectOnBackPressed(MapActivity.this);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);

        if(latLng != null){
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title(location));
        }
    }

    public LatLng getLatLngFromLocation(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng latLng = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null || address.isEmpty()) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            latLng = new LatLng(location.getLatitude(), location.getLongitude() );
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return latLng;
    }

    /** A class, to download Details of a place using it's place_id */
    private class PlacesTask extends AsyncTask<String, Integer, LatLng> {

        // Invoked by execute() method of this object
        @Override
        protected LatLng doInBackground(String... url) {
            try {
                latLng = getLatLngFromPlaceId(url[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return latLng;
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            if(latLng != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                onMapReady(mMap);
            }
            super.onPostExecute(latLng);
        }
    }

    private LatLng getLatLngFromPlaceId(String placesUrl){

        LatLng latLng = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            URL url = new URL(placesUrl);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);

            }
        } catch ( IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONObject result = jsonObj.getJSONObject("result").getJSONObject("geometry").
                    getJSONObject("location");
            Double longitude  = result.getDouble("lng");
            Double latitude =  result.getDouble("lat");
            latLng = new LatLng(latitude, longitude);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return latLng;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("position", position);
        setResult(ConstantVariables.ON_BACK_PRESSED_CODE,intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
