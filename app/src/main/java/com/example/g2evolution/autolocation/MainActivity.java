package com.example.g2evolution.autolocation;

import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks  {

    private AutoCompleteTextView mAutocompleteTextView;


    private static final int GOOGLE_API_CLIENT_ID = 0;

    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final String LOG_TAG = "MainActivity";
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAutocompleteTextView=(AutoCompleteTextView)findViewById(R.id.autotextview);

        //this implementation for location auto search ***************
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView.setThreshold(2);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);



    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);

            //base on place id get lat and long and save as user
            Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getStatus().isSuccess()) {
                                final Place myPlace = places.get(0);
                                LatLng queriedLocation = myPlace.getLatLng();

                                Log.e("testing","latitude = "+queriedLocation.latitude);
                                Log.e("testing","latitude = "+queriedLocation.longitude);


                            }
                            places.release();
                        }
                    });
        }
    };

    //error
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

            String place1 = place.getName().toString() +
                    place.getName() + place.getAddress().toString() + place.getPhoneNumber().toString();
            LatLng latLng = place.getLatLng();

            String strname = place.getName().toString();
            String straddress = place.getAddress().toString();

            Log.e("testing","palce = "+ place.getName());
            Log.e("testing","Address = "+ place.getAddress());
            Log.e("testing","Id = "+ place.getId());
            Log.e("testing","Number = "+ place.getPhoneNumber());
            Log.e("testing","Website = "+ place.getWebsiteUri());
            Log.e("testing","locale = "+ place.getLocale());


            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            String result = null;
            try {
                List<Address> addressList = geocoder.getFromLocation(
                        latLng.latitude, latLng.longitude, 1);
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append("\n");
                    }
                    sb.append(address.getLocality()).append("\n");
                    sb.append(address.getPostalCode()).append("\n");
                    sb.append(address.getCountryName());
                    result = sb.toString();
                   String strcity = address.getLocality();
                    String strpostal = address.getPostalCode();
                    String strcountry = address.getCountryName();
                    String strstate = address.getAdminArea();
                    Double doulatitude = address.getLatitude();
                    Double doulongitude = address.getLongitude();

                    String strlanti = doulatitude.toString();
                    String strlong = doulongitude.toString();

                    String strlocation = "[{"+"lat"+":"+doulatitude+","+"lan"+":"+doulongitude+","+"name"+":"+strname+","+"fullname"+":"+straddress+","+"cityname"+":"+strcity+","+"statename"+":"+strstate+","+"pincode"+":"+strpostal+"}]";
                    Log.e("testing","strcity = "+strcity);
                    Log.e("testing","strpostal = "+strpostal);
                    Log.e("testing","strstate = "+strstate);
                    Log.e("testing","strcountry = "+strcountry);
                    Log.e("testing","strlocation = "+strlocation);
                    String strapilocation = "[{"+"lat"+":"+doulatitude+","+"lan"+":"+doulongitude+","+"name"+":"+strname+","+"fullname"+":"+straddress+","+"cityname"+":"+strcity+","+"statename"+":"+strstate+","+"pincode"+":"+strpostal+"}]";
                    try {
                        JSONObject parent = new JSONObject();
                        JSONObject jsonObject = new JSONObject();
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.put("lv1");
                        jsonArray.put("lv2");

                        jsonObject.put("lat", strlanti);
                        jsonObject.put("lan", strlong);
                        jsonObject.put("name", strname);
                        jsonObject.put("fullname", straddress);
                        jsonObject.put("cityname", strcity);
                        jsonObject.put("statename", strstate);
                        jsonObject.put("pincode", strpostal);
                        parent.put("k2", jsonObject);
                        Log.d("output", parent.toString(2));

                        //strapilocation = "["+jsonObject.toString()+"]";

                        Log.e("testing", "strresult = " + strapilocation);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } catch (IOException e) {
               // Log.e(TAG, "Unable connect to Geocoder", e);
            }

        }
    };


    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();

    }


}
