package com.example.martyna.mapa;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.martyna.mapa.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    public double currentLat;
    public double currentLong;
    public LatLng currentLocalisation;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private static final long PROX_ALERT_EXPIRATION = -1; // It will never expire
    private static final String PROX_ALERT_INTENT = "com.example.martyna.mapa.MapsActivity";
    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

        //Fused Location Client for obtaining last known location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //get connected to Firebase
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("places");

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

        //disable 'get directions' buttons
        mMap.getUiSettings().setMapToolbarEnabled(false);

        //show current location
        mMap.setMyLocationEnabled(true);

        //check permissions for getting localisation
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!permissionGranted) {
            Log.i("tag", "brak pozwolen");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        //add current location marker
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.

                if(location != null){
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();
                    // Add a marker in current location and move the camera
                    currentLocalisation = new LatLng(currentLat, currentLong);

                    handleNewLocation(location);

                }else{
                    Log.i("tag", "nie mozna odnalezc lokalizacji");
                }

            }

            private void handleNewLocation(Location location) {
                double currentLatitude = location.getLatitude();
                double currentLongitude = location.getLongitude();
                LatLng latLng = new LatLng(currentLatitude, currentLongitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 21));
            }

        });

        //add marker and circle area for all saved places
        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                double baseLat = dataSnapshot.child("latlng").child("latitude").getValue(double.class);
                double baseLng = dataSnapshot.child("latlng").child("longitude").getValue(double.class);
                String baseName = dataSnapshot.child("name").getValue(String.class);
                double baseRange = Double.parseDouble(dataSnapshot.child("range").getValue(String.class))*10;
                String baseId = dataSnapshot.child("id").getValue(String.class);

                LatLng newLocation = new LatLng(baseLat, baseLng);

                Log.i("tag", "newLocation: " + newLocation);

                mMap.addMarker(new MarkerOptions()
                        .position(newLocation)
                        .title(baseName)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.addCircle(new CircleOptions()
                        .center(newLocation)
                        .radius(baseRange)
                        .strokeColor(Color.rgb(43,56,124))
                        .fillColor(Color.argb(128,114,128,206))
                        .strokeWidth(5.0f));

                //add proximity alerts for each saved area
                Intent intent = new Intent(PROX_ALERT_INTENT + baseId);
                intent.putExtra("name", baseName);
                PendingIntent proximityIntent = PendingIntent.getBroadcast(MapsActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                boolean permissionGranted = ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

                if (!permissionGranted) {
                    Log.i("tag", "brak pozwolen");
                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                }

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                locationManager.addProximityAlert(
                        baseLat, // the latitude of the central point of the alert region
                        baseLng, // the longitude of the central point of the alert region
                        (float) baseRange, // the radius of the central point of the alert region, in meters
                        PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no                           expiration
                        proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
                );

                IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT + baseId);
                registerReceiver(new ProximityIntentReceiver(), filter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


    }

    //add additional info about place being saved
    public void addMyLocation(View v){
        Log.i("tag", "currentLocalisation: " + currentLocalisation);
        Intent intent = new Intent(MapsActivity.this, PlacesActivity.class);
        intent.putExtra("localisation", currentLocalisation);
        startActivity(intent);
    }


}
