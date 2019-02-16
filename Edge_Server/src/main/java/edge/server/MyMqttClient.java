package edge.server;

import org.eclipse.paho.client.mqttv3.*;

import java.io.BufferedReader;
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

    /***********************************************************
     * This callback is invoked upon losing the MQTT connection.
     ***********************************************************/
    @Override
    public void connectionLost(Throwable t)
    {
        System.out.println("Connection lost!");
        //Code to reconnect to the broker would go here if desired
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

    /*****************************************************************************
     * This callback is invoked when a message is received on a subscribed topic.
     ****************************************************************************/
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception
    {
        System.out.println("" + message);
    }

    public static void main()
    {
        MyMqttClient client = new MyMqttClient();
        client.runClient();
    }


    /****************************************************************
     * Create a MQTT client, connect to broker, pub/sub, disconnect.
     ****************************************************************/
    public void runClient()
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
        // publish messages if publisher
        if (publisher)
        {
            try
            {
                MqttDeliveryToken token = null;
                token = topic.publish(new MqttMessage("hello there".getBytes()));
                token.waitForCompletion();
            }
            catch (MqttPersistenceException e) { e.printStackTrace(); }
            catch (MqttException e) { e.printStackTrace(); }
        }
    }

}







































//    public static void startClient()
//    {
//        String broker = "tcp://localhost:8181";
//        String edgeClientId = "EdgeServer";
//        try
//        {
//            MqttClient edgeClient = new MqttClient(broker, edgeClientId);
//            MqttConnectOptions connOpts = new MqttConnectOptions();
//            connOpts.setCleanSession(true);
//            System.out.println(" -connecting to broker -> " + broker);
//            edgeClient.connect(connOpts);
//            System.out.println(" -connect ok");
//        }
//        catch (MqttException e) { e.printStackTrace(); }
//    }
//
//    public static void subscribe() throws IOException
//    {
//        ProcessBuilder builder = new ProcessBuilder("mosquitto_sub", "-h", "localhost",
//                "-p", "8181", "-t", "4C:49:E3:13:88:24"); //Edge server subscribes to specific device's topic.
//        builder.redirectErrorStream(true);
//        final Process process = builder.start();
//        Mqtt.watch(process);
//        while (TRUE) ;
//    }
//
//    public static void watch(final Process process)
//    {
//        new Thread(() -> //A new thread is created to watch over the topic.
//        {
//            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line = null;
//            try
//            {
//                while ((line = input.readLine()) != null)
//                {
//                    System.out.println(line);
//                }
//            }
//            catch (IOException e) { e.printStackTrace(); }
//        }).start();
//    }
//}
