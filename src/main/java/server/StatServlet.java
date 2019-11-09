package server;

import com.google.gson.Gson;

//import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import databaseUtils.StatsDao;
import io.swagger.client.model.APIStats;
import io.swagger.client.model.APIStatsEndpointStats;

@WebServlet(name = "StatServlet", value = "/statistics/*")
public class StatServlet extends HttpServlet {
    protected StatsDao statsDao;
//    final static Logger logger = Logger.getLogger(StatServlet.class);

    public void init() throws ServletException {
        statsDao = StatsDao.getInstance();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {}

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        String urlPath = req.getPathInfo();

        res.setStatus(HttpServletResponse.SC_OK);
        APIStats apiStats = new APIStats();
        try {
            for (String server : new String[]{"resort", "skier"}) {
                for  (String operation : new String[]{"GET", "POST"}) {
                    APIStatsEndpointStats apiStatsEndpointStats = new APIStatsEndpointStats();
                    server.Stat stat = statsDao.getStat(server, operation);
                    apiStatsEndpointStats.setURL("/" + server);
                    apiStatsEndpointStats.setOperation(operation);
                    apiStatsEndpointStats.setMean(stat.getMean());
                    apiStatsEndpointStats.setMax(stat.getMax());

                    apiStats.addEndpointStatsItem(apiStatsEndpointStats);
                }
            }
        } catch (SQLException e) {
//            logger.error(e);
        }
        res.getWriter().write(new Gson().toJson(apiStats));
    }
}