package server;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import databaseUtils.StatsDao;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;
import java.util.logging.Level;

//@WebListener("Creates a connection pool that is stored in the Servlet's context for later use.")
public class StatisticOperator implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(StatisticOperator.class.getName());

    // A random key as key in database for identifying the row responsible for current server
    private String uuid;
    // Shared filepath for recording response durations
    private String resortGetStatPath;
    private String resortPostStatPath;
    private String skierGetStatPath;
    private String skierPostStatPath;

    private ScheduledExecutorService scheduler;

    public void contextInitialized(ServletContextEvent e) {
        System.out.println("starting context listener");
        LOGGER.log(Level.WARNING, "starting context listener");
        ServletContext cntxt = e.getServletContext();

        uuid = UUID.randomUUID().toString();
        try {
            resortGetStatPath = File.createTempFile(uuid + "_resort_get", null).getAbsolutePath();
            resortPostStatPath = File.createTempFile(uuid + "_resort_post", null).getAbsolutePath();
            skierGetStatPath = File.createTempFile(uuid + "_skier_get", null).getAbsolutePath();
            skierPostStatPath = File.createTempFile(uuid + "_skier_post", null).getAbsolutePath();
        } catch (IOException ioErr) {
            System.out.println("Encounter IO error, quite context");
            ioErr.printStackTrace();
            return;
        }

        System.out.println("Stats stored on this server:");
        System.out.println(resortPostStatPath);
        System.out.println(resortPostStatPath);
        System.out.println(skierGetStatPath);
        System.out.println(skierPostStatPath);

        cntxt.setAttribute("resortGetStatPath", resortGetStatPath);
        cntxt.setAttribute("resortPostStatPath", resortPostStatPath);
        cntxt.setAttribute("skierGetStatPath", skierGetStatPath);
        cntxt.setAttribute("skierPostStatPath", skierPostStatPath);

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new StatsReporter(this.uuid, resortGetStatPath, resortPostStatPath, skierGetStatPath, skierPostStatPath),
            0, 5, TimeUnit.SECONDS);
    }

    private class StatsReporter implements Runnable {
        protected StatsDao statsDao;

        private String uuid;
        private String resortGetStatPath;
        private String resortPostStatPath;
        private String skierGetStatPath;
        private String skierPostStatPath;

        public StatsReporter(
            String uuid,
            String resortGetStatPath,
            String resortPostStatPath,
            String skierGetStatPath,
            String skierPostStatPath)
        {
            this.uuid = uuid;
            this.resortGetStatPath = resortGetStatPath;
            this.resortPostStatPath = resortPostStatPath;
            this.skierGetStatPath = skierGetStatPath;
            this.skierPostStatPath = skierPostStatPath;
        }

        @Override
        public void run() {
            statsDao = StatsDao.getInstance();

            System.out.println("Thread up");
            Stat resortGetStat = new Stat("Resort", "GET", resortGetStatPath);
            Stat resortPostStat = new Stat("Resort", "POST", resortPostStatPath);
            Stat skierGetStat = new Stat("Skier", "GET", skierGetStatPath);
            Stat skierPostStat = new Stat("Skier", "POST", skierPostStatPath);

            for (Stat stat : new Stat[]{
                resortGetStat, resortPostStat, skierGetStat, skierPostStat
            }) {
                stat.loadFromFile();

                LOGGER.log(Level.WARNING, "before trying updating stats");

                // Write uuid, count, mean, max, server, operation in database
                try {
                    statsDao.updateStat(this.uuid, stat);
                    LOGGER.log(Level.WARNING, "successfully updated stats");
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "failed updating stats with SQLException");
                    e.printStackTrace();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "failed updating stats with other exception");
                    e.printStackTrace();
                }
            }
        }
    }

    public void contextDestroyed(ServletContextEvent e){
        scheduler.shutdownNow();
        System.out.println("Destroyed");
    }
}
