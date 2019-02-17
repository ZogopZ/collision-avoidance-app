package edge.server;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        System.out.println("|Initializing|");
        clearPreviousData();
//        System.out.println("|Websocket Client Initialization|");
//        EdgeSocketClient.connect(); //Create a websocket client and connect to websocket server.
//        System.out.println("|File Classification|");
//        File file = new File("training_set.csv");
//        Tools.localClassify(file); //Locally classify downloaded file.
        System.out.println("|Mqtt Broker Initialization|");
        startBroker();
        System.out.println("|Edge Server Mqtt Client Initialization|");
        MyMqttClient.main();
    }

    private static void startBroker()

    {
        try
        {
            ProcessBuilder mosquittoBuilder = new ProcessBuilder("mosquitto", "-d", "-p", "8181");
            mosquittoBuilder.start(); //Start mosquitto in the background using port 8181.
            System.out.println(" -mosquitto broker started listening on port 8181");
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    private static void clearPreviousData()
    {
        try
        {
            File dataDirectory = new File("data");
            if (dataDirectory.exists())
                FileUtils.deleteDirectory(new File("data"));
            System.out.println(" -creating data directory");
            dataDirectory.mkdir();
            ProcessBuilder portKiller = new ProcessBuilder("fuser", "-n", "tcp", "-k", "8181");
            System.out.println(" -killing any process using port 8181");
            portKiller.start();

            Thread.sleep(2000);
        }
        catch (IOException e) { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }
}





