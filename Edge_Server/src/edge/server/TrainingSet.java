package edge.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class TrainingSet {

    public static void localClassify(File trainingSet)
    {
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(trainingSet.getAbsoluteFile()));
            String str;
            ArrayList<String> lines = new ArrayList<String>();
            while ((str = in.readLine()) != null) //Read each line from training_set.csv.
            {
                lines.add(str); //Add each line to an array list.
            }
            String[] linesArray = lines.toArray(new String[lines.size()]);
//            for (String gen : linesArray)
//            {
//                System.out.printf("\n");
//                System.out.print(gen);
//            }
        }
        catch (Exception ex) { ex.printStackTrace(); }
    }
}
