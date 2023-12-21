package src.map.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import src.Game;
import src.map.grid.Camera;
import src.map.grid.Grid;
import src.map.grid.GridCamera;
import src.map.grid.GridModel;
import src.map.grid.GridView;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import src.map.util.GameMap;
import src.map.util.MapLoader;
import src.map.util.MapReader;
import src.map.util.XMLMapReader;
import src.utility.GameCallback;
import src.utility.PropertiesLoader;


/**
 * Controller of the application.
 * 
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.5
 * 
 */
public class Controller implements ActionListener, GUIInformation {

	public static final String DEFAULT_PROPERTIES_PATH = "properties/test2.properties";

	/**
	 * The model of the map editor.
	 */
	private Grid model;

	private Tile selectedTile;
	private Camera camera;

	private List<Tile> tiles;

	private GridView grid;
	private View view;

	private int gridWith = Constants.MAP_WIDTH;
	private int gridHeight = Constants.MAP_HEIGHT;

	String propertiesPath = DEFAULT_PROPERTIES_PATH;
	// 使用xml adapter从XML文件读取地图
	MapReader mapReader = new XMLMapReader();
	MapLoader mapLoader = new MapLoader(mapReader);

	/**
	 * Construct the controller with no map
	 */
	public Controller() {
		init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
	}

	/**
	 * Construct the controller.
	 * mode 0: with a single map
	 * mode 1: with a folder
	 */
	public Controller(int mode, String name) {
		if (mode == 0) {
			List<GameMap> maps = new ArrayList<>();
			GameMap gameMap = mapLoader.loadMap(name);
			if (gameMap == null) {
				// failed level checking
				init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
				loadSingleFile(name);
			} else {
				maps.add(gameMap);
				final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
				GameCallback gameCallback = new GameCallback();
				new Game(gameCallback, properties, maps);
				init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
				loadSingleFile(name);
			}
		} else if (mode == 1) {
			// TODO: if level checking failed, open editor
			List<GameMap> maps = mapLoader.loadMaps(name);
			final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
			GameCallback gameCallback = new GameCallback();
			new Game(gameCallback, properties, maps);
			init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
		}
	}

	public void init(int width, int height) {
		this.tiles = TileManager.getTilesFromFolder("data");
		this.model = new GridModel(width, height, tiles.get(0).getCharacter());
		this.camera = new GridCamera(model, Constants.GRID_WIDTH,
				Constants.GRID_HEIGHT);

		grid = new GridView(this, camera, tiles); // Every tile is
		// 30x30 pixels

		this.view = new View(this, camera, grid, tiles);
	}

	/**
	 * Different commands that comes from the view.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		for (Tile t : tiles) {
			if (e.getActionCommand().equals(
					Character.toString(t.getCharacter()))) {
				selectedTile = t;
				break;
			}
		}
		if (e.getActionCommand().equals("flipGrid")) {
			// view.flipGrid();
		} else if (e.getActionCommand().equals("save")) {
			saveFile();
		} else if (e.getActionCommand().equals("load")) {
			loadFile();
		} else if (e.getActionCommand().equals("update")) {
			updateGrid(gridWith, gridHeight);
		} else if (e.getActionCommand().equals("start_game")) {
			System.out.println("start game");
			// TODO: switch mode
			// start playing the map currently have
			// by saving a tmp file and load it to the game
//			saveTmpFile();
//			this.view.close();
			List<GameMap> maps = new ArrayList<>();
			maps.add(mapLoader.loadMap("game/sample_map1.xml"));
			final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
			GameCallback gameCallback = new GameCallback();
			new Game(gameCallback, properties, maps);
//			init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
//			loadSingleFile("game/sample_map1.xml");
		}
	}

	public void updateGrid(int width, int height) {
		view.close();
		init(width, height);
		view.setSize(width, height);
	}

	DocumentListener updateSizeFields = new DocumentListener() {

		public void changedUpdate(DocumentEvent e) {
			gridWith = view.getWidth();
			gridHeight = view.getHeight();
		}

		public void removeUpdate(DocumentEvent e) {
			gridWith = view.getWidth();
			gridHeight = view.getHeight();
		}

		public void insertUpdate(DocumentEvent e) {
			gridWith = view.getWidth();
			gridHeight = view.getHeight();
		}
	};

	private void saveFile() {

		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"xml files", "xml");
		chooser.setFileFilter(filter);
		File workingDirectory = new File(System.getProperty("user.dir"));
		chooser.setCurrentDirectory(workingDirectory);

		int returnVal = chooser.showSaveDialog(null);
		try {
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				Element level = new Element("level");
				Document doc = new Document(level);
				doc.setRootElement(level);

				Element size = new Element("size");
				int height = model.getHeight();
				int width = model.getWidth();
				size.addContent(new Element("width").setText(width + ""));
				size.addContent(new Element("height").setText(height + ""));
				doc.getRootElement().addContent(size);

				for (int y = 0; y < height; y++) {
					Element row = new Element("row");
					for (int x = 0; x < width; x++) {
						char tileChar = model.getTile(x,y);
						String type = "PathTile";

						if (tileChar == 'b')
							type = "WallTile";
						else if (tileChar == 'c')
							type = "PillTile";
						else if (tileChar == 'd')
							type = "GoldTile";
						else if (tileChar == 'e')
							type = "IceTile";
						else if (tileChar == 'f')
							type = "PacTile";
						else if (tileChar == 'g')
							type = "TrollTile";
						else if (tileChar == 'h')
							type = "TX5Tile";
						else if (tileChar == 'i')
							type = "PortalWhiteTile";
						else if (tileChar == 'j')
							type = "PortalYellowTile";
						else if (tileChar == 'k')
							type = "PortalDarkGoldTile";
						else if (tileChar == 'l')
							type = "PortalDarkGrayTile";

						Element e = new Element("cell");
						row.addContent(e.setText(type));
					}
					doc.getRootElement().addContent(row);
				}
				XMLOutputter xmlOutput = new XMLOutputter();
				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput
						.output(doc, new FileWriter(chooser.getSelectedFile()));
			}
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null, "Invalid file!", "error",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
		}
	}

	public void saveTmpFile() {
		File workingDirectory = new File(System.getProperty("user.dir"));
		try {
			Element level = new Element("level");
			Document doc = new Document(level);
			doc.setRootElement(level);

			Element size = new Element("size");
			int height = model.getHeight();
			int width = model.getWidth();
			size.addContent(new Element("width").setText(width + ""));
			size.addContent(new Element("height").setText(height + ""));
			doc.getRootElement().addContent(size);

			for (int y = 0; y < height; y++) {
				Element row = new Element("row");
				for (int x = 0; x < width; x++) {
					char tileChar = model.getTile(x,y);
					String type = "PathTile";

					if (tileChar == 'b')
						type = "WallTile";
					else if (tileChar == 'c')
						type = "PillTile";
					else if (tileChar == 'd')
						type = "GoldTile";
					else if (tileChar == 'e')
						type = "IceTile";
					else if (tileChar == 'f')
						type = "PacTile";
					else if (tileChar == 'g')
						type = "TrollTile";
					else if (tileChar == 'h')
						type = "TX5Tile";
					else if (tileChar == 'i')
						type = "PortalWhiteTile";
					else if (tileChar == 'j')
						type = "PortalYellowTile";
					else if (tileChar == 'k')
						type = "PortalDarkGoldTile";
					else if (tileChar == 'l')
						type = "PortalDarkGrayTile";

					Element e = new Element("cell");
					row.addContent(e.setText(type));
				}
				doc.getRootElement().addContent(row);
			}
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput
					.output(doc, new FileWriter("tmp.xml"));
		} catch (IOException e) {
		}
	}

	public void loadSingleFile(String fileName) {
		SAXBuilder builder = new SAXBuilder();
		try {
			File selectedFile = new File(fileName);
			if (selectedFile.canRead() && selectedFile.exists()) {
				Document document;
				document = (Document) builder.build(selectedFile);

				Element rootNode = document.getRootElement();

				List sizeList = rootNode.getChildren("size");
				Element sizeElem = (Element) sizeList.get(0);
				int height = Integer.parseInt(sizeElem
						.getChildText("height"));
				int width = Integer
						.parseInt(sizeElem.getChildText("width"));
				updateGrid(width, height);

				List rows = rootNode.getChildren("row");
				for (int y = 0; y < rows.size(); y++) {
					Element cellsElem = (Element) rows.get(y);
					List cells = cellsElem.getChildren("cell");

					for (int x = 0; x < cells.size(); x++) {
						Element cell = (Element) cells.get(x);
						String cellValue = cell.getText();

						char tileNr = 'a';
						if (cellValue.equals("PathTile"))
							tileNr = 'a';
						else if (cellValue.equals("WallTile"))
							tileNr = 'b';
						else if (cellValue.equals("PillTile"))
							tileNr = 'c';
						else if (cellValue.equals("GoldTile"))
							tileNr = 'd';
						else if (cellValue.equals("IceTile"))
							tileNr = 'e';
						else if (cellValue.equals("PacTile"))
							tileNr = 'f';
						else if (cellValue.equals("TrollTile"))
							tileNr = 'g';
						else if (cellValue.equals("TX5Tile"))
							tileNr = 'h';
						else if (cellValue.equals("PortalWhiteTile"))
							tileNr = 'i';
						else if (cellValue.equals("PortalYellowTile"))
							tileNr = 'j';
						else if (cellValue.equals("PortalDarkGoldTile"))
							tileNr = 'k';
						else if (cellValue.equals("PortalDarkGrayTile"))
							tileNr = 'l';
						else
							tileNr = '0';

						model.setTile(x, y, tileNr);
					}
					String mapString = model.getMapAsString();
					grid.redrawGrid();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadFile() {
		SAXBuilder builder = new SAXBuilder();
		try {
			JFileChooser chooser = new JFileChooser();
			File selectedFile;
			BufferedReader in;
			FileReader reader = null;
			File workingDirectory = new File(System.getProperty("user.dir"));
			chooser.setCurrentDirectory(workingDirectory);

			int returnVal = chooser.showOpenDialog(null);
			Document document;
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				selectedFile = chooser.getSelectedFile();
				if (selectedFile.canRead() && selectedFile.exists()) {
					document = (Document) builder.build(selectedFile);

					Element rootNode = document.getRootElement();

					List sizeList = rootNode.getChildren("size");
					Element sizeElem = (Element) sizeList.get(0);
					int height = Integer.parseInt(sizeElem
							.getChildText("height"));
					int width = Integer
							.parseInt(sizeElem.getChildText("width"));
					updateGrid(width, height);

					List rows = rootNode.getChildren("row");
					for (int y = 0; y < rows.size(); y++) {
						Element cellsElem = (Element) rows.get(y);
						List cells = cellsElem.getChildren("cell");

						for (int x = 0; x < cells.size(); x++) {
							Element cell = (Element) cells.get(x);
							String cellValue = cell.getText();

							char tileNr = 'a';
							if (cellValue.equals("PathTile"))
								tileNr = 'a';
							else if (cellValue.equals("WallTile"))
								tileNr = 'b';
							else if (cellValue.equals("PillTile"))
								tileNr = 'c';
							else if (cellValue.equals("GoldTile"))
								tileNr = 'd';
							else if (cellValue.equals("IceTile"))
								tileNr = 'e';
							else if (cellValue.equals("PacTile"))
								tileNr = 'f';
							else if (cellValue.equals("TrollTile"))
								tileNr = 'g';
							else if (cellValue.equals("TX5Tile"))
								tileNr = 'h';
							else if (cellValue.equals("PortalWhiteTile"))
								tileNr = 'i';
							else if (cellValue.equals("PortalYellowTile"))
								tileNr = 'j';
							else if (cellValue.equals("PortalDarkGoldTile"))
								tileNr = 'k';
							else if (cellValue.equals("PortalDarkGrayTile"))
								tileNr = 'l';
							else
								tileNr = '0';

							model.setTile(x, y, tileNr);
						}
					}

					String mapString = model.getMapAsString();
					grid.redrawGrid();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tile getSelectedTile() {
		return selectedTile;
	}
}
