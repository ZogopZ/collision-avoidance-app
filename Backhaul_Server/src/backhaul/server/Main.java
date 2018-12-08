package backhaul.server;

public class Main {

    public static void main(String[] args)
    {
        try
        {
            System.out.println("|Data Extraction|");
            DataExtraction.extract();
            BackhaulSocket.connect();
        }
        catch (Throwable t) { t.printStackTrace(); }
    }

}

