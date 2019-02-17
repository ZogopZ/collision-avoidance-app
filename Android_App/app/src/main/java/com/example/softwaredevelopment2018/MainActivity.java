package com.example.softwaredevelopment2018;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
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
import com.google.android.gms.common.api.GoogleApiClient;
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
    public static String androidID;
    EasyLocationProvider easyLocationProvider;

    /**********************************************************/

    /**********************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this.getApplicationContext();

        Tools.getMacAddress();
        Tools.getRingtone(context);
//        Tools.checkConnection(context);

        Button subscribeButton = findViewById(R.id.subscribeButton);
        Button macButton = findViewById(R.id.macButton);
        Button gpsButton = findViewById(R.id.gpsButton);
        subText = findViewById(R.id.subText);

        new AndroidMqttClient(context);
        this.client = AndroidMqttClient.getAndroidMqttClient();

        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
        { //Application requests for GPS location permissions.
            Log.w("permissions", "ACCESS_FINE_LOCATION permission needed. Will try to grant it.");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        }


        /***************************************************************************************************************************/
        /*Source: https://stackoverflow.com/questions/48908337/get-current-location-with-fused-location-provider/53348535#53348535 */
        /***************************************************************************************************************************/
        easyLocationProvider = new EasyLocationProvider.Builder(this)
                .setInterval(5000)
                .setFastestInterval(2000)
                //.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setListener(new EasyLocationProvider.EasyLocationCallback()
                {
                    @Override
                    public void onGoogleAPIClient(GoogleApiClient googleApiClient, String message)
                    {
                        try
                        {
                            Log.i("Location", "onGoogleAPIClient: " + message);
                            Toast.makeText(getApplicationContext(), "onGoogleAPIClient: " + message, Toast.LENGTH_SHORT).show();
                            client.publish(Tools.topic, new MqttMessage(("onGoogleAPIClient: " + message).getBytes()));
                        }
                        catch (MqttPersistenceException e) { e.printStackTrace(); }
                        catch (MqttException e) { e.printStackTrace(); }
                    }

                    @Override
                    public void onLocationUpdated(double latitude, double longitude)
                    {
                        try
                        {
                            Log.i("Location","onLocationUpdated:: " + "Latitude: " + latitude + " Longitude: " + longitude);
                            Toast.makeText(getApplicationContext(), "onLocationUpdated:: " + "Latitude: " + latitude + " Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                            client.publish(Tools.topic, new MqttMessage(("onLocationUpdated:: " + "Latitude: " + latitude + " Longitude: " + longitude).getBytes()));
                        }
                        catch (MqttPersistenceException e) { e.printStackTrace(); }
                        catch (MqttException e) { e.printStackTrace(); }
                    }

                    @Override
                    public void onLocationUpdateRemoved()
                    {
                        Log.i("Location","onLocationUpdateRemoved");
                    }
                }).build();
        getLifecycle().addObserver(easyLocationProvider);
        /*****************************************************************************************************************************/


        /*******************************
         *      Subscribe Button       *
         ******************************/
        subscribeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    if (client.isConnected())
                        client.unsubscribe(Tools.topic);
                    client.subscribe(Tools.topic, 1);
                    client.publish(Tools.topic, new MqttMessage(("Android: " + Tools.topic " subscribed.").getBytes());
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

    @Override
    protected void onDestroy() {
        easyLocationProvider.removeUpdates();
        getLifecycle().removeObserver(easyLocationProvider);
        super.onDestroy();
    }
}