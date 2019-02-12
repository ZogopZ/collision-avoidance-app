package backhaul.server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class BackhaulSocketServer
{

    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        System.out.println(" -edge server just exited");
    }

    @OnWebSocketError
    public void onError(Throwable t)
    {
        System.out.println("Error: " + t.getMessage());
    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws InterruptedException
    {
        System.out.println(" -edge server is now connected");
        Thread.sleep(500);
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, InterruptedException
    {
            if (message.equals("request")) //Edge server requests a file.
            {
                BackhaulSocket.sendMessage(session, "uploading");
                System.out.println(" -edge server requests the training_set.csv file");
                Thread.sleep(1000);
                File file = new File("training_set.csv"); //Get the generated training_set.csv file.
                System.out.printf(" -uploading %s file to edge server\n", file.getName());
                String contents = new String(Files.readAllBytes(Paths.get(file.getName())));
                //Create a string with the training set data to be uploaded.
                session.getRemote().sendString(contents); //Upload training_set.csv file to edge server.
                Thread.sleep(2500);
                System.out.printf(" -%s file was uploaded successfully\n", file.getName());
            }
            else if (message.startsWith("logs")) //Edge server sends logging details.
            {
                System.out.println(""+message);
            }
            else if (message.equals("disconnecting")) //Edge server is shutting down.
            {
                BackhaulSocket.serverStop = 1; //Websocket server flag for shutdown function.
            }
    }
}