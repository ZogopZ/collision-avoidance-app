package edge_server;

import java.io.*;
import java.net.Socket;

public class BackhaulConnection
{

    public static File connect()
    {
        File trainingSet = new File("Edge_Server/training_set.csv");
        try
        {
            Socket backhaulSocket;
            InputStream inSocket;
            OutputStream outFile;

            String host = "127.0.0.1";                       //This is the localhost. Must be changed to Backhaul server's ip.
            backhaulSocket = new Socket(host, 4444);    //Connect to Backhaul server's port 4444.
            inSocket = backhaulSocket.getInputStream();      //Store socket's data in inSocket.

            outFile = new FileOutputStream(trainingSet);     //Use outFile stream to write data to created file.
            byte[] bytes = new byte[16*1024];
            int count;
            System.out.println(" -downloading file");
            while ((count = inSocket.read(bytes)) > 0)       //Read data from Backhaul server and write it to file.
            {
                outFile.write(bytes, 0, count);
            }
            System.out.println(" -file transfer completed");
            backhaulSocket.close();                          //Close connection with Backhaul server.
            System.out.println(" -closing connection to backhaul server");
        }
        catch (Exception ex) {ex.printStackTrace();}
        return trainingSet;
    }
}
