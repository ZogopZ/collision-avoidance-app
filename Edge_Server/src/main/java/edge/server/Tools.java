package edge.server;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class Tools
{
    public static ArrayList<ExtractionResult> extractionResult = new ArrayList<>();
    public static String androidID;

    public static void localClassify(File trainingSet)
    {
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(trainingSet.getAbsoluteFile()));
            String str;
            ArrayList<String> lines = new ArrayList<String>();
            while ((str = in.readLine()) != null) //Read each line from training_set.csv.
                lines.add(str); //Add each line to an array list.
            in.close();
            String[] linesArray = lines.toArray(new String[0]);
        }
        catch (Exception ex) { ex.printStackTrace(); }
    }

    static File storeFile(String contents)
    {
        File file = null;
        try
        {
            String fileName;
            int startOfFileName = contents.indexOf(":") + 1;
            int endOfFileName = contents.indexOf("\n", startOfFileName);
            int startOfAndroidID = contents.indexOf(":", endOfFileName) + 1;
            int endOfAndroidID = contents.indexOf("\n", startOfAndroidID);
            if (endOfFileName != -1 && endOfAndroidID != -1)
            {
                fileName = contents.substring(startOfFileName, endOfFileName);
                androidID = contents.substring(startOfAndroidID, endOfAndroidID);
                file = new File("data/" + fileName);
                PrintWriter writer = new PrintWriter(file, "UTF-8");
                writer.print(contents.substring(endOfAndroidID + 1));
                writer.close();
            }
        }
        catch (UnsupportedEncodingException | FileNotFoundException e) { e.printStackTrace(); }
        return file;
    }

    static void extractData(File file)
    {
        try
        {
            int lineNo = -1;
            int columnNo;
            int lineCounter = 0;
            String currentLine;
            String expName;
            double[] dataVector;
            Vector<Double> resVector = new Vector<>();
            List<Double> list = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while ((currentLine = bufferedReader.readLine()) != null)
            { //Parse each line of file.
                lineNo++;
                if (lineNo != 0)
                { //Skip the first line.
                    lineCounter++; //Number of lines counter.
                    String[] arr = currentLine.split(","); //Get current line's data, deliminated by "," .
                    for (columnNo = 0; columnNo < 14; columnNo++)
                    { //Get the first 14 columns of each line.
                        list.add(Double.parseDouble(arr[columnNo])); //Add doubles to list.
                    }
                }
            }
            if (file.getName().contains("Closed"))
            { //Check for Eyes Closed in file's name.
                expName = "Eyes Closed";
            }
            else
            { //Check for Eyes Opened in file's name.
                expName = "Eyes Opened";
            }
            dataVector = new double[lineCounter]; //Create a vector to store double types. This vector will be
            //used with entropy calculator. It's size is set according to number of lines of specific file.
            for (columnNo = 0; columnNo < 14; columnNo++)
            { //This for loop, will iterate through all "columns" of arraylist.
                lineNo = 0;
                for (int i = 0; i < list.size(); i += 14)
                { //This for loop, will iterate through all "lines" of arraylist.
                    dataVector[lineNo] = list.get(i + columnNo); //Copy data to vector.
                    lineNo++;
                }
                resVector.add(Entropy.calculateEntropy(dataVector)); //Calculate entropy.
            }
            ExtractionResult exResult = new ExtractionResult(resVector, expName, androidID);
            extractionResult.add(exResult);
        }
        catch (IOException e) { e.printStackTrace(); }
    }
}

class ExtractionResult
{
    Vector<Double> featureVector;
    String expName;
    String androidID;

    ExtractionResult(Vector<Double> featureVector, String expName, String androidID)
    {
        this.featureVector = featureVector;
        this.expName = expName;
        this.androidID = androidID;
    }
}
