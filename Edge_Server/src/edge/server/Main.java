package edge.server;

import java.io.File;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("|Websocket Client Initialization|");
        EdgeSocketClient.connect(); //Create a websocket client and connect to websocket server.
        System.out.println("|File Classification|");
        File file = new File("training_set.csv");
        TrainingSet.localClassify(file); //Locally classify downloaded file.
    }
}
