package com.example.app1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import android.Manifest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;

import android.location.Location;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MapLocation extends AppCompatActivity {

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;

    private GoogleMap mMap;
    private DatabaseReference recipesRef;
    private List<Marker> recipeMarkers;

    private CheckBox showRecipesCheckBox;
    private SeekBar radiusSeekBar;
    private TextView radiusLabel;

    private boolean showRecipesInRadius = false;
    private int selectedRadius = 0;
    private Circle radiusCircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle("Map");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        recipesRef = FirebaseDatabase.getInstance().getReference("Android Tutorials");
        recipeMarkers = new ArrayList<>();

        showRecipesCheckBox = findViewById(R.id.showRecipesCheckBox);
        radiusSeekBar = findViewById(R.id.radiusSeekBar);
        radiusLabel = findViewById(R.id.radiusLabel);

        showRecipesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showRecipesInRadius = isChecked;
                if (isChecked) {
                    showRecipeLocations();
                } else {
                    clearRecipeMarkers();
                }
            }
        });

        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedRadius = progress;
                radiusLabel.setText("Radijus: " + progress + " km");
                if (showRecipesInRadius) {
                    showRecipeLocations();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getCurrentLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        mMap = googleMap;
                        if(location != null){
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Current Location");
                            googleMap.addMarker(markerOptions);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        } else {
                            Toast.makeText(MapLocation.this, "Please enable your location", Toast.LENGTH_SHORT).show();
                        }

                        showRecipeLocations();
                    }
                });
            }
        });
    }

    private void showRecipeLocations() {
        recipesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    DataClass recipe = recipeSnapshot.getValue(DataClass.class);
                    if (recipe != null) {
                        double latitude = Double.parseDouble(recipe.getLati());
                        double longitude = Double.parseDouble(recipe.getLongi());
                        String recipeTitle = recipe.getDataTitle();
                        String userName = recipe.getUserName();

                        LatLng recipeLocation = new LatLng(latitude, longitude);

                        if (showRecipesInRadius && !isRecipeInRadius(recipeLocation)) {
                            continue;
                        }

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(recipeLocation)
                                .title(recipeTitle)
                                .snippet("Posted by: " + userName);

                        Marker marker = mMap.addMarker(markerOptions);
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                        Circle circle = mMap.addCircle(new CircleOptions()
                                .center(recipeLocation)
                                .radius(500)
                                .strokeColor(getResources().getColor(R.color.colorPrimary))
                                .fillColor(getResources().getColor(R.color.colorPrimaryTransparent)));

                        recipeMarkers.add(marker);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private boolean isRecipeInRadius(LatLng recipeLocation) {
        if (selectedRadius == 0) {
            return true; // Ako radijus nije postavljen, prikaži sve recepte
        }

        Location userLocation = new Location("");
        userLocation.setLatitude(mMap.getCameraPosition().target.latitude);
        userLocation.setLongitude(mMap.getCameraPosition().target.longitude);

        int radius = selectedRadius;

        float distance = userLocation.distanceTo(new Location("") {{
            setLatitude(recipeLocation.latitude);
            setLongitude(recipeLocation.longitude);
        }});

        return distance <= radius * 100;
    }

    private void clearRecipeMarkers() {
        for (Marker marker : recipeMarkers) {
            marker.remove();
        }
        recipeMarkers.clear();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}