package com.example.kalmanfiltersensorfusion;


import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.tasks.Task;

import java.util.Locale;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;

    private LocationCallback locationCallback;
    private Location mPrevLocation;
    private static final long INTERVAL = 2000; // 2 sec
    private static final long UPDATE_INTERVAL = 5000; // 5 seconds
    private TextView speedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speedTextView = findViewById(R.id.sample_text);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest locationRequest = new LocationRequest.Builder(UPDATE_INTERVAL)
                .setMinUpdateIntervalMillis(INTERVAL)
                .build();

//      Initialize location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    updateSpeed(location);
                }
            }
        };

        // Check location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates(locationRequest);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void requestLocationUpdates(LocationRequest locationRequest) {
        // Check location settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build());
        task.addOnCompleteListener(taskResult -> {
            try {
                taskResult.getResult();
                if (ActivityCompat.checkSelfPermission(MainActivity.this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    return;
                }
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } catch (Exception e) {
                Toast.makeText(this, "LocationSettings: Error: " + e.getMessage() ,Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }



    @Override
    public void onLocationChanged(@NonNull Location location) {
        updateSpeed(location);
    }

    private void updateSpeed(Location location) {
        if (mPrevLocation != null) {
            float distance = mPrevLocation.distanceTo(location); // Distance in meters
            long timeElapsed = (location.getTime() - mPrevLocation.getTime()) / 1000; // Time elapsed in seconds

            // Check for potential division by zero
            if (timeElapsed > 0) {
                float speed = distance / timeElapsed; // Speed in meters per second
                // Convert speed to km/h or mph if needed
                speed = speed * (float) (3.6); // Convert m/s to km/h

                // Update UI with the calculated speed
                speedTextView.setText(String.format(Locale.getDefault(), "Speed: %.2f km/h", speed));
            } else {
                Toast.makeText(this, "Location, Time elapsed is zero, skipping speed calculation", Toast.LENGTH_LONG).show();
            }
        }
        // Store the current location as the previous location for the next update
        mPrevLocation = location;
    }
}