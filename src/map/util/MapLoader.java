package src.map.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {
    private final MapReader mapReader;

    public MapLoader(MapReader mapReader) {
        this.mapReader = mapReader;
    }

    public GameMap loadMap(String arg) {
        // Load a single map
        File file = new File(arg);
        if (file.isFile()) {
            GameMap map = mapReader.readMapFile(file.getPath());
            boolean isValidMap = LevelChecker.levelCheck(arg, map);
            if (!isValidMap) return null;
            return map;
        }
        return null;
    }

    public List<GameMap> loadMaps(String arg) {
        List<GameMap> maps = new ArrayList<>();

        // check folder first
        List<File> validMaps = GameChecker.gameCheck(arg);
        if (validMaps == null) {
            return null;
        }
        for (File file : validMaps) {
            if (file.isFile()) {
                GameMap map = mapReader.readMapFile(file.getPath());
                boolean isValidMap = LevelChecker.levelCheck(arg, map);
                // TODO: where a level check failed in a game check
                maps.add(map);
            }
        }

        return maps;
    }

}

