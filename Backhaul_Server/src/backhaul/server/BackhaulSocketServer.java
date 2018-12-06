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
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
    }

    @OnWebSocketError
    public void onError(Throwable t)
    {
        System.out.println("Error: " + t.getMessage());
    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception
    {
        System.out.println("Connect: " + session.getRemoteAddress().getAddress());
        File file = new File("training_set.csv");
        String contents = new String(Files.readAllBytes(Paths.get(file.getName())));
        session.getRemote().sendString(contents);
    }


    @OnWebSocketMessage
    public void onMessage(String message)
    {
        System.out.println("Message: " + message);
    }
}