package edge_server;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;


public class BackhaulConnection
{

    public static SocketChannel connect() throws IOException
    {
        InetSocketAddress hostAddress;
        SocketChannel backhaulSocket;

        hostAddress = new InetSocketAddress("localhost", 4444); //Change localhost to server's ip.
        backhaulSocket = SocketChannel.open(hostAddress); //Connect to Backhaul server.
        System.out.println(" -Connect ok!");
        return backhaulSocket;
    }

    public static File download(SocketChannel backhaulSocket) throws IOException
    {
        File trainingSet;
        ByteBuffer bBuffer;
        FileChannel filechannel;

        trainingSet = new File("Edge_Server/training_set.csv"); //Create a file to store data.
        filechannel = new FileOutputStream(trainingSet, false).getChannel();
        bBuffer = ByteBuffer.allocate(1024);
        System.out.println(" -downloading file");
        while (backhaulSocket.read(bBuffer) > 0) //Read data from socket to a buffer.
        {
            bBuffer.flip();
            filechannel.write(bBuffer); //Write data from buffer to file.
            bBuffer.clear();
        }
        filechannel.close();
        System.out.println(" -file transfer completed");
        backhaulSocket.close(); //Close connection with Backhaul server.
        System.out.println(" -closing connection to backhaul server");
        return trainingSet;
    }
}