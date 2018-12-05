package edge.server;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;


@WebSocket(maxTextMessageSize = 64 * 1024)
public class EdgeSocket
{
    private final CountDownLatch closeLatch;
    @SuppressWarnings("unused")
    private Session session;

    public EdgeSocket()
    {
        this.closeLatch = new CountDownLatch(1);
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException
    {
        return this.closeLatch.await(duration,unit);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        this.session = null;
        this.closeLatch.countDown(); // trigger latch
    }

    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        System.out.println(" -connect ok!");
        this.session = session;
        try
        {
            Future<Void> fut;
            fut = session.getRemote().sendStringByFuture("|Edge Server| -> Hello Backhaul Server");
            fut.get(2,TimeUnit.SECONDS); // wait for send to complete.
        }
        catch (Throwable t) { t.printStackTrace(); }
    }

    @OnWebSocketMessage
    public void onMessage(String message) throws IOException
    {
        System.out.printf(" -Backhaul Server says -> %s%n",message);
        session.close(StatusCode.NORMAL,"I'm done");
    }

}