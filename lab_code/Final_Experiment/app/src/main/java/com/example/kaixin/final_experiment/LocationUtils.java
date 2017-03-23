package com.example.kaixin.final_experiment;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.io.IOException;
import java.util.List;

public class LocationUtils {
    private static LocationManager locationManager;
    private static Location location;
    public static String cityName = "火星";
    private static Geocoder geocoder;

    public static final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location loc) {
            if (location != null) {
                location = loc;
            }
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    public static void getCNBylocation(final Context context) throws SecurityException {
        geocoder = new Geocoder(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null) {
            String queryed_name = updateWithNewLocation(location);
            if((queryed_name != null) && (0 != queryed_name.length())){
                cityName = queryed_name;
            }
        } else {
            cityName = "火星";
        }
    }

    private static String updateWithNewLocation(Location location) {
        String mcityName = "";
        double lat = 0;
        double lng = 0;
        List<Address> addList = null;

        lat = location.getLatitude();
        lng = location.getLongitude();

        try {
            addList = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addList != null && addList.size() > 0) {
            for (int i = 0; i < addList.size(); i++) {
                Address add = addList.get(i);
                mcityName += add.getLocality();
            }
        }
        if (mcityName.length() != 0) {
            return mcityName.substring(0, (mcityName.length()-1));
        } else {
            return mcityName;
        }
    }
}
