package MainPack;

import java.io.*;
import java.util.Scanner;

public class Database {
    public static final String path = "settings.txt";

    public static File file;
    public static PrintWriter dbWriter;
    public static Scanner dbReader;

    //Indexes
    public final static int ROBOTWIDTH = 0;
    public final static int ROBOTHEIGHT = 1;

    public final static int FIELDWIDTH = 2;
    public final static int FIELDHEIGHT = 3;

    private static double[] data = new double[4];

    public static void load(){

        try {

            file = new File(path);
            dbReader = new Scanner(file);

        } catch (Exception e){
            e.printStackTrace();
        }

        while (dbReader.hasNext()){
            String line = dbReader.nextLine();
            try {
                int i = Integer.parseInt("" + line.charAt(0));
                data[i] = Double.parseDouble(line.substring(line.indexOf('=') + 1, line.length()).trim());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        dbReader.close();
    }

    public static void save(){
        try {
            file.delete();
            file = new File(path);
            dbWriter = new PrintWriter(file);
        } catch (Exception e){
            e.printStackTrace();
        }

        for (int i=0; i<data.length; i++){
            dbWriter.println(i + "=" + data[i]);
        }

        dbWriter.close();
    }

    public static Double getData(int index){
        return data[index];
    }

    public static void setData(int index, double value){
        data[index] = value;
    }
}
