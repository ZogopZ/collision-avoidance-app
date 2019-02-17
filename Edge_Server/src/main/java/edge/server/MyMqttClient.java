package edge.server;

import org.eclipse.paho.client.mqttv3.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Boolean.TRUE;

public class MyMqttClient implements MqttCallback
{

    static MqttClient client;
    MqttConnectOptions connectOptions;
    static final String BROKER_URL = "tcp://localhost:8181"; //Replace localhost with inet address if mqtt broker runs on another pc.
    //The following two flags control whether this example is a publisher, a subscriber or both.
    static final Boolean subscriber = true;
    static final Boolean publisher = true;

    static void main()
    {
        MyMqttClient client = new MyMqttClient();
        client.runClient();
    }

    /***************************************************
     * Create MQTT client, connect to broker, pub/sub. *
     ***************************************************/
    private void runClient()
    {
        connectOptions = new MqttConnectOptions();
        connectOptions.setCleanSession(true);
        connectOptions.setKeepAliveInterval(30);

        try //Connect to Broker.
        {
            client = new MqttClient(BROKER_URL, "edgeServerClient");
            client.setCallback(this);
            client.connect(connectOptions);
        }
        catch (MqttException e) { e.printStackTrace(); System.exit(-1); }
        System.out.println(" -connected to " + BROKER_URL);
        MqttTopic topic = client.getTopic("4C:49:E3:13:88:24");

        // subscribe to topic if subscriber
        if (subscriber)
        {
            try
            {
                client.subscribe("4C:49:E3:13:88:24", 1);
            }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    /*****************************************************************************
     * This callback is invoked when a message is received on a subscribed topic.
     ****************************************************************************/
    @Override
    public void messageArrived(String topic, MqttMessage message)
    {
        String newMessage = message.toString();
        File newFile;
        if (newMessage.startsWith("FILE")) //New mqtt message contains data for entropy calculation.
        {
            newFile = Tools.storeFile(newMessage);
            Tools.extractData(newFile);
        }
        else //Non file mqtt message.
            System.out.println("" + message);
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
    {
        System.out.println(" -connection lost!");
        System.out.println(" -retrying connection to broker");
        MyMqttClient.main();
        //Code to reconnect to the broker would go here if desired
    }

}