package edge.server;

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
//        System.out.println("|Mqtt Broker Initialization|");
        Mqtt.startBroker();
        System.out.println("|Edge Server Mqtt Client Initialization|");
        Mqtt.startClient();
        Mqtt.subscribe();

    }


}





