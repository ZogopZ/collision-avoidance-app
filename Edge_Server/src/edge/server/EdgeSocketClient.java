package edge.server;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;


public class EdgeSocketClient
{

    public static void connect()
    {
        String destUri = "ws://localhost:8080";

        System.out.println("|Connecting to Backhaul server|");
        WebSocketClient client = new WebSocketClient();
        EdgeSocket socket = new EdgeSocket();
        try //Connection with websocket server.
        {
            client.start();
            URI echoUri = new URI(destUri);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(socket, echoUri, request);

            socket.awaitClose(5, TimeUnit.SECONDS); //Wait for closed socket connection.
            Thread.sleep(3000);
        }
        catch (Throwable t) { t.printStackTrace(); }
        finally
        {
            try
            {
                client.stop();
                System.out.println(" -connection closed");
            }
            catch (Exception e) { e.printStackTrace(); }
        }
    }
}