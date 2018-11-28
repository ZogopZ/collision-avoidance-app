package edge_server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class TrainingSet
{

    public static void localClassify(File trainingSet)
    {
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(trainingSet.getAbsoluteFile()));
            String line;
            while ((line = in.readLine()) != null)
            {
                System.out.println(line);
            }
            in.close();
        }
        catch (Exception ex) {ex.printStackTrace();}
    }
}
