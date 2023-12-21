// PacActor.java
// Used for PacMan
package src;

import ch.aplu.jgamegrid.*;
import src.move.DirectStrategy;
import src.move.MovingStrategy;

import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PacActor extends Actor implements GGKeyRepeatListener
{
  private static final int nbSprites = 4;
  private int idSprite = 0;
  private int nbPills = 0;
  private int score = 0;
  private Game game;
  private ArrayList<Location> visitedList = new ArrayList<Location>();
  private final int listLength = 10;
  private int seed;
  private Random randomiser = new Random();
  public Random getRandom() {
    return this.randomiser;
  }

  private MovingStrategy movingStrategy;

  public PacActor(Game game)
  {
    super(true, "sprites/pacpix.gif", nbSprites);  // Rotatable
    this.game = game;
    movingStrategy = new DirectStrategy();
  }
  private boolean isAuto = false;

  public void setAuto(boolean auto) {
    isAuto = auto;
  }


  public void setSeed(int seed) {
    this.seed = seed;
    randomiser.setSeed(seed);
  }

  public void keyRepeated(int keyCode)
  {
    if (isAuto) {
      return;
    }
    if (isRemoved())  // Already removed
      return;
    Location next = null;
    switch (keyCode)
    {
      case KeyEvent.VK_LEFT:
        next = getLocation().getNeighbourLocation(Location.WEST);
        setDirection(Location.WEST);
        break;
      case KeyEvent.VK_UP:
        next = getLocation().getNeighbourLocation(Location.NORTH);
        setDirection(Location.NORTH);
        break;
      case KeyEvent.VK_RIGHT:
        next = getLocation().getNeighbourLocation(Location.EAST);
        setDirection(Location.EAST);
        break;
      case KeyEvent.VK_DOWN:
        next = getLocation().getNeighbourLocation(Location.SOUTH);
        setDirection(Location.SOUTH);
        break;
    }
    if (next != null && canMove(next))
    {
      setLocation(next);
      eatPill(next);
    }
  }

  public void act()
  {
    show(idSprite);
    idSprite++;
    if (idSprite == nbSprites)
      idSprite = 0;

    if (isAuto) {
      Location next = movingStrategy.getNext(this, game);
      setLocation(next);
      eatPill(next);
      addVisitedList(next);
    }
    this.game.getGameCallback().pacManLocationChanged(getLocation(), score, nbPills);
  }



  private void addVisitedList(Location location)
  {
    visitedList.add(location);
    if (visitedList.size() == listLength)
      visitedList.remove(0);
  }

  public boolean isVisited(Location location)
  {
    for (Location loc : visitedList)
      if (loc.equals(location))
        return true;
    return false;
  }

  public boolean canMove(Location location)
  {
    Color c = getBackground().getColor(location);
    if ( c.equals(Color.gray) || location.getX() >= game.getNumHorzCells()
            || location.getX() < 0 || location.getY() >= game.getNumVertCells() || location.getY() < 0)
      return false;
    else
      return true;
  }

  public int getNbPills() {
    return nbPills;
  }

  private void eatPill(Location location)
  {
    // 检查通过传送门
    Color c = getBackground().getColor(location);
    if (c.equals(Color.black)) {
      Map<String, List<int[]>> portals = game.getCurrentMap().getPortals();
      for (Map.Entry<String, List<int[]>> entry : portals.entrySet()) {
        List<int[]> locs = entry.getValue();
        if (location.getX() == locs.get(0)[1] && location.getY() == locs.get(0)[0]) {
          // move to the second loc
          setLocation(new Location(locs.get(1)[1], locs.get(1)[0]));
        } else if (location.getX() == locs.get(1)[1] && location.getY() == locs.get(1)[0]) {
          // move to the first loc
          setLocation(new Location(locs.get(0)[1], locs.get(0)[0]));
        }
      }
    }
    else if (c.equals(Color.white))
    {
      nbPills++;
      score++;
      getBackground().fillCell(location, Color.lightGray);
      game.getGameCallback().pacManEatPillsAndItems(location, "pills");
    } else if (c.equals(Color.yellow)) {
      nbPills++;
      score+= 5;
      getBackground().fillCell(location, Color.lightGray);
      game.getGameCallback().pacManEatPillsAndItems(location, "gold");
      game.removeItem("gold",location);
    } else if (c.equals(Color.blue)) {
      getBackground().fillCell(location, Color.lightGray);
      game.getGameCallback().pacManEatPillsAndItems(location, "ice");
      game.removeItem("ice",location);
    }
    String title = "[PacMan in the Multiverse] Current score: " + score;
    gameGrid.setTitle(title);
  }


}
