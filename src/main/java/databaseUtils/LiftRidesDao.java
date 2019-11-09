package databaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import io.swagger.client.model.SkierVertical;
import io.swagger.client.model.SkierVerticalResorts;
import io.swagger.client.model.LiftRide;

public class LiftRidesDao {

    protected ConnectionManager connectionManager;
    // Single pattern: instantiation is limited to one object.
    private static LiftRidesDao instance = null;
    protected LiftRidesDao() {
        connectionManager = new ConnectionManager();
    }
    public static LiftRidesDao getInstance() {
        if(instance == null) {
            instance = new LiftRidesDao();
        }
        return instance;
    }


    public SkierVertical getTotalVertical (int skierId, int resortId, int seasonId) throws SQLException {
        SkierVertical skierVertical = new SkierVertical();
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        String whereStmt = "WHERE ResortId=? AND SkierId=?";
        if (seasonId > 0) {
            whereStmt = "WHERE ResortId=? AND SkierId=? AND SeasonId=?";
        }
        String selectVertical = "SELECT SeasonId, SUM(Vertical) AS TotalVertical " +
            "FROM (SELECT SeasonId, Vertical FROM LiftRides " +
            whereStmt +
            ") AS V " +
            "GROUP BY SeasonId;";

        try {
            connection = connectionManager.getConnection();
            selectStmt = connection.prepareStatement(selectVertical);
            selectStmt.setInt(1, resortId);
            selectStmt.setInt(2, skierId);
            if (seasonId > 0) {
                selectStmt.setInt(3, seasonId);
            }
            results = selectStmt.executeQuery();

            while(results.next()) {
                int totalVertical = results.getInt("TotalVertical");
                seasonId = results.getInt("SeasonId");
                SkierVerticalResorts skierVerticalResorts = new SkierVerticalResorts();
                skierVerticalResorts.setSeasonID(String.valueOf(seasonId));
                skierVerticalResorts.setTotalVert(totalVertical);
                skierVertical.addResortsItem(skierVerticalResorts);
            }
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
        return skierVertical;
    }

    public int getVerticalBySkier (int resortId, int seasonId, int dayId, int skierId) throws SQLException {
        int vertical = 0;
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        String selectVertical = "SELECT SUM(Vertical) AS TotalVertical " +
            "FROM (SELECT Vertical FROM LiftRides " +
            "WHERE ResortId=? AND SeasonId=? AND DayId=? AND SkierId=?) AS V;";

        try {
            connection = connectionManager.getConnection();
            selectStmt = connection.prepareStatement(selectVertical);
            selectStmt.setInt(1, resortId);
            selectStmt.setInt(2, seasonId);
            selectStmt.setInt(3, dayId);
            selectStmt.setInt(4, skierId);
            results = selectStmt.executeQuery();

            if(results.next()) {
                vertical = results.getInt("TotalVertical");
            }
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
        return vertical;
    }

    public void insertLiftRide (int resortId, int seasonId, int dayId, int skierId, LiftRide liftRide) throws SQLException {
        String insertLiftRide = "INSERT INTO LiftRides(ResortId, SeasonId, DayId, SkierId, StartTime, LiftId, Vertical) VALUES(?,?,?,?,?,?,?);";
        Connection connection = null;
        PreparedStatement insertStmt = null;

        try {
            connection = connectionManager.getConnection();
            insertStmt = connection.prepareStatement(insertLiftRide);
            insertStmt.setInt(1, resortId);
            insertStmt.setInt(2, seasonId);
            insertStmt.setInt(3, dayId);
            insertStmt.setInt(4, skierId);
            insertStmt.setInt(5, liftRide.getTime());
            insertStmt.setInt(6, liftRide.getLiftID());
            insertStmt.setInt(7, 10 * liftRide.getLiftID());
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
