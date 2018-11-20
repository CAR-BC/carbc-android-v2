package com.example.madhushika.carbc_android_v3;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Objects.ServiceStation;
import Objects.ServiceType;
import Objects.SparePartData;

import controller.Controller;
import core.connection.BlockJDBCDAO;
import core.connection.IdentityJDBC;
import core.connection.VehicleJDBCDAO;

public class ServiceActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private ListView listView;
    private ListView locationListView;
    private JSONObject jsonObject;
    private TextView vehicleNumber;
    private JSONObject serviceDataJSON;
    private JSONObject serviceStationJson;
    private JSONArray sparePartSellerList = new JSONArray();
    private Button done;
    private TextView vehicleDetailsText;

    Calendar myCalendar = Calendar.getInstance();

    String regNo;
    ServiceStation serviceStationSelected;
    Controller controller;
    ArrayList<ServiceStation> stations;
    JSONArray locationList;
    ArrayList<ServiceStation> locationArray;
    String datePicked;

    private static final String TAG = ServiceActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(6.927079, 79.861244); //Colombo
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted = true;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        IdentityJDBC identityJDBC = new IdentityJDBC();
        try {
            locationList = identityJDBC.getPeersByLocation();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (locationList.isNull(0)) {
            Toast.makeText(ServiceActivity.this, "Please select correct service station.", Toast.LENGTH_LONG).show();

        }
        locationArray = getServiceStation(locationList);

        for (ServiceStation s : locationArray){
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .title(s.getName())
                    .position(new LatLng(Double.parseDouble(s.getLattitude()),
                            Double.parseDouble(s.getLongtitude())))
                    .snippet(s.getAddress()));
            marker.setTag(s.getPublicKey());
        }

//        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                }
            });
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
//        if (mMap == null) {
//            return;
//        }
//
//        // Get the likely places - that is, the businesses and other points of interest that
//        // are the best match for the device's current location.
//        @SuppressWarnings("MissingPermission") final
//        Task<PlaceLikelihoodBufferResponse> placeResult =
//                mPlaceDetectionClient.getCurrentPlace(null);
//        placeResult.addOnCompleteListener
//                (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
//                    @Override
//                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
//
//                            // Set the count, handling cases where less than 5 entries are returned.
//                            int count;
//                            if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
//                                count = likelyPlaces.getCount();
//                            } else {
//                                count = M_MAX_ENTRIES;
//                            }
//
//                            int i = 0;
//                            mLikelyPlaceNames = new String[count];
//                            mLikelyPlaceAddresses = new String[count];
//                            mLikelyPlaceAttributions = new String[count];
//                            mLikelyPlaceLatLngs = new LatLng[count];
//
//                            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
//                                // Build a list of likely places to show the user.
//                                mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
//                                mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
//                                        .getAddress();
//                                mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
//                                        .getAttributions();
//                                mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();
//
//                                i++;
//                                if (i > (count - 1)) {
//                                    break;
//                                }
//                            }
//
//                            // Release the place likelihood buffer, to avoid memory leaks.
//                            likelyPlaces.release();
//
//                            // Show a dialog offering the user the list of likely places, and add a
//                            // marker at the selected place.
//                            openPlacesDialog();
//
//                        } else {
//                            Log.e(TAG, "Exception: %s", task.getException());
//                        }
//                    }
//                });
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
//        // Ask the user to choose the place where they are now.
//        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // The "which" argument contains the position of the selected item.
//                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
//                String markerSnippet = mLikelyPlaceAddresses[which];
//                if (mLikelyPlaceAttributions[which] != null) {
//                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
//                }
//
//                // Add a marker for the selected place, with an info window
//                // showing information about that place.
//                mMap.addMarker(new MarkerOptions()
//                        .title(mLikelyPlaceNames[which])
//                        .position(markerLatLng)
//                        .snippet(markerSnippet));
//
//                // Position the map's camera at the location of the marker.
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
//                        DEFAULT_ZOOM));
//            }
//        };
//
//        // Display the dialog.
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle(R.string.pick_place)
//                .setItems(mLikelyPlaceNames, listener)
//                .show();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        hideActionBar();

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.service_map);
        mapFragment.getMapAsync(this);

        ImageView backBtn = (ImageView) findViewById(R.id.back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent i = getIntent();
        regNo = i.getStringExtra("vid");
        vehicleNumber = (TextView) findViewById(R.id.vehicle_number);
        vehicleNumber.setText(regNo);
        final EditText edittext= (EditText) findViewById(R.id.servicedDate);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        edittext.setText(dateFormat.format(myCalendar.getTime()));

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub



                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                edittext.setText(sdf.format(myCalendar.getTime()));
                datePicked = sdf.format(myCalendar.getTime());
            }
        };


        edittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ServiceActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

//        String vid = i.getExtras().getString("vid");
        //get location and request nearby service stations
//



//        setArrayAdaptersToLocationList(locationArray);
//        setClickListnerToLocationList();
//
//        controller = new Controller();
//
//        registerReceiver(broadcastReceiver, new IntentFilter("ReceivedTransactionData"));
//
//        //catch the response and stop activity indicator
//        //getServiceTypes(response)
//        //set adapter
//        //enable buttons
//
//        done = (Button) findViewById(R.id.done_btn);
//        Button cancel = (Button) findViewById(R.id.cancel_btn);
//        done.setEnabled(false);
//        done.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActivity.this);
//                builder.setTitle("Add a Transaction");
//                builder.setMessage("Do you really need to add this transaction? ");
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                        //get json array and add
//                        //call blockchain method
//                        try {
//                            serviceDataJSON.put("SecondaryParty", serviceStationJson);
//
//                            JSONObject thirdParty = new JSONObject();
//                            thirdParty.put("SparePartProvider", sparePartSellerList);
//                            serviceDataJSON.put("ThirdParty", thirdParty);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        System.out.println(serviceDataJSON);
//                        controller.sendTransaction("ServiceRepair", VehicleJDBCDAO.vehicleNumbersWithRegistrationNumbers.get(regNo), serviceDataJSON);
//                        finish();
//                    }
//                });
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                        // User cancelled the dialog
//                    }
//                });
//
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//        });
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActivity.this);
//                builder.setTitle("Cancel a Transaction");
//                builder.setMessage("Do you really need to cancel this transaction? ");
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                        finish();
//                    }
//                });
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                        // User cancelled the dialog
//                    }
//                });
//
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//        });
    }



    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String str = intent.getStringExtra("responseFormServiceStation");
            try {
                serviceDataJSON = new JSONObject(str);
                ArrayList<ServiceType> arrayList = getServiceTypes(new JSONObject(str));
                locationArray = new ArrayList<>();
                locationArray.add(serviceStationSelected);
                setArrayAdaptersToLocationList(locationArray);
                setArrayAdaptersToServiceTypeList(arrayList);
                vehicleDetailsText.setText("Service details");
                done.setEnabled(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public LatLng getLocationFromAddress(Context context, String strAddress)
    {
        Geocoder coder= new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            if(address==null)
            {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return p1;

    }

    private void hideActionBar() {
        //Hide the action bar only if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private ArrayList<ServiceType> getServiceTypes(JSONObject object) {
        ArrayList<ServiceType> serviceTypesArray = null;
        try {

            JSONArray servicetypes = object.getJSONArray("services");
            serviceTypesArray = new ArrayList<>();

            for (int i = 0; i < servicetypes.length(); i++) {
                JSONObject service = servicetypes.getJSONObject(i);
                JSONArray spareParts = null;
                String type = null;
                if (service.has("serviceType")) {
                    type = service.getString("serviceType");
                }
                if (service.has("serviceData")) {
                    spareParts = service.getJSONArray("serviceData");
                }


                ArrayList<SparePartData> sparePartsArray = new ArrayList<>();

                if (spareParts.length() > 0) {
                    for (int j = 0; j < spareParts.length(); j++) {
                        JSONObject sparePart = spareParts.getJSONObject(j);
                        String seller = null;
                        String sparePartName = null;
                        if (sparePart.has("seller")) {
                            sparePartSellerList.put(sparePart.getString("seller"));
                            seller = sparePart.getString("seller");
                        }
                        if (sparePart.has("sparePart")) {
                            sparePartName = sparePart.getString("sparePart");
                        }
                        if (seller != null && sparePartName != null) {
                            SparePartData sparePartData = new SparePartData(seller, sparePartName);
                            sparePartsArray.add(sparePartData);
                        }
                    }
                }
                ServiceType serviceType = new ServiceType(type, sparePartsArray);
                serviceTypesArray.add(serviceType);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return serviceTypesArray;
    }


    private void setArrayAdaptersToServiceTypeList(final ArrayList<ServiceType> serviceTypes) {
//        listView = (ListView) findViewById(R.id.service_info);
//
//
//        listView.setAdapter(new BaseAdapter() {
//            @Override
//            public int getCount() {
//                return serviceTypes.size();
//            }
//
//            @Override
//            public Object getItem(int position) {
//                return serviceTypes.get(position);
//            }
//
//            @Override
//            public long getItemId(int position) {
//                return position;
//            }
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup viewGroup) {
//                final ServiceType serviceTypeItem = serviceTypes.get(position);
//                View cellUser = null;
//
//                if (convertView == null) {
//
//                    cellUser = LayoutInflater.from(ServiceActivity.this).inflate(R.layout.cell_service_info,
//                            viewGroup, false);
//
//                } else {
//                    cellUser = convertView;
//                }
//
//                Placeholder ph = (Placeholder) cellUser.getTag();
//                TextView serviceType;
//                // ListView spareParts;
//
//
//                if (ph == null) {
//                    serviceType = (TextView) cellUser.findViewById(R.id.service_type_txt);
//                    //spareParts = (ListView) cellUser.findViewById(R.id.list_spare_parts);
//
//
//                    ph = new Placeholder();
//                    ph.serviceType = serviceType;
//                    // ph.spareParts = spareParts;
//
//                    cellUser.setTag(ph);
//                } else {
//                    serviceType = ph.serviceType;
//                    // spareParts = ph.spareParts;
//
//                }
//
//                serviceType.setText(serviceTypeItem.getServiceType());
//
//                return cellUser;
//            }
//        });

    }


    private ArrayList<ServiceStation> getServiceStation(JSONArray serviceStationJSonArray) {
        ArrayList<ServiceStation> serviceStations = new ArrayList<>();
        try {
            //JSONArray serviceStationJSonArray = object.getJSONArray("data");

            for (int i = 0; i < serviceStationJSonArray.length(); i++) {
                JSONObject station = serviceStationJSonArray.getJSONObject(i);

                String name = station.getString("name");
                String address = station.getString("location");
                String publicKey = station.getString("publicKey");
                String role = station.getString("role");
                String longtitude = station.getString("longitude");
                String lattitude = station.getString("latitude");
                ServiceStation serviceStation = new ServiceStation(name, address, publicKey, role, lattitude, longtitude);
                serviceStations.add(serviceStation);
            }
            System.out.println();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return serviceStations;
    }


    private void setClickListnerToLocationList() {

        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                serviceStationJson = new JSONObject();
                JSONObject serviceStationElem = new JSONObject();
                try {
                    serviceStationElem.put("name", locationArray.get(position).getName());
                    serviceStationElem.put("publicKey", locationArray.get(position).getPublicKey());
                    serviceStationJson.put("serviceStation", serviceStationElem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                serviceStationSelected = locationArray.get(position);
                System.out.println("*********************service station name*********************");
                System.out.println(locationArray.get(position).getName());
                controller.requestTransactionDataTest("ServiceRepair", regNo, "2018/05/21", locationArray.get(position).getPublicKey());
//                        stations.add(serviceStation);
                //serviceStationJson = new JSONObject((Map) serviceStation);
            }
        });
    }

    private void setArrayAdaptersToLocationList(final ArrayList<ServiceStation> locationList) {

        locationListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return locationList.size();
            }

            @Override
            public Object getItem(int position) {
                return locationList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup viewGroup) {

                ServiceStation serviceStation = locationList.get(position);
                View cellUser = null;

                if (convertView == null) {

                    cellUser = LayoutInflater.from(ServiceActivity.this).inflate(R.layout.cell_service_location,
                            viewGroup, false);

                } else {
                    cellUser = convertView;
                }

                PlaceholderLocation ph = (PlaceholderLocation) cellUser.getTag();
                final TextView name;
                TextView address;
                final Button select;

                if (ph == null) {
                    name = (TextView) cellUser.findViewById(R.id.service_location_txt);
                    address = (TextView) cellUser.findViewById(R.id.service_location_address_txt);
                    // select = (Button) cellUser.findViewById(R.id.select_location);

                    ph = new PlaceholderLocation();
                    ph.name = name;
                    ph.address = address;
                    //ph.select = select;

                    cellUser.setTag(ph);
                } else {
                    name = ph.name;
                    address = ph.address;
                    // select = ph.select;

                }

                name.setText(serviceStation.getName());
                address.setText(serviceStation.getAddress());

                return cellUser;
            }
        });
    }


    public JSONArray getSparePartSellerList() {
        return sparePartSellerList;
    }

    public void setSparePartSellerList(JSONArray sparePartSellerList) {
        this.sparePartSellerList = sparePartSellerList;
    }


    private class Placeholder {
        public TextView serviceType;
        public ListView spareParts;
    }

    private class PlaceholderSP {
        public TextView sparepart;
    }

    private class PlaceholderLocation {
        public TextView name;
        public TextView address;
        public Button select;

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        for (ServiceStation s : locationArray){
            if (s.getPublicKey().equals(marker.getTag())){
                System.out.println("Current selected location : " + s.getAddress());
                Intent i = new Intent(ServiceActivity.this, ServiceSecondActivity.class);
                i.putExtra("vid",regNo);
                i.putExtra("station",s.getName());
                i.putExtra("stationAddress",s.getAddress());
                i.putExtra("publicKey",s.getPublicKey());
                i.putExtra("datePicked",datePicked);
                System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD"+datePicked);
                startActivity(i);
                Toast.makeText(ServiceActivity.this,"Please select the date",Toast.LENGTH_SHORT);
            }
        }
    }

    //    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(broadcastReceiver);
//    }
}
