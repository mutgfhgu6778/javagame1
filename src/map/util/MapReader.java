package src.map.util;


// Strategy Pattern used to read different map files based on format
public interface MapReader {
    GameMap readMapFile(String filename);
}