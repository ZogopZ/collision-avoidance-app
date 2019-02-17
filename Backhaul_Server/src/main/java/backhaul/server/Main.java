package backhaul.server;

public class Main {

    public static void main(String[] args)
    {
        try
        {
            System.out.println("|Data Extraction|");
            DataExtraction.extract();
            System.out.println("|Websocket Server Initialization|");
            BackhaulSocket.connect();
            System.out.println("|Database Connection Initialization|");
            DatabaseConnection.connect();
        }
        catch (Throwable t) { t.printStackTrace(); }
    }

}

