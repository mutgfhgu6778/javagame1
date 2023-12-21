package src.map.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Convert XML file into Map<String, String>
 */
public class XMLMapReader implements MapReader {

    @Override
    public GameMap readMapFile(String filename) {

        // store actors with their starting positions
        Map<String, List<int[]>> actors = new HashMap<>();

        // store all portals
        Map<String, List<int[]>> portals = new HashMap<>();

        // create a new DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // create a new DocumentBuilder
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            // parse an XML file into a DOM tree
            Document document = builder.parse(new File(filename));

            // get the root element of the document
            Element root = document.getDocumentElement();

            // get all the row elements
            NodeList rows = root.getElementsByTagName("row");

            // create a new map to store the cell values
            StringBuilder sb = new StringBuilder();

            // loop over all the row elements
            for (int i = 0; i < rows.getLength(); i++) {
                // get the current row element
                Element row = (Element) rows.item(i);

                // get all the cell elements in this row
                NodeList cells = row.getElementsByTagName("cell");

                // loop over all the cell elements in this row
                for (int j = 0; j < cells.getLength(); j++) {
                    // get the current cell element
                    Element cell = (Element) cells.item(j);

                    // get the text content of the cell element
                    String cellValue = cell.getTextContent();

                    // put the cell value into the map
                    switch (cellValue) {
                        case "WallTile":
                            sb.append("x");
                            break;
                        case "PillTile":
                            sb.append(".");
                            break;
                        case "PathTile":
                            sb.append(" ");
                            break;
                        case "PacTile":
                            addActor(actors, "pacman", new int[] {j, i});
                            sb.append(" ");
                            break;
                        case "TX5Tile":
                            addActor(actors, "TX5", new int[] {j, i});
                            sb.append(" ");
                            break;
                        case "TrollTile":
                            addActor(actors, "Troll", new int[] {j, i});
                            sb.append(" ");
                            break;
                        case "GoldTile":
                            sb.append("g");
                            break;
                        case "IceTile":
                            sb.append("i");
                            break;
                        case "PortalWhiteTile":
                            sb.append("1");
                            addActor(portals, "PortalWhiteTile", new int[] {i, j});
                            break;
                        case "PortalYellowTile":
                            sb.append("2");
                            addActor(portals, "PortalYellowTile", new int[] {i, j});
                            break;
                        case "PortalDarkGoldTile":
                            sb.append("3");
                            addActor(portals, "PortalDarkGoldTile", new int[] {i, j});
                            break;
                        case "PortalDarkGrayTile":
                            sb.append("4");
                            addActor(portals, "PortalDarkGrayTile", new int[] {i, j});
                        default:
                            sb.append(" ");
                            break;
                    }
                }
            }
            System.out.println(sb.toString());
            return new GameMap(actors, portals, sb.toString());

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addActor(Map<String, List<int[]>> actors, String actorName, int[] loc) {
        if (!actors.containsKey(actorName)) {
            List<int[]> locs = new ArrayList<>();
            locs.add(loc);
            actors.put(actorName, locs);
        } else {
            actors.get(actorName).add(loc);
        }
    }

}