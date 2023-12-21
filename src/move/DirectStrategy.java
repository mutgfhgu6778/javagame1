package src.move;

import ch.aplu.jgamegrid.*;
import src.Game;
import src.PacActor;

import java.util.List;
import java.util.Random;

public class DirectStrategy implements MovingStrategy {

    private Random randomiser;

    private Location closestPillLocation(PacActor pacman, Game game) {
        int currentDistance = 1000;
        Location currentLocation = null;
        List<Location> pillAndItemLocations = game.getPillAndItemLocations();
        for (Location location: pillAndItemLocations) {
            int distanceToPill = location.getDistanceTo(pacman.getLocation());
            if (distanceToPill < currentDistance) {
                currentLocation = location;
                currentDistance = distanceToPill;
            }
        }

        return currentLocation;
    }

    @Override
    public Location getNext(PacActor pacman, Game game) {
        Location closestPill = closestPillLocation(pacman, game);
        double oldDirection = pacman.getDirection();

        Location.CompassDirection compassDir =
                pacman.getLocation().get4CompassDirectionTo(closestPill);
        Location next = pacman.getLocation().getNeighbourLocation(compassDir);
        pacman.setDirection(compassDir);
        if (!pacman.isVisited(next) && pacman.canMove(next)) {
            return next;
        } else {
            // normal movement
            randomiser = pacman.getRandom();
            int sign = randomiser.nextDouble() < 0.5 ? 1 : -1;
            pacman.setDirection(oldDirection);
            pacman.turn(sign * 90);  // Try to turn left/right
            next = pacman.getNextMoveLocation();
            if (pacman.canMove(next)) {
                return next;
            } else {
                pacman.setDirection(oldDirection);
                next = pacman.getNextMoveLocation();
                if (pacman.canMove(next)) // Try to move forward
                {
                    return next;
                } else {
                    pacman.setDirection(oldDirection);
                    pacman.turn(-sign * 90);  // Try to turn right/left
                    next = pacman.getNextMoveLocation();
                    if (pacman.canMove(next)) {
                        return next;
                    } else {
                        pacman.setDirection(oldDirection);
                        pacman.turn(180);  // Turn backward
                        next = pacman.getNextMoveLocation();
                        return next;
                    }
                }
            }
        }
    }


}
