package com.mshop.internal.verolocator;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mshop.internal.verolocator.data.MessageCode;
import com.mshop.internal.verolocator.presenter.PositionPresenter;
import com.mshop.internal.verolocator.repository.bodies.RefreshLocationBody;
import com.mshop.internal.verolocator.repository.responses.BasicResponseDto;

import java.util.HashMap;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.provider.Settings.Secure.ANDROID_ID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener, PositionPresenter.PositionView {
    private static final int PERMISSION_REQUEST_PHONE_STATE = 1000;
    private static final int UPDATING_PERIOD = 60000;


    private TextView txtPosition;
    private Button btnSendPosition;
    private ProgressBar progressBar;

    private boolean sendingPosition = false;
    private double latitude = 0;
    private double longitude = 0;
    private LocationManager locationManager;
    private PositionPresenter positionPresenter;
    private Handler handlerRefreshPosition = new Handler();
    private RunUpdatePosition runUpdatePosition;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtPosition = findViewById(R.id.txtPosition);
        btnSendPosition = findViewById(R.id.btnSendPosition);
        progressBar = findViewById(R.id.progressBar);


        btnSendPosition.setOnClickListener(this);


        positionPresenter = new PositionPresenter(AndroidSchedulers.mainThread(), Schedulers.newThread());
        positionPresenter.setPositionView(this);

        runUpdatePosition = new RunUpdatePosition();

        setUpGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_PHONE_STATE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setUpGPS();
        }
    }




    // ---------------------------------------------------------------------------------------------------------------------------------------
    // ------------------------------------------------------------ USER INTERACTION ------------------------------------------------------------
    @Override
    public void onClick(View v) {
        if (v == btnSendPosition) {

            if (sendingPosition) {
                handlerRefreshPosition.removeCallbacks(runUpdatePosition);
                btnSendPosition.setText(getString(R.string.send_position));
            } else {
                handlerRefreshPosition.post(runUpdatePosition);
                btnSendPosition.setText(getString(R.string.updating_position));
            }

            sendingPosition = !sendingPosition;
        }
    }



    // ---------------------------------------------------------------------------------------------------------------------------------------
    // ------------------------------------------------------------ GPS INTERFACE ------------------------------------------------------------
    @Override
    public void onLocationChanged(Location location) {
        progressBar.setVisibility(View.GONE);
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        String position = String.format(getString(R.string.latitude_and_longitude), String.valueOf(latitude), String.valueOf(longitude));
        txtPosition.setText(position);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        progressBar.setVisibility(View.VISIBLE);
        txtPosition.setText(getString(R.string.searching_position));
    }



    // ---------------------------------------------------------------------------------------------------------------------------------------
    // ------------------------------------------------------------ POSITION VIEW INTERFACE --------------------------------------------------
    @Override
    public void onPositionUpdated(BasicResponseDto any) {
        System.out.println("onPositionUpdated! :: " + any.getCode());
    }

    @Override
    public void onPositionServerError(MessageCode any) {
        System.out.println("onPositionServerError! :: " + any.getCode());
        Toast.makeText(this, any.getCode(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPositionError(Throwable e) {
        System.out.println("onPositionError! :: " + e.getMessage());
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }



    // ---------------------------------------------------------------------------------------------------------------------------------------
    // ------------------------------------------------------------ METHODS AND RUNNABLES ----------------------------------------------------
    private void setUpGPS() {
        int locationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (locationPermission == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (locationManager == null) {
                Toast.makeText(this, getString(R.string.gps_unavailable), Toast.LENGTH_SHORT).show();
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
                progressBar.setVisibility(View.VISIBLE);
            }
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_PHONE_STATE
            );
        }
    }

    private class RunUpdatePosition implements Runnable {
        @Override
        public void run() {
            RefreshLocationBody body = new RefreshLocationBody(0, ANDROID_ID, 100, latitude, longitude);
            positionPresenter.callToRefreshLocation(body);
            handlerRefreshPosition.postDelayed(this, UPDATING_PERIOD);
        }
    }
}
