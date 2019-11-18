package databaseUtils;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import io.swagger.client.model.*;

public class ResortsDao {

    protected ConnectionManager connectionManager;
    // Single pattern: instantiation is limited to one object.
    private static ResortsDao  instance = null;
    protected ResortsDao () {
        connectionManager = ConnectionManager.getInstance();
    }
    public static ResortsDao  getInstance() {
        if(instance == null) {
            instance = new ResortsDao ();
        }
        return instance;
    }


    /**
     * Get all the resort records by fetching it from MySQL instance.
     * This runs a SELECT statement and returns a list of resorts.
     */
    public ResortsList getAllResorts() throws SQLException {

        ResortsList resortsList = new ResortsList();
        String selectResorts = "SELECT * FROM Resorts;";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
            connection = connectionManager.getConnection();
            selectStmt = connection.prepareStatement(selectResorts);
            results = selectStmt.executeQuery();

            while(results.next()) {
                String resortName = results.getString("ResortName");
                int resortId = results.getInt("ResortId");
                ResortsListResorts r = new ResortsListResorts();
                r.setResortName(resortName);
                r.setResortID(resortId);
                resortsList.addResortsItem(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        return resortsList;
    }

    /**
     * Get resort by resort ID.
     */
    public ResortsListResorts getResortByResortId(int resortId) throws SQLException {
        ResortsListResorts resort = null;

        String selectSeasons =
            "SELECT * FROM Resorts WHERE ResortId=?;";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
            connection = connectionManager.getConnection();
            selectStmt = connection.prepareStatement(selectSeasons);
            selectStmt.setInt(1, resortId);
            results = selectStmt.executeQuery();
            if(results.next()) {
                String resortName = results.getString("ResortName");
                resort = new ResortsListResorts();
                resort.setResortID(resortId);
                resort.setResortName(resortName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        return resort;
    }


    /**
     * Get all the seasons by resort ID.
     */
    public SeasonsList getSeasonsByResortId(int resortId) throws SQLException {
        SeasonsList seasonsList = new SeasonsList();
        seasonsList.setSeasons(new ArrayList<>());

        String selectSeasons =
            "SELECT * " +
                "FROM Resorts INNER JOIN Seasons " +
                " ON Resorts.ResortId = Seasons.ResortId " +
                "WHERE Resorts.ResortId=?;";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
            connection = connectionManager.getConnection();
            selectStmt = connection.prepareStatement(selectSeasons);
            selectStmt.setInt(1, resortId);
            results = selectStmt.executeQuery();
            while(results.next()) {
                int season = results.getInt("Seasons.Season");
                seasonsList.addSeasonsItem(String.valueOf(season));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        return seasonsList;
    }

    public void createSeason(int season, int resortId) throws SQLException {

        String insertSeason = "INSERT INTO Seasons(Season, ResortId) VALUES(?,?);";
        Connection connection = null;
        PreparedStatement insertStmt = null;
        try {
            connection = connectionManager.getConnection();
            insertStmt = connection.prepareStatement(insertSeason, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setInt(1, season);
            insertStmt.setInt(2, resortId);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() != 1062) {
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
