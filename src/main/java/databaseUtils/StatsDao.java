package databaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatsDao {

    protected ConnectionManager connectionManager;
    // Single pattern: instantiation is limited to one object.
    private static StatsDao instance = null;
    protected StatsDao() {
        connectionManager = ConnectionManager.getInstance();
    }
    public static StatsDao getInstance() {
        if(instance == null) {
            instance = new StatsDao();
        }
        return instance;
    }

    public server.Stat getStat (String server, String operation) throws SQLException {
        server.Stat stat = new server.Stat(server, operation, null);
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        String whereStmt = "WHERE Server=? AND Operation=?";
        String selectRows = "SELECT * FROM Stats " + whereStmt;

        try {
            connection = connectionManager.getConnection();
            selectStmt = connection.prepareStatement(selectRows);
            selectStmt.setString(1, server);
            selectStmt.setString(2, operation);
            results = selectStmt.executeQuery();

            int countAll = 0;
            int sumAll = 0;
            int maxAll = 0;
            while(results.next()) {
                int count = results.getInt("Count");
                int mean = results.getInt("Mean");
                int max = results.getInt("Max");

                countAll += count;
                sumAll += count * mean;
                maxAll = maxAll > max ? maxAll : max;
            }
            int mean = countAll == 0 ? 0 : sumAll/countAll;
            stat.setCount(countAll);
            stat.setMean(mean);
            stat.setMax(maxAll);
        } catch (SQLException e) {
            throw e;
        } finally {
            if(connection != null) {
                connection.close();
            }
            if(selectStmt != null) {
                selectStmt.close();
            }
            if(results != null) {
                results.close();
            }
        }
        return stat;
    }

    public void updateStat (String uuid, server.Stat stat) throws SQLException {
        String deleteSt = "DELETE FROM Stats WHERE UniqueId=? AND Server=? AND Operation=? ";
        String updateStatSt = "INSERT INTO Stats(UniqueId, Count, Mean, Max, Server, Operation) VALUES(?,?,?,?,?,?);";
        Connection connection = null;
        PreparedStatement deleteStmt = null;
        PreparedStatement insertStmt = null;
        connection = connectionManager.getConnection();

        try {
            deleteStmt = connection.prepareStatement(deleteSt);
            deleteStmt.setString(1, uuid);
            deleteStmt.setString(2, stat.getServer());
            deleteStmt.setString(3, stat.getOperation());
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }

        try {
            insertStmt = connection.prepareStatement(updateStatSt);
            insertStmt.setString(1, uuid);
            insertStmt.setInt(2, stat.getCount());
            insertStmt.setInt(3, stat.getMean());
            insertStmt.setInt(4, stat.getMax());
            insertStmt.setString(5, stat.getServer());
            insertStmt.setString(6, stat.getOperation());
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw e;
            }
        } finally {
            if(connection != null) {
                connection.close();
            }
            if(insertStmt != null) {
                insertStmt.close();
            }
        }
    }
}
