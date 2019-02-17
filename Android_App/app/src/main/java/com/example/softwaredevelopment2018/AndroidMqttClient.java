package com.example.softwaredevelopment2018;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class AndroidMqttClient implements MqttCallback
{
    static MqttAndroidClient client;
    MqttConnectOptions connectOptions;
    static final String BROKER_URL = "tcp://192.168.1.6:8181"; //Replace according to inet address. (ifconfig).
    public Context context;
    private final Handler handler;
    private Timer myTimer = new Timer();

    public AndroidMqttClient(Context context)
    {
        this.context = context;
        handler = new Handler(context.getMainLooper());
        runClient(context);
    }
//    public void main(Context context)
//    {
//        handler = new Handler(context.getMainLooper());
//        AndroidMqttClient client = new AndroidMqttClient();
//        client.runClient(context);
//    }

    /*****************************************************
     * Create MQTT client, connect to broker, subscribe. *
     *****************************************************/
    private void runClient(final Context context)
    {
        connectOptions = new MqttConnectOptions();
        connectOptions.setCleanSession(true);
        connectOptions.setKeepAliveInterval(30);

        try //Connect to Broker.
        {
            client = new MqttAndroidClient(context, BROKER_URL, "androidClient");
            client.setCallback(this);
            IMqttToken token = client.connect(connectOptions);
            token.setActionCallback(new IMqttActionListener()
            {
                @Override
                public void onSuccess(IMqttToken asyncActionToken)
                {
                    try
                    {
                        Toast.makeText(context, "connected", Toast.LENGTH_LONG).show();
                        Log.i("Mqtt", "Android client connected to MQTT broker");
                        client.subscribe(Tools.topic, 1);
                        /***Timer for data sending, between regural intervals.***/
                        myTimer.scheduleAtFixedRate(new MyTimerTask(), 0, 2000);
                    }
                    catch (MqttSecurityException e) { e.printStackTrace(); }
                    catch (MqttException e) { e.printStackTrace(); }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                {
                    Toast.makeText(context, "connection failed", Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (MqttException e) { e.printStackTrace(); System.exit(-1); }
    }

    /******************************************************************************
     * This callback is invoked when a message is received on a subscribed topic. *
     ******************************************************************************/
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception
    {
        if (!message.toString().startsWith("FILE"))
        {
            String newMessage = new String(message.getPayload());
            Toast.makeText(context, "" + newMessage, Toast.LENGTH_SHORT).show();
        }
    }

    /*******************************************************************
     * This callback is invoked when a message published by this client.
     * is successfully received by the broker.
     * *****************************************************************/
    @Override
    public void deliveryComplete(IMqttDeliveryToken token)
    {
        //ToDo
    }

    /***********************************************************
     * This callback is invoked upon losing the MQTT connection.
     ***********************************************************/
    @Override
    public void connectionLost(Throwable t)
    { //Reconnect to broker in case of disconnection.
        Log.i("Mqtt", "Lost connection to broker");
        Log.i("Mqtt", "Attempting to reconnect to " + BROKER_URL);
        myTimer.cancel(); //Stop sending data, until android reconnects to broker.
        new AndroidMqttClient(context);
    }

    public class MyTimerTask extends TimerTask
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

    private void runOnUiThread(Runnable runnable)
    {
        handler.post(runnable);
    }

    private void sendRandomFiles()
    {
        try
        {
            String[] directoryContents = context.getAssets().list("Training_Set"); //List all files of asset Training_Set.
            Random random = new Random(); //Create random number generator.
            int randomFileNumber = random.nextInt(directoryContents.length); //Choose a random number 0-35 (according to number of files in Training_Set directory.
            File randomFile = new File(directoryContents[randomFileNumber]);
            InputStream inputStream = context.getAssets().open("Training_Set" + randomFile.getAbsolutePath());
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write(("FILE:" + randomFile.getName() + "\n").getBytes()); //Add file's name to byte array.
            output.write(("ANDROID_ID:" + Tools.topic + "\n").getBytes()); //Add android's mac address to byte array.
            while ((bytesRead = inputStream.read(buffer)) != -1)
                output.write(buffer, 0, bytesRead); //Read all file's bytes to output variable.
            byte randomFileContents[] = output.toByteArray(); //Convert to byte array needed for Mqtt Message.
            Log.d("timer", Integer.toString(randomFileNumber) + ", " + randomFile.getName());
            client.publish(Tools.topic, new MqttMessage(randomFileContents)); //Publish file's name and contents to topic.
        }
        catch (MqttException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }
    }
}


























//
//    MqttClient myClient;
//    MqttConnectOptions connOpt;
//
//    static final String BROKER_URL = "tcp://192.168.1.5:8181";
//
//    static final Boolean subscriber = true;
//    static final Boolean publisher = true;
//
//    /***********************************************************
//     * This callback is invoked upon losing the MQTT connection.
//     ***********************************************************/
//    @Override
//    public void connectionLost(Throwable t)
//    {
//        System.out.println("Connection lost!");
//        //Code to reconnect to the broker would go here if desired
//    }
//
//    /*******************************************************************
//     * This callback is invoked when a message published by this client.
//     * is successfully received by the broker.
//     * *****************************************************************/
//    public void deliveryComplete(MqttDeliveryToken token)
//    {
//        //System.out.println("Pub complete" + new String(token.getMessage().getPayload()));
//    }
//
//    /*****************************************************************************
//     * This callback is invoked when a message is received on a subscribed topic.
//     ****************************************************************************/
//    public void messageArrived(MqttTopic topic, MqttMessage message) throws Exception {
//        System.out.println("-------------------------------------------------");
//        System.out.println("| Topic:" + topic.getName());
//        System.out.println("| Message: " + new String(message.getPayload()));
//        System.out.println("-------------------------------------------------");
//    }
//
//    /***MAIN***/
//    public static void main(String[] args)
//    {
//        mqttApp smc = new mqttApp();
//        smc.runClient();
//    }
//
//
//    /****************************************************************
//     * Create a MQTT client, connect to broker, pub/sub, disconnect.
//     ****************************************************************/
//    public void runClient()
//    {
//        connOpt = new MqttConnectOptions();
//        connOpt.setCleanSession(true);
//        connOpt.setKeepAliveInterval(30);
//
//        // Connect to Broker
//        try
//        {
//            myClient = new MqttClient(BROKER_URL, "androidClient");
//            myClient.setCallback(this);
//            myClient.connect(connOpt);
//        }
//        catch (MqttException e) { e.printStackTrace(); System.exit(-1); }
//
//        Log.i("mqtt", "Connected to " + BROKER_URL);
//        MqttTopic topic = myClient.getTopic(Tools.topic);
//
//        // subscribe to topic if subscriber
//        if (subscriber)
//        {
//            try
//            {
//                myClient.subscribe(Tools.topic, 1);
//                Log.i("mqtt", "Subscribed to topic" + Tools.topic);
//            }
//            catch (Exception e) { e.printStackTrace(); }
//        }
//
//        // publish messages if publisher
//        if (publisher)
//        {
//            for (int i=1; i<=4; i++)
//            {
//                String pubMsg = "{\"pubmsg\":" + i + "}";
//                int pubQoS = 0;
//                MqttMessage message = new MqttMessage(pubMsg.getBytes());
//                message.setQos(pubQoS);
//                message.setRetained(false);
//
//                // Publish the message
//                System.out.println("Publishing to topic \"" + Tools.topic + "\" qos " + pubQoS);
//                MqttDeliveryToken token = null;
//                try {
//                    // publish message to broker
//                    token = topic.publish(message);
//                    // Wait until the message has been delivered to the broker
//                    token.waitForCompletion();
//                    Thread.sleep(100);
//                }
//                catch (Exception e) { e.printStackTrace(); }
//            }
//        }
//
//        // disconnect
//        try
//        {
//            // wait to ensure subscribed messages are delivered
//            if (subscriber)
//            {
//                Thread.sleep(5000);
//            }
//            myClient.disconnect();
//        }
//        catch (Exception e) { e.printStackTrace(); }
//    }
//
//    @Override
//    public void deliveryComplete(IMqttDeliveryToken arg0) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
//        // TODO Auto-generated method stub
//
//    }
//}
