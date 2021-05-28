package com.mypakdriver.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mypakdriver.R;
import com.mypakdriver.models.ModelLogin;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyService extends Service {

    FusedLocationProviderClient mFusedLocationClient;
    public static final int notify = 5000;  // interval between two services(Here Service run every 1 Minute)
    private Handler mHandler = new Handler();   // run on another Thread to avoid crash
    private Timer mTimer = null; // timer handling
    SharedPref sharedPref;
    ModelLogin modelLogin;

    public MyService() {}

    @Override
    public void onCreate() {

        requestNewLocationData();

        String channelId = "channel-01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        } else {
            startForeground(1, new Notification());
        }

        if (mTimer != null) // Cancel if already existed
            mTimer.cancel();
        else
            mTimer = new Timer();   // recreate new

        mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.cityonedriver";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("service is ", "running");
                    getLastLocation();
                }
            });
        }
    }


    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();

                        if (location == null) {
                            requestNewLocationData();
                        } else {

                            Log.e("sdfasfsfds","location lat = " + location.getLatitude());
                            Log.e("sdfasfsfds","location lon = " + location.getLongitude());

                            Intent intent1 = new Intent("data_update_location1");
                            intent1.putExtra("lat", String.valueOf(location.getLatitude()));
                            intent1.putExtra("lon", String.valueOf(location.getLongitude()));
                            sendBroadcast(intent1);

                            sharedPref = SharedPref.getInstance(MyService.this);

                            if (sharedPref.getBooleanValue(AppConstant.IS_REGISTER)) {
                                modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
                                updateProviderLatLon(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), modelLogin.getResult().getId());
                            }

                        }
                    }
                }
        );

    }

    public void updateProviderLatLon(String lat, String lon, String userId) {

        Api api = ApiFactory.getClientWithoutHeader(this).create(Api.class);

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());
        param.put("lat",lat);
        param.put("lon",lon);

        Call<ResponseBody> call = api.updateLocationApiCall(param);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("fasfasdfasd","responseString = " + responseString);
                    Log.e("fasfasdfasd","response = " + response);

                    if(jsonObject.getString("status").equals("1")) {
                        modelLogin.getResult().setLat(lat);
                        modelLogin.getResult().setLon(lon);

                        String address = ProjectUtil.getCompleteAddressString(MyService.this, Double.parseDouble(lat), Double.parseDouble(lon));

                        modelLogin.getResult().setAddress(address);

                        sharedPref.setUserDetails(AppConstant.USER_DETAILS,modelLogin);

                        // Toast.makeText(MyService.this, "Location update", Toast.LENGTH_SHORT).show();
                    } else {
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                   // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure","onFailure = " + t.getMessage());
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
      /*LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );*/
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            if (mLastLocation == null) {
                requestNewLocationData();
            } else {
                Log.e("user Latitude", "" + mLastLocation.getLatitude() + "");
                Log.e("user Longitude", "" + mLastLocation.getLongitude() + "");
                Intent intent1 = new Intent("data_update_location1");
                intent1.putExtra("latitude", String.valueOf(mLastLocation.getLatitude()));
                intent1.putExtra("longitude", String.valueOf(mLastLocation.getLongitude()));
                sendBroadcast(intent1);
//              SessionManager.writeString(getApplicationContext(), SessionManager.USER_LATITUDE, "");
//              SessionManager.writeString(getApplicationContext(), SessionManager.USER_LONGITUDE, "");
                //  latTextView.setText(mLastLocation.getLatitude()+"");
                //  lonTextView.setText(mLastLocation.getLongitude()+"");
                //  Toast.makeText(LocationService.this, ""+mLastLocation.getLatitude(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        System.out.println("service in onTaskRemoved");
        long ct = System.currentTimeMillis(); // get current time
        Intent restartService = new Intent(getApplicationContext(), MyService.class);
        PendingIntent restartServicePI = PendingIntent.getService (
                getApplicationContext(), 0, restartService,
                0);

        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.setRepeating(AlarmManager.RTC_WAKEUP, ct, 1 * 4000, restartServicePI);
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }
    }

}