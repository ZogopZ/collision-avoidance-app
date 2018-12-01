package edge_server;

import java.io.File;
import java.nio.channels.SocketChannel;

public class Main
{

    public static void main(String[] args)
    {

        try
        {
            File trainingSet;

            System.out.println("|Connecting to Backhaul server...|");
            SocketChannel backhaulSocket = BackhaulConnection.connect(); //Connect to backhaul server.
            System.out.println("|Download training set from Backhaul server...|");
            trainingSet = BackhaulConnection.download(backhaulSocket); //Download training set from backhaul server.
            System.out.println("|Storing data from training set...|");
            TrainingSet.localClassify(trainingSet); //Read training set file and store data locally.
        }
        catch (Exception ex) {ex.printStackTrace();}
    }
}




