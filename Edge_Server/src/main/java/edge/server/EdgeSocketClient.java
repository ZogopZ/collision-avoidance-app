package edge.server;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;


public class EdgeSocketClient
{
    static WebSocketClient client = new WebSocketClient();

    public static void connect()
    {
        String destUri = "ws://localhost:8080";

        System.out.println(" -connecting to backhaul server");
        EdgeSocket socket = new EdgeSocket();
        try //Connection with websocket server.
        {
            client.start(); //Start the websocket client.
            URI echoUri = new URI(destUri);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(socket, echoUri, request); //Connect to websocket server.
            socket.awaitClose(5, TimeUnit.SECONDS); //Wait for closed socket connection.
            Thread.sleep(3000);
        }
        catch (Throwable t) { t.printStackTrace(); }
    }

    public static void disconnect()
    {
        try
        {
            System.out.println(" -disconnecting from backhaul server");
            sendMessage("disconnecting");
            EdgeSocket.session.close(StatusCode.NORMAL, "|Edge Server| -> I'm done"); //Send close sequence with message to server
            client.stop();
        }
        catch (InterruptedException e) { e.printStackTrace(); }
        catch (Exception e) { e.printStackTrace(); }

    }

    public static void sendMessage(String message)
    {
        try
        {
            Future<Void> future;
            future = EdgeSocket.session.getRemote().sendStringByFuture(message); //Send message to backhaul server.
            future.get(2, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) { e.printStackTrace(); }
        catch (ExecutionException e) { e.printStackTrace(); }
        catch (TimeoutException e) { e.printStackTrace(); }
    }
}