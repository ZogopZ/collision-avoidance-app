package backhaul.server;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection
{
//    static String username = "root"; //To be changed according to remote's server username.
//    static String password = "root"; //To be changed according to remote's server password.
//    static String databaseUrl = "jdbc:mysql://localhost:3306/databaseName";
    //Connect to this database. localhost to be changed according to remote server, databaseName to be changed
    //according to remote's server database name.

    static String databaseUrl = "jdbc:mysql://ensembldb.ensembl.org:3337/MySQL";

    public static void connect() throws SQLException
    {
        Connection connection = null;
        try
        {
//            connection = DriverManager.getConnection(databaseUrl, username, password);
            connection = DriverManager.getConnection(databaseUrl);
            System.out.println(" -connection established to SQL database");
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {
            connection.close();
            System.out.println(" -connection to SQL database closed");
        }
    }
}
