// PacMan.java
// Simple PacMan implementation
package src;

import ch.aplu.jgamegrid.*;
import src.map.util.GameMap;
import src.utility.GameCallback;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Game extends GameGrid
{
  private final static int nbHorzCells = 20;
  private final static int nbVertCells = 11;
  protected PacManGameGrid grid;

  protected PacActor pacActor = new PacActor(this);
  private List<Monster> monsters = new ArrayList<>();

  private ArrayList<Location> pillAndItemLocations = new ArrayList<Location>();
  private ArrayList<Actor> iceCubes = new ArrayList<Actor>();
  private ArrayList<Actor> goldPieces = new ArrayList<Actor>();
  private GameCallback gameCallback;
  private Properties properties;
  private int seed = 30006;

  private GameMap currentMap = null;
  public GameMap getCurrentMap() {
    return currentMap;
  }

  public Game(GameCallback gameCallback, Properties properties, List<GameMap> maps)
  {

    super(nbHorzCells, nbVertCells, 32, false);
    //Setup game
    this.gameCallback = gameCallback;
    this.properties = properties;
    setSimulationPeriod(100);
    setTitle("[PacMan in the Multiverse]");
    //Setup for auto test
    pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));

    for (int i=0; i<maps.size(); i++) {

      GameMap map = maps.get(i);
      this.currentMap = map;
      // maps to be used
      grid = new PacManGameGrid(nbHorzCells, nbVertCells, map.getMap());

      setupActorLocations(map);

      GGBackground bg = getBg();
      drawGrid(bg);

      //Setup Random seeds
      seed = Integer.parseInt(properties.getProperty("seed"));
      pacActor.setSeed(seed);
      pacActor.setSlowDown(3);
      for (Monster monster : monsters) {
        monster.setSeed(seed);
        monster.setSlowDown(3);
      }
      addKeyRepeatListener(pacActor);
      setKeyRepeatPeriod(150);


      //Run the game
      doRun();
      show();
      // Loop to look for collision in the application thread
      // This makes it improbable that we miss a hit
      boolean hasPacmanBeenHit;
      boolean hasPacmanEatAllPills;
      setupPillAndItemsLocations();
      int maxPillsAndItems = countPillsAndItems();

      do {
        hasPacmanBeenHit = false;
        for (Monster monster : monsters) {
          if (monster.getLocation().equals(pacActor.getLocation())) {
            hasPacmanBeenHit = true;
          }
        }
        hasPacmanEatAllPills = pacActor.getNbPills() >= maxPillsAndItems;
        delay(10);
      } while(!hasPacmanBeenHit && !hasPacmanEatAllPills);
      delay(120);

      Location loc = pacActor.getLocation();
      for (Monster monster : monsters) {
        monster.setStopMoving(true);
      }
      pacActor.removeSelf();

      String title = "";
      if (hasPacmanBeenHit) {
        bg.setPaintColor(Color.red);
        title = "GAME OVER";
        addActor(new Actor("sprites/explosion3.gif"), loc);
        setTitle(title);
        gameCallback.endOfGame(title);
        doPause();
        hide();
        return;
      }
      else if (hasPacmanEatAllPills && i == maps.size() - 1) {
        // continue play unless the last level
        bg.setPaintColor(Color.yellow);
        title = "YOU WIN";
        setTitle(title);
        gameCallback.endOfGame(title);
        doPause();
        hide();
        return;
      } else {
        // reset pacman
        pacActor = new PacActor(this);
      }
    }
  }

  public GameCallback getGameCallback() {
    return gameCallback;
  }

  private void setupActorLocations(GameMap map) {
    Map<String, List<int[]>> actors = map.getActors();
    for (Map.Entry<String, List<int[]>> entry : actors.entrySet()) {
      for (int[] loc : entry.getValue()) {
        switch (entry.getKey()) {
          case "pacman":
            addActor(pacActor, new Location(loc[0], loc[1]));
            break;
          case "TX5":
            Monster tx5 = new Monster(this, MonsterType.TX5);
            tx5.stopMoving(5);
            monsters.add(tx5);
            addActor(tx5, new Location(loc[0], loc[1]), Location.NORTH);
            break;
          case "Troll":
            Monster troll = new Monster(this, MonsterType.Troll);
            monsters.add(troll);
            addActor(troll, new Location(loc[0], loc[1]), Location.NORTH);
            break;
        }
      }
    }
  }

  private int countPillsAndItems() {
    int pillsAndItemsCount = 0;
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        Location location = new Location(x, y);
        int a = grid.getCell(location);
        if (a == 1) { // Pill
          pillsAndItemsCount++;
        } else if (a == 3) { // Gold
          pillsAndItemsCount++;
        }
      }
    }

    return pillsAndItemsCount;
  }

  public ArrayList<Location> getPillAndItemLocations() {
    return pillAndItemLocations;
  }

  private void setupPillAndItemsLocations() {
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        Location location = new Location(x, y);
        int a = grid.getCell(location);
        if (a == 1) {
          pillAndItemLocations.add(location);
        }
        if (a == 3) {
          pillAndItemLocations.add(location);
        }
        if (a == 4) {
          pillAndItemLocations.add(location);
        }
      }
    }
  }

  private void drawGrid(GGBackground bg)
  {
    bg.clear(Color.gray);
    bg.setPaintColor(Color.white);
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        bg.setPaintColor(Color.white);
        Location location = new Location(x, y);
        int a = grid.getCell(location);
        if (a > 0)
          bg.fillCell(location, Color.lightGray);
        if (a == 1) { // Pill
          putPill(bg, location);
        } else if (a == 3) { // Gold
          putGold(bg, location);
        } else if (a == 4) {
          putIce(bg, location);
        } else if (a >= 5) {
          putPortal(bg, location, a);
        }
      }
    }
  }

  private void putPill(GGBackground bg, Location location){
    bg.fillCircle(toPoint(location), 5);
  }

  private void putGold(GGBackground bg, Location location){
    bg.setPaintColor(Color.yellow);
    bg.fillCircle(toPoint(location), 5);
    Actor gold = new Actor("sprites/gold.png");
    this.goldPieces.add(gold);
    addActor(gold, location);
  }

  private void putIce(GGBackground bg, Location location){
    bg.setPaintColor(Color.blue);
    bg.fillCircle(toPoint(location), 5);
    Actor ice = new Actor("sprites/ice.png");
    this.iceCubes.add(ice);
    addActor(ice, location);
  }

  private void putPortal(GGBackground bg, Location location, int type) {
    bg.setPaintColor(Color.black);
    bg.fillCircle(toPoint(location), 5);
    Actor portal;
    switch (type) {
      case 5:
        portal = new Actor("data/i_portalWhiteTile.png");
        break;
      case 6:
        portal = new Actor("data/j_portalYellowTile.png");
        break;
      case 7:
        portal = new Actor("data/k_portalDarkGoldTile.png");
        break;
      default:
        portal = new Actor("data/l_portalDarkGrayTile.png");
        break;
    }
    addActor(portal, location);
  }

  public void removeItem(String type,Location location){
    if(type.equals("gold")){
      for (Actor item : this.goldPieces){
        if (location.getX() == item.getLocation().getX() && location.getY() == item.getLocation().getY()) {
          item.hide();
        }
      }
    }else if(type.equals("ice")){
      for (Actor item : this.iceCubes){
        if (location.getX() == item.getLocation().getX() && location.getY() == item.getLocation().getY()) {
          item.hide();
        }
      }
    }
  }

  public int getNumHorzCells(){
    return this.nbHorzCells;
  }
  public int getNumVertCells(){
    return this.nbVertCells;
  }
}
