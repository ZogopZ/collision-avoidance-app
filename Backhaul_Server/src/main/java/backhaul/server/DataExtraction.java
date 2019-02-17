package backhaul.server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataExtraction
{

    public static void extract() throws IOException, InterruptedException
    {
        int lineCounter = 0;
        BufferedReader br;
        String sCurrentLine;
        List<Double> list = new ArrayList<>();


        File directory = new File("Training_Set");
        System.out.println(" -listing all files included in directory " + directory);
        Thread.sleep(1000);
        File[] directoryContents = new File(directory.getPath()).listFiles();
         //List all files inside Training Set directory.
        File resultsFile = new File("training_set.csv");
         //Create a new empty training_set.csv file.
        System.out.println(" -creating empty file " + resultsFile);
        Thread.sleep(1000);
        FileOutputStream nullifier = new FileOutputStream(resultsFile);
        nullifier.close();
         //Truncate file training_set.csv if it already exists.
        BufferedWriter resultsWriter = new BufferedWriter(new FileWriter(resultsFile));

        System.out.print(" -extracting data and calculating entropies");
        for (File file : directoryContents)
        { //For each file inside Training Set directory.
            System.out.print(".");
            Thread.sleep(10);
            int lineNo = -1;
            int columnNo;

            if (file.isFile())
            {
                br = new BufferedReader(new FileReader(file));
                while ((sCurrentLine = br.readLine()) != null)
                { //Parse each line of file.
                    lineNo++;
                    if (lineNo != 0)
                    { //Skip the first line.
                        lineCounter++; //Counter number of lines.
                        String[] arr = sCurrentLine.split(","); //Get current line's data, deliminated by "," .
                        for (columnNo = 0; columnNo < 14; columnNo++)
                        { //Get the first 14 columns of each line.
                            list.add(Double.parseDouble(arr[columnNo])); //Add doubles to list.
                        }
                    }
                    else if (lineNo == 0)
                    { //Skip the first line. It contains channel names etc.
                        continue;
                    }
                }
            }


            if ((file.getName().contains("Eyes")) && (file.getName().contains("Closed")))
            { //Check for Eyes Closed in file's name.
                resultsWriter.write("Eyes Closed"); //Write Eyes Closed to training_set.csv file.
            }
            else if ((file.getName().contains("Eyes")) && (file.getName().contains("Opened")))
            { //Check for Eyes Opened in file's name.
                resultsWriter.write("Eyes Opened"); //Write Eyes Opened to training_set.csv file.
            }
            resultsWriter.write(","); //Deliminate with "," .
            double[] vector = new double[lineCounter]; //Create a vector to store double types. This vector will be
             //used with entropy calculator. It's size is set according to number of lines of specific file.
            for (columnNo = 0; columnNo < 14; columnNo++)
            { //This for loop will iterate through all "columns" of arraylist.
                lineNo = 0;
                for (int i = 0; i < list.size(); i += 14)
                { //This for loop, will iterate through all "lines" of arraylist.
                    vector[lineNo] = list.get(i + columnNo); //Copy data to vector.
                    lineNo++;
                }
                resultsWriter.write(Double.toString(Entropy.calculateEntropy(vector)));
                 //Calculate entropy of vector and write it back to training_set.csv file.
                resultsWriter.write(","); //Deliminate with "," .
            }

            resultsWriter.newLine(); //Add a new line to training_set.csv file.
        }
        System.out.printf("\n -writing new data to " + resultsFile.getName());
        Thread.sleep(1000);
        System.out.printf("\n -%s file is ready for upload\n", resultsFile.getName());
        resultsWriter.close();
    }
}
