import java.io.*;
import java.net.Socket;

public class Main
{

    public static void main(String[] args)
    {
        try
        {
            Socket bhaul_socket;
            InputStream inSocket;
            OutputStream outFile;
            String host = "127.0.0.1";                     //This is the localhost. Must be changed to Backhaul server's ip.
            bhaul_socket = new Socket(host, 4444);    //Connect to Backhaul server's port 4444.
            inSocket = bhaul_socket.getInputStream();      //Store socket's data in inSocket.
            File training_set = new File("training_set.csv");
            outFile = new FileOutputStream(training_set);  //Use outFile stream to write data to created file.

            byte[] bytes = new byte[16*1024];
            int count;
            while ((count = inSocket.read(bytes)) > 0)     //Read data from Backhaul server and write it to file.
            {
                outFile.write(bytes, 0, count);
            }
            bhaul_socket.close();                          //Close connection with Backhaul server.
        }
        catch (Exception ex) {ex.printStackTrace();}
    }
}




