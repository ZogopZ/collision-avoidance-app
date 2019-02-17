package backhaul.server;

class DatabaseElement
{

    public static String AndroidID;
    public static String TimeStamp;
    public static String GPSSignal;
    public static String CritLevel;

    public static void StringSplitter(String s)
    {
        String[] Arr = new String[4];
        int i = 0;
        for (String part: s.split("-"))
        {
            Arr[i] = part;
            ++i;
        }
        AndroidID = Arr[0];
        TimeStamp = Arr[1];
        GPSSignal = Arr[2];
        CritLevel = Arr[3];
    }
}