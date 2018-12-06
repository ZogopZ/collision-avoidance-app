package backhaul.server;


public class Main {

    public static void main(String[] args)
    {
        try
        {
            BackhaulSocket.connect();
        }
        catch (Throwable t) { t.printStackTrace(); }

    }
}
