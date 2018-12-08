package backhaul.server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataExtraction
{

    public static void extract() throws IOException, InterruptedException
    {
        int lineCounter = 0;
        BufferedReader br = null;
        String sCurrentLine = null;
        List<Double> list = new ArrayList<Double>();


        File[] directory = new File("Training_Set").listFiles();
        File resultsFile = new File("training_set.csv");
        FileOutputStream nullifier = new FileOutputStream(resultsFile);
        BufferedWriter resultsWriter = new BufferedWriter(new FileWriter(resultsFile));

        for (File file : directory)
        {
            int lineNo = -1;
            int columnNo;

            if (file.isFile())
            {
                br = new BufferedReader(new FileReader(file));
                while ((sCurrentLine = br.readLine()) != null)
                {
                    lineNo++;
                    if (lineNo != 0)
                    {
                        lineCounter++;
                        String[] arr = sCurrentLine.split(",");
                        for (columnNo = 0; columnNo < 14; columnNo++)
                        {
                            list.add(Double.parseDouble(arr[columnNo]));
                        }
                    }
                    else if (lineNo == 0)
                    {
                        continue;
                    }
                }
            }


            if ((file.getName().contains("Eyes")) && (file.getName().contains("Closed")))
            {
                resultsWriter.write("Eyes Closed");
            }
            else if ((file.getName().contains("Eyes")) && (file.getName().contains("Opened")))
            {
                resultsWriter.write("Eyes Opened");
            }
            resultsWriter.write(",");
            double[] vector = new double[lineCounter];
            for (columnNo = 0; columnNo < 14; columnNo++)
            {
                lineNo = 0;
                for (int i = 0; i < list.size(); i += 14)
                {
                    vector[lineNo] = list.get(i + columnNo);
                    lineNo++;
                }
                resultsWriter.write(Double.toString(Entropy.calculateEntropy(vector)));
                resultsWriter.write(",");
            }
            resultsWriter.newLine();
        }
        resultsWriter.close();
    }
}
