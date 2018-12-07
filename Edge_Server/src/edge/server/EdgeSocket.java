package edge.server;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;


@WebSocket(maxTextMessageSize = 64 * 1024) //Set maximum message size.
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
        this.closeLatch.countDown(); //Trigger latch.
    }

    @OnWebSocketConnect //On connection with websocket server.
    public void onConnect(Session session)
    {
        System.out.println(" -connect ok!");
        this.session = session;
        try //Send a simple hello message.
        {
            Future<Void> fut;
            fut = session.getRemote().sendStringByFuture("(Edge Server says) Hello Backhaul Server");
            fut.get(2,TimeUnit.SECONDS); //Wait for send to complete.
        }
        catch (Throwable t) { t.printStackTrace(); }
    }

    @OnWebSocketMessage //On message from websocket server.
    public void onMessage(String message) throws IOException
    {
        System.out.println(" -downloading data");
        File file = new File("training_set.csv"); //Create an empty training_set.csv file.
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(message); //Write the received message (backhaul's training set file) to the empty file.
        fileWriter.close();
        System.out.println(" -writing data to training_set.csv completed");
        session.close(StatusCode.NORMAL,"|Edge Server| -> I'm done"); //Send close sequence with message to server.
    }

}