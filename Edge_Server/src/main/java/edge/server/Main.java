package edge.server;

import org.eclipse.paho.client.mqttv3.MqttClient;

import java.io.File;
import java.io.IOException;

public class Main
{
    public static void main(String[] args) throws IOException
    {
//        System.out.println("|Websocket Client Initialization|");
//        EdgeSocketClient.connect(); //Create a websocket client and connect to websocket server.
//        System.out.println("|File Classification|");
//        File file = new File("training_set.csv");
//        TrainingSet.localClassify(file); //Locally classify downloaded file.
        System.out.println("|Mqtt Broker Initialization|");
        startBroker();
        System.out.println("|Edge Server Mqtt Client Initialization|");
        MyMqttClient.main();
//        Mqtt.startClient();
//        Mqtt.subscribe();

    }

    private static void startBroker()

    {
        try
        {
            ProcessBuilder mosquittoBuilder = new ProcessBuilder("mosquitto", "-d", "-p", "8181");
            mosquittoBuilder.start(); //Start mosquitto in the background using port 8181.
            System.out.println(" -mosquitto broker started listening on port 8181");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}





