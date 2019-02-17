package com.example.softwaredevelopment2018;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;


public class MainActivity extends AppCompatActivity
{

    final Handler mHandler = new Handler();
    private Thread mUiThread;
    private MqttAndroidClient client;
    TextView subText;
    private static final int REQUEST_FINE_LOCATION = 200;
    private static final int REQUEST_PHONE_STATE = 201;
    LocationTrack locationTrack;
    public static String androidID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this.getApplicationContext();
        String clientId = "user1";
        String mqttHost = "tcp://192.168.1.6:8181";

        Tools.getMacAddress();
        Tools.getRingtone(context);
//        Tools.checkConnection(context);

        Button subscribeButton = findViewById(R.id.subscribeButton);
        Button macButton = findViewById(R.id.macButton);
        Button gpsButton = findViewById(R.id.gpsButton);
        subText = findViewById(R.id.subText);

        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
        { //Application requests for GPS location permissions.
            Log.w("permissions", "ACCESS_FINE_LOCATION permission needed. Will try to grant it.");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        }
        new AndroidMqttClient(context);

        /*******************************
         *      Subscribe Button       *
         ******************************/
        subscribeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Tools.subscribe(client);
                try
                {
                    client.subscribe(Tools.topic, 1);
                }
                catch (MqttException e) { e.printStackTrace(); }
            }
        });

        /*******************************
         *     Mac Address Button      *
         ******************************/
        macButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    MqttMessage message = new MqttMessage(Tools.topic.getBytes());
                    client.publish(Tools.topic, message);
                }
                catch (MqttException e) { e.printStackTrace(); }
            }
        });

        /*******************************
         *     GPS Location Button     *
         ******************************/
        gpsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
                {
                    Log.i("Permissions", "ACCESS_FINE_LOCATION permission needed. Will try to grant it.");
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
                }
                locationTrack = new LocationTrack(MainActivity.this);
                if (locationTrack.canGetLocation())
                {
                    Location location = LocationTrack.myLocation;
                    String latitude = Double.toString(location.getLatitude());
                    String longitude = Double.toString(location.getLongitude());
                    Toast.makeText(getApplicationContext(), "Latitude:" + latitude + "\nLongitude:" + longitude, Toast.LENGTH_SHORT).show();
                    Log.i("Location", "Latitude: " + latitude + " Longitude: " + longitude);
                    try
                    {
                        client.publish(Tools.topic, new MqttMessage(("" + latitude + ", " + longitude).getBytes()));
                    }
                    catch (MqttException e ) { e.printStackTrace(); }
                }
                else
                {
//                    locationTrack.showSettingsAlert();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == REQUEST_FINE_LOCATION)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))
            { //Show an explanation to the user *asynchronously*
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This permission is important for the application.")
                        .setTitle("Important permission required");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
                    }
                });
            }
        }
        else if (requestCode == REQUEST_PHONE_STATE)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_PHONE_STATE))
            { //Show an explanation to the user *asynchronously*
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This permission is important for the application.")
                        .setTitle("Important permission required");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_STATE);
                    }
                });
            }
        }
    }

    //code for menu creation and use
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.commonmenus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.setting:
                Toast.makeText(MainActivity.this, "Settings is clicked", Toast.LENGTH_LONG).show();
                Intent i = new Intent(this, SendData.class);
                startActivity(i);
                break;
            case R.id.exit:
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure you want to Exit?");
                builder.setCancelable(true);
                builder.setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int i)
                    {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int i)
                    {
                        finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure you want to Exit?");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int i)
            {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int i)
            {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}