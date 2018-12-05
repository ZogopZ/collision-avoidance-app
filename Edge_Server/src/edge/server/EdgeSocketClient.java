package edge.server;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;


public class EdgeSocketClient
{
    public static void main(String[] args)
    {
        String destUri = "ws://localhost:8080";
        if (args.length > 0)
        {
            destUri = args[0];
        }

        System.out.println("|Connecting to Backhaul server|");
        WebSocketClient client = new WebSocketClient();
        EdgeSocket socket = new EdgeSocket();
        try
        {
            client.start();
            URI echoUri = new URI(destUri);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(socket, echoUri, request);

            // wait for closed socket connection.
            socket.awaitClose(5, TimeUnit.SECONDS);
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