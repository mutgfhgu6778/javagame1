package src.map.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class LevelChecker {

    public static boolean levelCheck(String level, GameMap gameMap) {
        return onePacman(level, gameMap);
//                && isAccessible(level, gameMap);
    }

    // check 1:
    // exactly one starting point for PacMan
    public static boolean onePacman(String level, GameMap gameMap) {
        Map<String, List<int[]>> actors = gameMap.getActors();
        if (!actors.containsKey("pacman")) {
            System.out.println("Level " + level + "- no start for pacman");
            return false;
        }
        if (actors.get("pacman").size() != 1) {
            System.out.println("Level " + level + "- more than 1 start for pacman");
            return false;
        }
        return true;
    }

    // check 2
    // exactly two tiles for each portal appearing on the map



    // check 4
    // all elements are accessible
    public static boolean isAccessible(String level, GameMap gameMap) {

        int row = gameMap.getActors().get("pacman").get(0)[1];
        int col = gameMap.getActors().get("pacman").get(0)[0] ;
        String grid = gameMap.getMap();

        Queue<Integer> queue = new LinkedList<>();
        boolean[] visited = new boolean[grid.length()];
        visited[row * 11 + col] = true;
        int count = 0;

        for (int i = 0; i < grid.length(); i++) {
            if (grid.charAt(i) == 'g' || grid.charAt(i) == '.') {
                count++;
            }
        }

        queue.add(row * 11 + col);

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            int r = curr / 11;
            int c = curr % 11;

            if (grid.charAt(curr) == 'g' || grid.charAt(curr) == '.') {
                count--;
            }

            if (count == 0) {
                return true;
            }

            int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] dir : dirs) {
                int newRow = r + dir[0];
                int newCol = c + dir[1];

                if (newRow >= 0 && newRow < 20 && newCol >= 0 && newCol < 11 && grid.charAt(newRow * 11 + newCol) != 'x' && !visited[newRow * 11 + newCol]) {
                    visited[newRow * 11 + newCol] = true;
                    queue.add(newRow * 11 + newCol);
                }
            }
        }

        return false;
    }
}
