package com.androidlmy.mapselectaddress.track;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.androidlmy.mapselectaddress.R;
import com.androidlmy.mapselectaddress.bean.TrackBean;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.ButterKnife;

public class TrackActivity extends AppCompatActivity implements View.OnClickListener, AMap.OnMapClickListener {
    private MapView mapView;
    private AMap aMap;
    private double droneLocationLat = 181, droneLocationLng = 181;
    private boolean isAdd = false;
    private final Map<Integer, Marker> mMarkers = new ConcurrentHashMap<Integer, Marker>();
    private Marker droneMarker = null;
    private Button locate, add, clear;
    private Polyline polyline;
    ArrayList<LatLng> latLngList = new ArrayList<LatLng>();

    public static void show(Context context) {
        context.startActivity(new Intent(context, TrackActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        ButterKnife.bind(this);
        mapView = findViewById(R.id.map);
        locate = findViewById(R.id.locate);
        add = findViewById(R.id.add);
        clear = findViewById(R.id.clear);
        locate.setOnClickListener(this);
        add.setOnClickListener(this);
        clear.setOnClickListener(this);
        mapView.onCreate(savedInstanceState);

        initMapView();
        getguiji();
        aMap.addPolyline((new PolylineOptions())
                .addAll(latLngList)
                .width(10)
                .setDottedLine(false)
                .color(Color.BLACK)
        );
    }

    private void getguiji() {
        List<TrackBean> trackBeans = LitePal.findAll(TrackBean.class);
        for (TrackBean trackBean : trackBeans) {
            latLngList.add(new LatLng(trackBean.getJingdu(), trackBean.getWeidu()));
        }
    }


    private void initMapView() {
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setOnMapClickListener(this);// add the listener for click for amap object
        }

//        LatLng shenzhen = new LatLng(22.5362, 113.9454);
        LatLng shenzhen = new LatLng(34.776075, 113.731296);
        aMap.addMarker(new MarkerOptions().position(shenzhen).title("Marker in Shenzhen"));
        aMap.moveCamera(CameraUpdateFactory.newLatLng(shenzhen));
    }


    @Override
    public void onMapClick(final LatLng point) {
        if (isAdd == true) {

            Log.e("TAG", "onMapClick:" + point);

            markWaypoint(point);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(0);
                        latLngList.add(point);
                        Log.e("TAG", "start_thread: " + latLngList.toString());
                        aMap.addPolyline((new PolylineOptions())
                                .addAll(latLngList)
                                .width(10)
                                .setDottedLine(false)
                                .color(Color.BLACK)
                        );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        } else {
            setResultToToast("Cannot Add Waypoint");
        }
    }

    // Update the drone location based on states from MCU.
    private void updateDroneLocation() {
        LatLng pos = new LatLng(droneLocationLat, droneLocationLng);
        //Create MarkerOptions object
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(pos);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_gps_green));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (droneMarker != null) {
                    droneMarker.remove();
                }
                if (checkGpsCoordination(droneLocationLat, droneLocationLng)) {
                    droneMarker = aMap.addMarker(markerOptions);
                }
            }
        });
    }

    private void cameraUpdate() {
        LatLng pos = new LatLng(droneLocationLat, droneLocationLng);
        float zoomlevel = (float) 18.0;
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(pos, zoomlevel);
        aMap.moveCamera(cu);

    }

    public static boolean checkGpsCoordination(double latitude, double longitude) {
        return (latitude > -90 && latitude < 90 && longitude > -180 && longitude < 180) && (latitude != 0f && longitude != 0f);
    }

    private void setResultToToast(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TrackActivity.this, string, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markWaypoint(LatLng point) {
        //Create MarkerOptions object
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_gps_green));
        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        Marker marker = aMap.addMarker(markerOptions);
        mMarkers.put(mMarkers.size(), marker);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.locate: {
                updateDroneLocation();
                cameraUpdate(); // Locate the drone's place
                break;
            }
            case R.id.add: {
                enableDisableAdd();
                break;
            }
            case R.id.clear: {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        aMap.clear();
                        latLngList.clear();
                    }
                });
                updateDroneLocation();
                break;
            }
        }
    }

    private void enableDisableAdd() {
        if (isAdd == false) {
            isAdd = true;
            add.setText("Exit");
        } else {
            isAdd = false;
            add.setText("Add");
        }
    }

}
