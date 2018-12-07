package backhaul.server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        System.out.println("-edge server just exited");
    }

    @OnWebSocketError
    public void onError(Throwable t)
    {
        System.out.println("Error: " + t.getMessage());
    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws InterruptedException {
        System.out.println("-edge server is now connected");
        Thread.sleep(500);
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, InterruptedException {
        System.out.println("-incoming message ---> " + message);
        Thread.sleep(1000);
        System.out.println("-uploading training set to edge server");
        File file = new File("training_set.csv");
        String contents = new String(Files.readAllBytes(Paths.get(file.getName())));
        session.getRemote().sendString(contents);
        Thread.sleep(2500);
        System.out.println("-file was uploaded successfully");
        Thread.sleep(1000);
        BackhaulSocket.serverStop = 1;
    }
}