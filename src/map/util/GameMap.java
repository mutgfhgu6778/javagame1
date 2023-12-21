package src.map.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMap {

    private final static int DEFAULT_NBHORZCELLS = 20;
    private final static int DEFAULT_NBVERTCELLS = 11;

    private int nbHorzCells;
    private int nbVertCells;
    private Map<String, List<int[]>> actors = new HashMap<>();
    private Map<String, List<int[]>> portals = new HashMap<>();
    private String map;


    public GameMap(int nbHorzCells, int nbVertCells, Map<String, List<int[]>> actors, String map) {
        this.nbHorzCells = nbHorzCells;
        this.nbVertCells = nbVertCells;
        this.actors = actors;
        this.map = map;
    }

    public GameMap(Map<String, List<int[]>> actors, Map<String, List<int[]>> portals, String map) {
        this.nbHorzCells = DEFAULT_NBHORZCELLS;
        this.nbVertCells = DEFAULT_NBVERTCELLS;
        this.actors = actors;
        this.portals = portals;
        this.map = map;
    }

    public int getNbHorzCells() {
        return nbHorzCells;
    }

    public int getNbVertCells() {
        return nbVertCells;
    }

    public Map<String, List<int[]>> getActors() {
        return actors;
    }

    public String getMap() {
        return map;
    }

    public Map<String, List<int[]>> getPortals() {
        return portals;
    }
}
