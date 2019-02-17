package com.example.softwaredevelopment2018;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


public class LocationTrack extends Service implements LocationListener
{

    private final Context context;
    boolean checkGPS = false;
    boolean checkNetwork = false;
    public static boolean canGetLocation = false;
    public static Location myLocation;
    private LocationManager locationManager;

    public LocationTrack(Context context)
    {
        this.context = context;
        getLocation();
    }

    private void getLocation()
    {
        try
        {
            locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
            checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //Get GPS provider status.
            checkNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); //Get network provider status.
            if (!checkGPS)
            {
                Log.i("Location", "GPS is turned off. Requesting GPS activation.");
                showSettingsAlert();
            }
            else
            {
                FusedLocationProviderClient mFLClient = LocationServices.getFusedLocationProviderClient(context);
                mFLClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>()
                {
                    @Override
                    public void onSuccess(Location location)
                    { //GPS location can be null if GPS is switched off.
                        if (location != null)
                        {
                            Log.i("Location", "Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
                            Toast.makeText(context, "Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude(), Toast.LENGTH_LONG).show();
                            LocationTrack.canGetLocation = true;
                        }
                        myLocation = location;
                    }
                });
            }
        }
        catch (NullPointerException e) { e.printStackTrace(); }
        catch (SecurityException e) { Log.d("Debug", "" + e.getLocalizedMessage()); }
    }

    public boolean canGetLocation()
    {
        return this.canGetLocation;
    }

    public void showSettingsAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("GPS is not Enabled!");
        builder.setMessage("Do you want to enable GPS location?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                checkGPS = true;
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(context, "Cannot get current location. Gps is disabled", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        myLocation = location;
        String message = "" + Double.toString(myLocation.getLatitude()) +  ", " + Double.toString(myLocation.getLongitude());
        Log.i("location", "" + message);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}
