package com.example.map;

import com.example.map.PermissionsRequestor.ResultListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Looper;
import android.util.Log;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintHelper;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ui.AppBarConfiguration;
import com.example.map.databinding.ActivityMainBinding;
import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.Location;
import com.here.sdk.core.engine.SDKNativeEngine;
import com.here.sdk.core.engine.SDKOptions;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapviewlite.LayerState;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapLayer;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;
import com.here.sdk.mapviewlite.MapOverlay;
import com.here.sdk.mapviewlite.MapPolyline;
import com.here.sdk.mapviewlite.MapPolylineStyle;
import com.here.sdk.mapviewlite.MapScene;
import com.here.sdk.mapviewlite.MapSceneConfig;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.here.sdk.mapviewlite.PixelFormat;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.security.auth.login.LoginException;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private MapViewLite mapView;

    private TextView text;
    private static final String TAG = MainActivity.class.getSimpleName();
    private PermissionsRequestor permissionsRequestor;
    private findmylocation Findmylocation;

    public Integer iterations = 0;

    public Integer i=0;

    public Math m;
    public MapMarker mapMarker;

    private Location endlocation;
    public ArrayList<GeoCoordinates> coordinates = new ArrayList<>();

    private SharedPreferences mPrefs;
    private int mCurViewMode;
    private ConstraintHelper m_vwJokeLayout;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeHERESDK();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setContentView(R.layout.activity_main);
        RelativeLayout r = findViewById(R.id.info);

        text= (TextView) findViewById(R.id.textView1);

        // Get a MapViewLite instance from the layout.
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        if(text.getParent() != null) {
            ((ViewGroup)text.getParent()).removeView(text); // <- fix
        }

        mapView.addView(text);





        handlepermissions();




    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initializeHERESDK() {
        // Set your credentials for the HERE SDK.
        String accessKeyID = "EXBYQ0qoQecRTqVkSCfuVA";
        String accessKeySecret = "YtIlUMDZXDO7XL2Z6TR8jYMyAp9QI3_3dKYbBL7CZTN09J3HMYetUd2Lj-9Np5aQHt3DaGt0CN-_ezeNWrG9AQ";
        SDKOptions options = new SDKOptions(accessKeyID, accessKeySecret);
        try {
            Context context = this;
            SDKNativeEngine.makeSharedInstance(context, options);
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of HERE SDK failed: " + e.error.name());
        }
    }

    private void loadMapScene(Location location) {

        // Load a scene from the SDK to render the map with a map style.
        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapScene.ErrorCode errorCode) {
                if (errorCode == null) {

                    mapView.getCamera().setTarget(new GeoCoordinates(location.coordinates.latitude, location.coordinates.longitude));
                    System.out.println("latitude :" + location.coordinates.latitude + "longitude :" + location.coordinates.longitude);
                    mapView.getCamera().setZoomLevel(14);

                    setLocation(location);



                } else {
                    Log.d("loadMapScene()", "onLoadScene failed: " + errorCode.toString());
                }
            }

        });
    }

    private void handlepermissions() {
        permissionsRequestor = new PermissionsRequestor(this);
        Context context = this;
        permissionsRequestor.request(new ResultListener() {
            @Override
            public void permissionsGranted() {
                Findmylocation = new findmylocation(context);
                Findmylocation.startLocating(new findmylocation.PlatformLocationListener() {
                    @Override
                    public void onLocationUpdated(android.location.Location location) {
                        addlocation(convertlocation(location));
//                        System.out.println("my starting location is "+ mylocation.coordinates.latitude+mylocation.coordinates.longitude);
                        loadMapScene( convertlocation(location));


                    }
                });


            }

            @Override
            public void permissionsDenied() {
                Log.e(TAG, "permissionsDenied: ");
            }
        });


    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsRequestor.onRequestPermissionsResult(requestCode, grantResults);
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();




    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        disposeHERESDK();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        mapView.onSaveInstanceState();
        super.onSaveInstanceState(outState);
    }

    private void disposeHERESDK() {
        // Free HERE SDK resources before the application shuts down.
        // Usually, this should be called only on application termination.
        // Afterwards, the HERE SDK is no longer usable unless it is initialized again.
        SDKNativeEngine sdkNativeEngine = SDKNativeEngine.getSharedInstance();
        if (sdkNativeEngine != null) {
            sdkNativeEngine.dispose();
            // For safety reasons, we explicitly set the shared instance to null to avoid situations,
            // where a disposed instance is accidentally reused.
            SDKNativeEngine.setSharedInstance(null);
        }
    }


    public Location convertlocation(android.location.Location location) {
        GeoCoordinates geoCoordinates = new GeoCoordinates(
                location.getLatitude(),
                location.getLongitude()

        );
        Location location1 = new Location(geoCoordinates);


        if (location.hasSpeed()) {
            location1.speedInMetersPerSecond = (double) location.getSpeed();
        }

        if (location.hasAccuracy()) {
            location1.horizontalAccuracyInMeters = (double) location.getAccuracy();
        }
        return location1;
    }

    public void setLocation(Location location) {
        iterations++;
        Context context = this;

        if (iterations > 1) {

          //  System.out.println("my ending location is "+ endlocation.coordinates.latitude+endlocation.coordinates.longitude);
            MapPolyline mapPolyline = createPolyline(location);
            mapView.getMapScene().addMapPolyline(mapPolyline);
            mapMarker.setVisible(false);
            BlankFragment blankFragment = new BlankFragment();
            blankFragment.setTextView(blankFragment.getView(),location);

        }
        GeoCoordinates geoCoordinates = location.coordinates;

        MapImage mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.movement_icon);


        mapMarker = new MapMarker(geoCoordinates);
        mapMarker.addImage(mapImage, new MapMarkerImageStyle());


        mapView.getMapScene().addMapMarker(mapMarker);

        // Findmylocation.stopLocating();
    }

    private MapPolyline createPolyline(Location location) {

        System.out.println("create polyline at latitude :" + location.coordinates.latitude + "and longitude :" + location.coordinates.longitude);

        addlocation(location);

        coordinates.add(new GeoCoordinates(location.coordinates.latitude, location.coordinates.longitude));
        if (iterations > 1) {
            coordinates.add(new GeoCoordinates(location.coordinates.latitude, location.coordinates.longitude));
        }
        System.out.println(location.coordinates);

        GeoPolyline geoPolyline;
        try {
            geoPolyline = new GeoPolyline(coordinates);

            if (!coordinates.isEmpty()) {
                TextView textview= (TextView) findViewById(R.id.textView1);
                /*
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout m_vwJokeLayout = (LinearLayout) this.findViewById(R.id.map_view);
                TextView tv=new TextView(this);
                tv.setLayoutParams(lparams);
                tv.setText("test");
                this.m_vwJokeLayout.addView(tv);

                BlankFragment blankFragment = new BlankFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.info, blankFragment, blankFragment.getTag())
                        .commit();
                        */

            }
        } catch (InstantiationErrorException e) {
            // Less than two vertices.
            return null;
        }
        MapPolylineStyle mapPolylineStyle = new MapPolylineStyle();
        mapPolylineStyle.setWidthInPixels(20);
        mapPolylineStyle.setColor(0xFF0000A0, PixelFormat.RGBA_8888);
        MapPolyline mapPolyline = new MapPolyline(geoPolyline, mapPolylineStyle);


        return mapPolyline;

    }
    public void addlocation(Location mylocation){

        coordinates.add(mylocation.coordinates);
        System.out.println("im add a new coordinate at lat:"+ coordinates.get(0).latitude+"long :" +coordinates.get(0).longitude);
    }

    public Location Getlocation(int pos){

        Location mylocation = new Location(new GeoCoordinates(coordinates.get(pos).longitude,coordinates.get(pos).latitude));


        return mylocation;
    }

}

