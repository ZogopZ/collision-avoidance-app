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


        File directory = new File("Training_Set");
        System.out.println(" -listing all files included in directory " + directory);
        Thread.sleep(1000);
        File[] directoryContents = new File(directory.getPath()).listFiles();
        File resultsFile = new File("training_set.csv");
        System.out.println(" -creating empty file " + resultsFile);
        Thread.sleep(1000);
        FileOutputStream nullifier = new FileOutputStream(resultsFile);
        BufferedWriter resultsWriter = new BufferedWriter(new FileWriter(resultsFile));

        System.out.print(" -extracting data and calculating entropies");
        for (File file : directoryContents)
        {
            System.out.print(".");
            Thread.sleep(10);
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
        System.out.printf("\n -writing new data to " + resultsFile.getName());
        Thread.sleep(1000);
        System.out.printf("\n -%s file is ready for upload\n", resultsFile.getName());
        resultsWriter.close();
    }
}
