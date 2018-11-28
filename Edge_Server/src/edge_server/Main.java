package edge_server;

import java.io.File;

public class Main
{

    public static void main(String[] args)
    {

        System.out.println("Connecting to Backhaul server...");
        File trainingSet = BackhaulConnection.connect();            //Download training set from backhaul server.
        System.out.println("Storing data from training set...");
        TrainingSet.localClassify(trainingSet);                     //Read training set file and store data locally.
    }
}




