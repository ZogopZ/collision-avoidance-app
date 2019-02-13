package edge.server;


import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.Boolean.TRUE;

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
        Mqtt.startBroker();
        System.out.println("|Edge Server Mqtt Client Initialization|");
        Mqtt.startClient();
        ProcessBuilder builder = new ProcessBuilder("mosquitto_sub", "-h", "localhost", "-p", "8181", "-t", "T.d0:50:99:0e:ca:58");
        builder.redirectErrorStream(true);
        final Process process = builder.start();
        watch(process);
        while (TRUE) ;

    }

    private static void watch(final Process process)
    {
        new Thread(() ->
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





