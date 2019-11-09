package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;

public class Stat {
    private List<Integer> responseDurations;

    private String server;
    private String operation;
    private String filePath;
    private int count;
    private int mean;
    private int max;

    public Stat(String server, String operation, String filePath) {
        this.responseDurations = new ArrayList<Integer>();

        this.server = server;
        this.operation = operation;
        this.filePath = filePath;
    }

    static void appendFile(String filePath, long duration) throws IOException {
        File file = new File(filePath);
        FileWriter fr = new FileWriter(file, true);
        fr.write("\n" + Long.toString(duration));
        fr.close();
    }

    public void loadFromFile() {
        // restart stats
        this.responseDurations = new ArrayList<Integer>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(this.filePath));
            while (reader.ready()) {
                String line = reader.readLine();
                if (!line.isEmpty()) {
                    this.responseDurations.add(Integer.parseInt(line));
                }
            }
            reader.close();

            // Update mean and max of latest 1000 operations, if less than 1000 returns all
            int sum = 0;
            int len = this.responseDurations.size();
            int size = len < 1000 ? len : 1000;;
            for(int i = len - 1; i > len - 1 - size; i--) {
                sum = sum + this.responseDurations.get(i);
                if (this.responseDurations.get(i) > max) {
                    this.setMax(this.responseDurations.get(i));
                }
            }
            int mean = size == 0 ? 0 : sum/size;
            this.setMean(mean);
            this.setCount(size);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* getter and setter */

    public List<Integer> getResponseDurations() {
        return responseDurations;
    }

    public void setResponseDurations(List<Integer> responseDurations) {
        this.responseDurations = responseDurations;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMean() {
        return mean;
    }

    public void setMean(int mean) {
        this.mean = mean;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
