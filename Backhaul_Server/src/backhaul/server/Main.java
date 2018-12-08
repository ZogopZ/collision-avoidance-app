package backhaul.server;

import java.io.*;
import java.util.Collections;
import java.util.Vector;

public class Main {

    public static void main(String[] args)
    {
        try {
            BufferedReader br = null;
            String sCurrentLine = null;
            Vector vector = new Vector();


            File[] files = new File("Training_Set").listFiles();

            for (File file : files)
            {
                if (file.isFile())
                {
                    System.out.println("" +file);
                    br = new BufferedReader(new FileReader(file));
                    int i = -1;
                    int j;
                    while ((sCurrentLine = br.readLine()) != null)
                    {
                        i++;
                        if (i != 0)
                        {

                            String[] arr = sCurrentLine.split(",");
                            for (j = 0; j < 14; j++)
                            {
                                vector.add(i - 1, Double.parseDouble(arr[j]));

                            }
                        }
                        else if (i == 0)
                            continue;

                    }
                }
                break;
            }
            File resFile = new File("results.csv");
            FileOutputStream nullifier = new FileOutputStream("results.csv");
            BufferedWriter writer = new BufferedWriter(new FileWriter(resFile));
            int i = 1;
            Collections.reverse(vector);
            for (Object element : vector)
            {
                if ((i%15) == 0)
                {
                    writer.newLine();
                }
                writer.write(element.toString());
                writer.write(",");
                i++;
            }
            //BackhaulSocket.connect();
            System.out.println("FINISHING");
        }
        catch (Throwable t) { t.printStackTrace(); }

    }
}
