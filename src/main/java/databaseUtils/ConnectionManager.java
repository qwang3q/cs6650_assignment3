package databaseUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

/**
 * Use ConnectionManager to connect to your database instance.
 *
 * ConnectionManager uses the MySQL Connector/J driver to connect to your local MySQL instance.
 *
 * In our example, we will create a DAO (data access object) java class to interact with
 * each MySQL table. The DAO java classes will use ConnectionManager to open and close
 * connections.
 *
 * Instructions:
 * 1. Install MySQL Community Server. During installation, you will need to set up a user,
 * password, and port. Keep track of these values.
 * 2. Download and install Connector/J: http://dev.mysql.com/downloads/connector/j/
 * 3. Add the Connector/J JAR to your buildpath. This allows your application to use the
 * Connector/J library. You can add the JAR using either of the following methods:
 *   A. When creating a new Java project, on the "Java Settings" page, go to the
 *   "Libraries" tab.
 *   Click on the "Add External JARs" button.
 *   Navigate to the Connector/J JAR. On Windows, this looks something like:
 *   C:\Program Files (x86)\MySQL\Connector.J 5.1\mysql-connector-java-5.1.34-bin.jar
 *   B. If you already have a Java project created, then go to your project properties.
 *   Click on the "Java Build Path" option.
 *   Click on the "Libraries" tab, click on the "Add External Jars" button, and
 *   navigate to the Connector/J JAR.
 * 4. Update the "private final" variables below.
 */
public class ConnectionManager {
    private final String DB_NAME = "SkiDataAPI";
    private final String DB_USER = "root";
    private final String DB_PASS = "qianwang";
    private final String CLOUD_SQL_CONNECTION_NAME = "cs6650-258222:us-west2:cs6650-skier";

    private DataSource pool = null;

    public Connection getConnection() throws SQLException {

        if (pool == null) {
            // The configuration object specifies behaviors for the connection pool.
            HikariConfig config = new HikariConfig();

            // Configure which instance and what database user to connect with.
            config.setJdbcUrl(String.format("jdbc:mysql:///%s", DB_NAME));
            config.setUsername(DB_USER); // e.g. "root", "postgres"
            config.setPassword(DB_PASS); // e.g. "my-password"

            // For Java users, the Cloud SQL JDBC Socket Factory can provide authenticated connections.
            // See https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory for details.
            config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
            config.addDataSourceProperty("cloudSqlInstance", CLOUD_SQL_CONNECTION_NAME);
            config.addDataSourceProperty("useSSL", "false");

            // Initialize the connection pool using the configuration object.
            pool = new HikariDataSource(config);
        }
        return pool.getConnection();
    }

    /** Close the connection to the database instance. */
    public void closeConnection(Connection connection) throws SQLException {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
