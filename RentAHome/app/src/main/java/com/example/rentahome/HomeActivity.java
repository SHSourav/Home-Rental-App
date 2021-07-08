package com.example.rentahome;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rentahome.Prevalent.Prevalent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    private GoogleMap mMap,nMap;
    String sharelink1;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationRequest mLocationRequest;
    public TextView userNameTextView;
    public CircleImageView profileImageView;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 200000;
    private long FASTEST_INTERVAL = 500000;
    private LocationManager locationManager;
    private LatLng latLng;
    private boolean isPermission;
    private DatabaseReference mDatabase;
    private ArrayList<Marker> tmpRealTimeMarkers=new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers=new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        mDatabase=FirebaseDatabase.getInstance().getReference("FlatInfo");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        userNameTextView=headerView.findViewById(R.id.userProfileName);
        profileImageView=headerView.findViewById(R.id.userProfileImage);
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);
        userNameTextView.setText(Prevalent.currentOnlineUser.getName());


        if (requestSinglePermission()) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            LocationManager mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            checkhLocation();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            // Handle the camera action
        } else if (id == R.id.myprofile) {

            Intent intent=new Intent(HomeActivity.this, MyAccount.class);
            startActivity(intent);

        } else if (id == R.id.postflat) {
            Intent intent=new Intent(HomeActivity.this, PostFlat.class);
            startActivity(intent);


        } else if (id == R.id.mypost) {

                Intent intent=new Intent(HomeActivity.this, MyPosts.class);
                startActivity(intent);

        }else if (id == R.id.csupport) {

            Intent intent=new Intent(HomeActivity.this, ContactUs.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            DatabaseReference OtherRef= FirebaseDatabase.getInstance().getReference().child("Admins").child("Contact");
            OtherRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){

                        sharelink1 = dataSnapshot.child("sharelink").getValue().toString();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            Intent Share=new Intent(Intent.ACTION_SEND);
            Share.setType("text/plain");
            String body=sharelink1;
            String subject="Please Share The App:";

            Share.putExtra(Intent.EXTRA_TEXT,body);
            Share.putExtra(Intent.EXTRA_SUBJECT,subject);

            startActivity(Share);


        }
        else if (id == R.id.nav_logout) {

                Paper.book().destroy();
                Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(intent);



        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private boolean checkhLocation() {
        if (!isLocationEnabled()) {

            showAlert();
        }
        return isLocationEnabled();
    }

    private void showAlert() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Setting is set to 'Off'.\nPlease Enable Location to" +
                        "use Map")
                .setPositiveButton("Location Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean requestSinglePermission() {
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).
                withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        isPermission = true;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            isPermission = false;
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {

                    }

                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
        return isPermission;
    }




    @Override
    public void onMapReady(final GoogleMap googleMap) {


        mMap = googleMap;
        if (latLng != null) {


            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18F));

        }

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                for(Marker marker:realTimeMarkers){
                    marker.remove();
                }
                for(DataSnapshot snapshot: datasnapshot.getChildren()){

                      nMap=googleMap;
                    final MapHelper mh= snapshot.getValue(MapHelper.class);

                    Double latitude=Double.parseDouble(mh.getLatitude());
                    Double longitude=Double.parseDouble(mh.getLongitude());
                    MarkerOptions markerOptions=new MarkerOptions().title(mh.getUserkey()).icon(bitmapDescriptorFromVector(
                            getApplicationContext(),R.drawable.icon4));
                    markerOptions.position(new LatLng(latitude,longitude));
                    tmpRealTimeMarkers.add(nMap.addMarker(markerOptions));
                    nMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            String markertitle=marker.getTitle();
                            Intent i=new Intent(HomeActivity.this, detailsActivity.class);
                            i.putExtra("userkey",markertitle);
                            startActivity(i);
                            return false;
                        }
                    });
                }

                realTimeMarkers.clear();
                realTimeMarkers.addAll(tmpRealTimeMarkers);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context,int vectorResId){
        Drawable vectorDrawable= ContextCompat.getDrawable(context,vectorResId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap=Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getMinimumHeight(),Bitmap.Config.ARGB_8888);

        Canvas canvas=new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }


    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation == null) {
            startLocationUpdates();
        } else {
            Toast.makeText(this, "Location is not Detected", Toast.LENGTH_SHORT).show();
        }


    }

    private void startLocationUpdates() {

        mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL).setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    @Override
    public void onLocationChanged(Location location) {



        LocationHelper helper = new LocationHelper(
                location.getLongitude(), location.getLatitude()
        );

        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


}
