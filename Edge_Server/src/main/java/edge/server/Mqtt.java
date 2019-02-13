package edge.server;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;

public class Mqtt
{
    public static void startBroker()
    {
        try
        {
            ProcessBuilder mosquittoBuilder = new ProcessBuilder("mosquitto", "-d", "-p", "8181");
            mosquittoBuilder.start(); //Start mosquitto in the background using port 8181.
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public static void startClient()
    {
        String broker = "tcp://localhost:8181";
        String edgeClientId = "EdgeServer";
        try
        {
            MqttClient edgeClient = new MqttClient(broker, edgeClientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println(" -connecting to broker -> " + broker);
            edgeClient.connect(connOpts);
            System.out.println(" -connect ok");
        }
        catch (MqttException e) { e.printStackTrace(); }
    }
}
