package com.example.softwaredevelopment2018;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class Tools
{
    public static String topic;
    static Ringtone myRingtone;

    static void getMacAddress() //Get device's mac address (https://stackoverflow.com/questions/47607679/how-can-i-get-mac-address-android-7-0)
    {
        try
        {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all)
            {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null)
                {
                    return;
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes)
                {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:",b));
                }
                if (res1.length() > 0)
                {
                    res1.deleteCharAt(res1.length() - 1);
                }
                topic = res1.toString();
            }
        }
        catch (Exception ex) { ex.printStackTrace(); }
    }

    static void getRingtone(Context context)
    {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        myRingtone = RingtoneManager.getRingtone(context, uri);
    }

//    static void getAndroidID(Context context)
//    {
//        try
//        {
//            final TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            final String tmDevice, tmSerial, androidId;
//            tmDevice = "" + tManager.getDeviceId();
//            tmSerial = "" + tManager.getSimSerialNumber();
//            androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
//
//            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
//            String deviceId = deviceUuid.toString();
//            System.out.println("***********" + deviceId);
//        }
//        catch (SecurityException e) { System.out.println("-- " + e.getLocalizedMessage()); }
//    }

    static void subscribe(final MqttAndroidClient client)
    {
        try
        {
            int qos = 1;
            IMqttToken subToken = client.subscribe(Tools.topic, qos);
            subToken.setActionCallback(new IMqttActionListener()
            {
                @Override
                public void onSuccess(IMqttToken asyncActionToken)
                {
                    // The message was published
//                    Tools.myRingtone.play();
                    try
                    {
                        client.publish(Tools.topic, new MqttMessage("ANDROID_ID subscribed".getBytes()));
                        //Publish mac address to topic.
                    }
                    catch (MqttPersistenceException e)
                    {
                        e.printStackTrace();
                    }
                    catch (MqttException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception)
                {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        }
        catch (MqttException e) { e.printStackTrace(); }
    }

//    static void checkConnection(Context context)
//    { //periodical check of internet connection source:
//      //https://stackoverflow.com/questions/10350449/how-to-check-the-internet-connection-periodically-in-whole-application
//        BroadcastReceiver mConnReceiver = new BroadcastReceiver()
//        {
//            public void onReceive(Context context, Intent intent)
//            {
//                boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
//                String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
//                boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
//
////                NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
//                NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
//
//                if (currentNetworkInfo.isConnected())
//                {
//
//                    Toast.makeText(context.getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
//                }
//                else
//                {
//                    Toast.makeText(context.getApplicationContext(), "Not Connected", Toast.LENGTH_LONG).show();
//                }
//            }
//        };
//    }

    public static Activity getActivity(Context context)
    {
        if (context == null)
        {
            return null;
        }
        else if (context instanceof ContextWrapper)
        {
            if (context instanceof Activity)
            {
                return (Activity) context;
            }
            else
            {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }
        return null;
    }



}
