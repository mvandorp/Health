package com.health.visuals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.health.Chunk;
import com.health.Column;
import com.health.Record;
import com.health.Table;
import com.health.input.Input;
import com.health.input.InputException;

public class BoxPlot {
    
    /**
     * Creates a diagram with for each Chunk a BoxPlot.
     * @param table Table to use
     */
    public static void boxPlot(Table table, String column) {
        
    }
    
    private Map<String, Integer> createFrequencyMap(Chunk chunk, String column) {
        String columnName = column;

        // Create map to save frequencies
        Map<String, Integer> freqMap = new HashMap<String, Integer>();

        for (Chunk c : table) {
            for (Record r : c) {
                // Get value of record
                String value = r.getValue(columnName).toString();
                if (!freqMap.containsKey(value)) {
                    freqMap.put(value, 1);
                } else {
                    int currentFrequency = freqMap.get(value);
                    freqMap.replace(value, ++currentFrequency);
                }
            }
        }
        
        return null;
    }
    
    public static void main(String[] args){
        String filePath = "/home/bjorn/Documents/Context/Health/health/data/data_use/txtData.txt";
        String configPath = "/home/bjorn/Documents/Context/Health/health/data/configXmls/admireTxtConfig.xml";
        
        try {
            Table table = Input.readTable(filePath, configPath);
            //method to test
        } catch (IOException | ParserConfigurationException | SAXException
                | InputException e) {
            System.out.println("Error!");
        }
    }
}