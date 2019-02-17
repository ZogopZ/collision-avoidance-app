package com.example.softwaredevelopment2018;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.*;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;
import java.io.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity
{

    private MqttAndroidClient client;
    TextView subText;
    private static final int REQUEST_FINE_LOCATION = 200;
    private static final int REQUEST_PHONE_STATE = 201;
    private Timer myTimer = new Timer();
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
        client = new MqttAndroidClient(context, mqttHost, clientId);

        Tools.getMacAddress();
        Tools.getRingtone(context);
//        Tools.checkConnection(context);

        Button subscribeButton = findViewById(R.id.subscribeButton);
        Button macButton = findViewById(R.id.macButton);
        Button gpsButton = findViewById(R.id.gpsButton);
        Button fileButton = findViewById(R.id.fileButton);
        subText = findViewById(R.id.subText);

        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
        {
            Log.w("***PERMISSIONS***", "ACCESS_FINE_LOCATION permission needed. Will try to grant it.");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        }

        MqttConnectOptions options = new MqttConnectOptions();

        try
        {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener()
            {
                @Override
                public void onSuccess(IMqttToken asyncActionToken)
                {
                    Toast.makeText(MainActivity.this, "connected", Toast.LENGTH_LONG).show();
                    Log.i("***INFO***", "Android client connected to MQTT broker");
                    Tools.myRingtone.play();
                    /***Timer for random file selection between regural intervals.***/
                    myTimer.schedule(new MyTimerTask(), 0,2000);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                {
                    Toast.makeText(MainActivity.this, "connection failed", Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (MqttException e) { e.printStackTrace(); }

        client.setCallback(new MqttCallback()
        {
            @Override
            public void connectionLost(Throwable cause)
            {
                Log.d("mqtt", "connection lost");
                myTimer.cancel();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception
            {
                if (!message.toString().startsWith("FILE"))
                    subText.setText(new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token)
            {

            }
        });


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
                    Log.w("***PERMISSIONS***", "ACCESS_FINE_LOCATION permission needed. Will try to grant it.");
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
                }
                locationTrack = new LocationTrack(MainActivity.this);
                if (locationTrack.canGetLocation())
                {
                    Location location = LocationTrack.myLocation;
                    String latitude = Double.toString(location.getLatitude());
                    String longitude = Double.toString(location.getLongitude());
                    Toast.makeText(getApplicationContext(), "Latitude:" + latitude + "\nLongitude:" + longitude, Toast.LENGTH_SHORT).show();
                    Log.i("***LOCATION_INFO***", "Latitude: " + latitude + " Longitude: " + longitude);
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

        /*******************************
         *     File Sender Button      *
         ******************************/
        fileButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE ) != PackageManager.PERMISSION_GRANTED )
                {
                    Log.w("***PERMISSIONS***", "READ_PHONE_STATE permission needed. Will try to grant it.");
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_STATE);
                }
                while (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE ) != PackageManager.PERMISSION_GRANTED ) {}
                TelephonyManager telephoneMngr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                androidID = telephoneMngr.getImei();
                Log.i("***INFO***", "Current android's ID is: " + androidID);
                try
                {
                    String[] list;
                    AssetManager assetManager = getAssets(); //Get assets folder.
                    String[] files = assetManager.list(""); //List all files in assets folder.
                    client.publish(Tools.topic, new MqttMessage(androidID.getBytes()));
                }
                catch (UnsupportedEncodingException | MqttException e) { e.printStackTrace(); }
                catch (IOException e) { e.printStackTrace(); }
            }
        });
    }

    private void sendRandomFiles()
    {
        try
        {
            String[] directoryContents = getAssets().list("Training_Set"); //List all files of asset Training_Set.
            Random random = new Random(); //Create random number generator.
            int randomFileNumber = random.nextInt(directoryContents.length); //Choose a random number 0-35 (according to number of files in Training_Set directory.
            File randomFile = new File(directoryContents[randomFileNumber]);
            InputStream inputStream = getAssets().open("Training_Set" + randomFile.getAbsolutePath());
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write(("FILE:" + randomFile.getName() + "\n").getBytes()); //Add file's name to byte array.
            output.write(("ANDROID_ID:" + Tools.topic + "\n").getBytes()); //Add android's mac address to byte array.
            while ((bytesRead = inputStream.read(buffer)) != -1)
                output.write(buffer, 0, bytesRead); //Read all file's bytes to output variable.
            byte randomFileContents[] = output.toByteArray(); //Convert to byte array needed for Mqtt Message.
            Log.d("timer", "zotimeropoulos: " + Integer.toString(randomFileNumber) + ", " + randomFile.getName());
            client.publish(Tools.topic, new MqttMessage(randomFileContents)); //Publish file's name and contents to topic.
        }
        catch (MqttException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }
    }

    private class MyTimerTask extends TimerTask
    {

        @Override
        public void run()
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    sendRandomFiles();
                }
            });
        }
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