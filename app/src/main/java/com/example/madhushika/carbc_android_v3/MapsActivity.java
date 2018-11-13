package com.example.madhushika.carbc_android_v3;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng clickedLocation;
    String currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        // Add a marker in Sydney and move the camera
        LatLng mora = new LatLng(-6.798280, 79.901357);
        mMap.addMarker(new MarkerOptions().position(mora).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mora));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
                public void onMapClick(LatLng latLng) {
                MarkerOptions marker = new MarkerOptions().position(latLng).title("Hello Maps ");
                clickedLocation = latLng;
                // adding marker
                mMap.addMarker(marker);

                double lat = latLng.latitude;
                double lng = latLng.longitude;

                Geocoder gcd = new Geocoder(getBaseContext(),
                        Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(lat,
                            lng, 1);

                    if (addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String locality = addresses.get(0).getLocality();
                        String subLocality = addresses.get(0).getSubLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();
                        if (subLocality != null) {
                            currentLocation =  subLocality;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }



}
