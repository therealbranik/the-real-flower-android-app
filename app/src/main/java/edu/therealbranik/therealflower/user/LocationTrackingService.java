package edu.therealbranik.therealflower.user;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.homescreen.HomescreenActivity;

public class LocationTrackingService extends Service {
    public static final String BROADCAST_ACTION = ".homescreen.OnLocationChangeReceiver";
    private static final int SECOND = 1000;
    public LocationManager locationManager;
    public MyLocationListener listener;

    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    Intent intent;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30 * SECOND, 0, (LocationListener) listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30 * SECOND, 0, listener);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                heavyWork();
            }
        });
        thread.start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        super.onStartCommand(intent, flags, startId);


        return START_STICKY;
    }

    private void heavyWork () {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, HomescreenActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Intent buttonIntent = new Intent(this, LocationServiceDismissReceiver.class);
        buttonIntent.putExtra("notificationId",1);
        PendingIntent btPendingIntent = PendingIntent.getBroadcast(this, 0, buttonIntent,0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getResources().getString(R.string.location_tracking))
                .setContentText(input)
                .setSmallIcon(R.drawable.baseline_my_location_24)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.baseline_location_disabled_24, getResources().getString(R.string.stop), btPendingIntent)
                .build();

        startForeground(1, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.location_tracking_stopped), Toast.LENGTH_SHORT).show();
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(listener);
        super.onDestroy();
    }


    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i("*****", "Location changed");
                        loc.getLatitude();
                        loc.getLongitude();
                        intent.putExtra("lat", loc.getLatitude());
                        intent.putExtra("lon", loc.getLongitude());
                        intent.putExtra("provider", loc.getProvider());
                        sendBroadcast(intent);
                }
            });

            thread.start();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,"Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}