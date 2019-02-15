package mqttApp;

import com.example.softwaredevelopment2018.Tools;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class mqttApp implements MqttCallback
{

    MqttClient myClient;
    MqttConnectOptions connOpt;

    static final String BROKER_URL = "tcp://192.168.1.5:8181";

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
    public void deliveryComplete(MqttDeliveryToken token)
    {
        //System.out.println("Pub complete" + new String(token.getMessage().getPayload()));
    }


    /*****************************************************************************
     * This callback is invoked when a message is received on a subscribed topic.
     ****************************************************************************/
    public void messageArrived(MqttTopic topic, MqttMessage message) throws Exception {
        System.out.println("-------------------------------------------------");
        System.out.println("| Topic:" + topic.getName());
        System.out.println("| Message: " + new String(message.getPayload()));
        System.out.println("-------------------------------------------------");
    }


    /*******
     * MAIN
     *******/
    public static void main(String[] args) {
        mqttApp smc = new mqttApp();
        smc.runClient();
    }


    /****************************************************************
     * Create a MQTT client, connect to broker, pub/sub, disconnect.
     ****************************************************************/
    public void runClient()
    {
        connOpt = new MqttConnectOptions();
        connOpt.setCleanSession(true);
        connOpt.setKeepAliveInterval(30);

        // Connect to Broker
        try
        {
            myClient = new MqttClient(BROKER_URL, "Client_1");
            myClient.setCallback(this);
            myClient.connect(connOpt);
        }
        catch (MqttException e) { e.printStackTrace(); System.exit(-1); }

        System.out.println("******Connected to " + BROKER_URL);
        MqttTopic topic = myClient.getTopic(Tools.topic);

        // subscribe to topic if subscriber
        if (subscriber)
        {
            try
            {
                myClient.subscribe(Tools.topic, 1);
                System.out.println("******Subscribed to topic " + Tools.topic);
            }
            catch (Exception e) { e.printStackTrace(); }
        }

        // publish messages if publisher
        if (publisher)
        {
            for (int i=1; i<=4; i++)
            {
                String pubMsg = "{\"pubmsg\":" + i + "}";
                int pubQoS = 0;
                MqttMessage message = new MqttMessage(pubMsg.getBytes());
                message.setQos(pubQoS);
                message.setRetained(false);

                // Publish the message
                System.out.println("Publishing to topic \"" + Tools.topic + "\" qos " + pubQoS);
                MqttDeliveryToken token = null;
                try {
                    // publish message to broker
                    token = topic.publish(message);
                    // Wait until the message has been delivered to the broker
                    token.waitForCompletion();
                    Thread.sleep(100);
                }
                catch (Exception e) { e.printStackTrace(); }
            }
        }

        // disconnect
        try
        {
            // wait to ensure subscribed messages are delivered
            if (subscriber)
            {
                Thread.sleep(5000);
            }
            myClient.disconnect();
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
        // TODO Auto-generated method stub

    }
}
