package edge.server;

import java.io.File;

public class Main
{
    public static void main(String[] args)
    {
        EdgeSocketClient.connect(); //Create a websocket client and connect to websocket server.
        File file = new File("training_set.csv");
        TrainingSet.localClassify(file); //Locally classify downloaded file.
    }
}
