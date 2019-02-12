package backhaul.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;


public class BackhaulSocket
{
    public static int serverStop; //Flag for websocket server shutdown function.

    public static void connect() throws Exception
    {
        System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
        System.setProperty("org.eclipse.jetty.LEVEL", "OFF"); //Suppress logging output of jetty.

        Server server = new Server(8080);
        WebSocketHandler wsHandler = new WebSocketHandler()
        {
            @Override
            public void configure(WebSocketServletFactory factory)
            {
                factory.register(BackhaulSocketServer.class);
            }
        };
        server.setHandler(wsHandler);
        server.start(); //Websocket server start.
        System.out.println(" -waiting for edge server...");
        while (serverStop == 0) //Wait for websocket server shutdown.
        {
            Thread.sleep(1000);
        }
        Thread.sleep(4000);
        System.out.println(" -websocket server will now shut down");
        server.stop(); //Websocket server shutdown.
    }

}