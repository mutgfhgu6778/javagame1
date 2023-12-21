package src.move;

import ch.aplu.jgamegrid.*;
import src.Game;
import src.PacActor;

public interface MovingStrategy {

    Location getNext(PacActor pacman, Game game);

}
