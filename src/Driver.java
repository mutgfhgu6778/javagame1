package src;

import src.map.editor.Controller;
import src.map.util.GameMap;
import src.map.util.MapLoader;
import src.map.util.MapReader;
import src.map.util.XMLMapReader;
import src.utility.GameCallback;
import src.utility.PropertiesLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Driver {

    /**
     * Starting point
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        if (args.length == 1) {
            // TODO: 可以在这里判断args和读取XML
            if (args[0].endsWith(".xml")) {
                new Controller(0, args[0]);
            } else {
                new Controller(1, args[0]);
            }
        }
        else if (args.length == 0) {
            new Controller();
        } else {
            System.out.println("Invalid command line argument, please try again.");
        }
    }
}
