package src.map.util;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameChecker {

    /*
    If passes, return the final file names will be used as a list of string
    If failed, return null
     */
    public static List<File> gameCheck(String folderName) {

        File folder = new File(folderName);

        Set<Integer> levelExist = new HashSet<>();

        if (!folder.isDirectory()) {
            System.out.println(folderName + ": " + "invalid folder name");
            return null;
        }
        if (folder.listFiles().length == 0) {
            System.out.println(folderName + ": " + "no maps found");
            return null;
        }

        // TODO: check if there are same maps
        File[] files = folder.listFiles();
        List<File> validMaps = new ArrayList<>();

        for (File file : files) {
            Pattern pattern = Pattern.compile("^(\\d+).*\\.xml");
            Matcher matcher = pattern.matcher(file.getName());
            if (matcher.find()) {
                int level = Integer.parseInt(matcher.group(1));
                if (levelExist.contains(level)) {
                    System.out.println(folderName + ": " + "duplicate levels");
                    return null;
                }
                levelExist.add(level);
                validMaps.add(file);
            }
        }

        // sort the levels
        validMaps.sort(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        return validMaps;

    }

}
