package edge.server;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.Boolean.TRUE;

public class Mqtt
{
    public static void startBroker()
    {
        try
        {
            ProcessBuilder mosquittoBuilder = new ProcessBuilder("mosquitto", "-d" , "-p", "8181");
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

    public static void subscribe() throws IOException
    {
        ProcessBuilder builder = new ProcessBuilder("mosquitto_sub", "-h", "localhost",
                "-p", "8181", "-t", "4C:49:E3:13:88:24"); //Edge server subscribes to specific device's topic.
        builder.redirectErrorStream(true);
        final Process process = builder.start();
        Mqtt.watch(process);
        while (TRUE) ;
    }

    public static void watch(final Process process)
    {
        new Thread(() -> //A new thread is created to watch over the topic.
        {
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            try
            {
                while ((line = input.readLine()) != null)
                {
                    System.out.println(line);
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }).start();
    }
}
