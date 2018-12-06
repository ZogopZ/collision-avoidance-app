package backhaul.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class BackhaulSocket {

    public static void connect() throws Exception
    {
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
        server.start();
        server.join();
    }
}