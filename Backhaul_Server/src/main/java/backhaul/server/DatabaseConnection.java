package backhaul.server;

import java.sql.*;

public class DatabaseConnection
{

    static String databaseUrl = "jdbc:mysql://localhost:3306/database4bh";//"jdbc:mysql://ensembldb.ensembl.org:3337/MySQL";

    public static void connect() throws SQLException
    {
        try
        {
            //1. get connection to database
            Connection myConn = DriverManager.getConnection(databaseUrl, "root", "root");
            //2. create statement
            Statement myStmt = myConn.createStatement();
            //3. Execute sql query
            String sql = "insert into database4bh" + "(Android_ID,Time_Stamp,GPS_Signal,Criticality)" +
                    (DatabaseElement.AndroidID + DatabaseElement.TimeStamp + DatabaseElement.GPSSignal + DatabaseElement.CritLevel);
            //System.out.println(" -connection established to SQL database");
            myStmt.executeUpdate(sql);
        }
        catch (SQLException e) { System.err.println(e.getMessage()); }
        finally
        {
            connection.close();
            System.out.println(" -connection to SQL database closed");
        }
    }
}