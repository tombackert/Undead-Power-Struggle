package ups.view;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.input.MouseEvent;
import ups.controller.GameBoardController;
import ups.gui.ColorMapping;
import ups.model.GameBoard;

import javafx.scene.input.ScrollEvent;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The GameBoardView class is responsible for displaying the game board.
 */
public class GameBoardView extends Pane {
    private static final Logger logger = Logger.getLogger(GameBoardView.class.getName());

    private final GameBoard model; // Referenz auf das Model
    public static final int BOARD_WIDTH = 20; // Breite des Spielfelds
    public static final int BOARD_HEIGHT = 20; // Höhe des Spielfelds
    private final List<Group> hexGroups = new ArrayList<>(); // Liste der Gruppen für die Hexagone
    private final Color[][] originalColors = new Color[BOARD_HEIGHT][BOARD_WIDTH]; // Speichert die Originalfarben
    private GameBoardController controller; // Referenz auf den Controller


    private double scaleValue = 1.0; // Initial scale
    private static final double MAX_SCALE = 3.0; // Maximum zoom scale
    private static final double MIN_SCALE = 0.5; // Minimum zoom scale
    /**
     * Constructs a new GameBoardView with the given model.
     *
     * @param model the game board model
     */
    public GameBoardView(GameBoard model) {
        this.model = model; // Setze das Model
        initializeBoard(); // Initialisiere das Spielfeld
        widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> updateBoard()); // Füge einen Listener für die Breite hinzu
        heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> updateBoard()); // Füge einen Listener für die Höhe hinzu
    }

    /**
     * Sets the controller for the game board view.
     *
     * @param controller the game board controller
     */
    public void setController(GameBoardController controller) {
        this.controller = controller; // Setze den Controller
        setupEventHandlers(); // Setze die Event-Handler
          // Add mouse scroll event handler for zooming
          setOnScroll(event -> handleZoom(event));
    }

     /**
     * handles the zooming with the mouse
     * @param event the scroll event containing details about the mouse scroll action
     */
    private void handleZoom(ScrollEvent event) {
        if (event.getDeltaY() == 0) {
            return;
        }
    
        double scaleFactor = (event.getDeltaY() > 0) ? 1.1 : 0.9;
    
        double newScale = scaleValue * scaleFactor;
        if (newScale > MAX_SCALE) {
            newScale = MAX_SCALE;
        } else if (newScale < MIN_SCALE) {
            newScale = MIN_SCALE;
        }
    
        // Apply zoom at mouse pointer
        double f = (newScale / scaleValue) - 1;
        double dx = (event.getX() - (getWidth() / 2));
        double dy = (event.getY() - (getHeight() / 2));
    
        setScaleX(newScale);
        setScaleY(newScale);
        setTranslateX(getTranslateX() - f * dx);
        setTranslateY(getTranslateY() - f * dy);
    
        scaleValue = newScale;
    }

 
    /**
     * Sets up the event handlers for the hexagons.
     */
    private void setupEventHandlers() {
        for (int i = 0; i < hexGroups.size(); i++) {
            Group hexGroup = hexGroups.get(i);
            Polygon hex = (Polygon) hexGroup.getChildren().get(0);
            int row = i / BOARD_WIDTH;
            int col = i % BOARD_WIDTH;
            hex.setOnMouseClicked(event -> handleHexagonClick(hexGroup, row, col));
            hex.setOnMouseEntered(event -> handleHexagonMouseEntered(event, row, col));
            hex.setOnMouseExited(event -> handleHexagonMouseExited(event, row, col));
        }
    }

    /**
     * Initializes the game board.
     */
    private void initializeBoard() {
        System.out.println("Initializing board...");
        for (int i = 0; i < BOARD_HEIGHT; i++) { // Iteriere über die Zeilen
            for (int j = 0; j < BOARD_WIDTH; j++) { // Iteriere über die Spalten
                Polygon hex = createHexagon(); // Erstelle ein neues Hexagon
                Color color = model.getColor(i, j); // Hole die Farbe aus dem Model
                hex.setFill(color); // Setze die Füllfarbe
                hex.setStroke(Color.BLACK); // Setze die Linienfarbe

                originalColors[i][j] = color; // Speichere die Originalfarbe

                Group hexGroup = new Group(hex); // Erstelle eine neue Gruppe
                hexGroups.add(hexGroup); // Füge die Gruppe zur Liste hinzu
                getChildren().add(hexGroup); // Füge die Gruppe zum Pane hinzu
            }
        }
        updateBoard();  // Stelle sicher, dass das Board aktualisiert wird, nachdem die Gruppen hinzugefügt wurden
    }

    /**
     * Updates the game board.
     */
    public void updateBoard() {
        double hexSize = Math.min(getWidth() / (BOARD_WIDTH * Math.sqrt(3)), getHeight() / (BOARD_HEIGHT * 1.5)); // Berechne die Größe der Hexagone
        double hexWidth = Math.sqrt(3) * hexSize; // Berechne die Breite der Hexagone
        double hexHeight = 2 * hexSize; // Berechne die Höhe der Hexagone

        // Berechne die notwendige Verschiebung, um die Hexagone zu zentrieren
        double xOffset = (getWidth() - (BOARD_WIDTH * hexWidth + (hexWidth / 2))) / 2; // Berechne den X-Offset
        double yOffset = (getHeight() - (BOARD_HEIGHT * hexHeight * 0.75 + hexHeight / 4)) / 2; // Berechne den Y-Offset

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Group hexGroup = hexGroups.get(i * BOARD_WIDTH + j); // Hole die Gruppe für das aktuelle Hexagon
                Polygon hex = (Polygon) hexGroup.getChildren().get(0); // Hole das Hexagon aus der Gruppe
                hex.getPoints().clear(); // Lösche die alten Punkte
                setHexagonPoints(hex, hexSize); // Setze die neuen Punkte

                // Füge die berechneten Offsets hinzu
                hexGroup.setLayoutX(xOffset + j * hexWidth + (i % 2) * hexWidth / 2); // Berechne die X-Position
                hexGroup.setLayoutY(yOffset + i * hexHeight * 0.75); // Berechne die Y-Position

                // Aktualisiere die Größe und Position der Bildansicht innerhalb der Gruppe
                if (hexGroup.getChildren().size() > 1) { // Überprüfe, ob die Gruppe ein Bild enthält
                    ImageView houseImageView = (ImageView) hexGroup.getChildren().get(1); // Hole die Bildansicht
                    updateHouseImageView(houseImageView, hexSize, hexWidth, hexHeight); // Aktualisiere die Bildansicht
                }
            }
        }
    }

    /**
     * Updates the house image view.
     *
     * @param houseImageView the house image view
     * @param hexSize        the size of the hexagon
     * @param hexWidth       the width of the hexagon
     * @param hexHeight      the height of the hexagon
     */
    public void updateHouseImageView(ImageView houseImageView, double hexSize, double hexWidth, double hexHeight) {
        double imageSize = hexSize * 1.55; // Größe des Bildes im Verhältnis zur Hexagon-Größe
        houseImageView.setFitWidth(imageSize); // Setze die Breite
        houseImageView.setFitHeight(imageSize); // Setze die Höhe
        houseImageView.setPreserveRatio(true); // Bewahre das Seitenverhältnis

        houseImageView.setLayoutX((hexWidth - 1.6 * imageSize)); // Setze die X-Position
        houseImageView.setLayoutY((hexHeight - 1.8 * imageSize)); // Setze die Y-Position
    }

    /**
     * Creates a new hexagon.
     *
     * @return the hexagon
     */
    private Polygon createHexagon() {
        Polygon hex = new Polygon();
        setHexagonPoints(hex, 10); // Standardgröße für Initialisierung
        return hex;
    }

    /**
     * Sets the points of the hexagon.
     *
     * @param hexagon the hexagon
     * @param size    the size of the hexagon
     */
    private void setHexagonPoints(Polygon hexagon, double size) {
        hexagon.getPoints().clear(); // Leere die Punkte vor dem Setzen neuer Punkte
        for (int i = 0; i < 6; i++) { // Iteriere über die sechs Ecken
            double angle = 2 * Math.PI / 6 * (i + 0.5); // Berechne den Winkel
            double x = size * Math.cos(angle); // Berechne die X-Koordinate
            double y = size * Math.sin(angle); // Berechne die Y-Koordinate
            hexagon.getPoints().addAll(x, y); // Füge die Koordinaten hinzu
        }
    }

    /**
     * Returns the original color of the hexagon at the given row and column.
     *
     * @param row the row
     * @param col the column
     * @return the original color
     */
    public Color getOriginalColor(int row, int col) {
        return originalColors[row][col]; // Gebe die Originalfarbe zurück
    }

    /**
     * Handles the click event on a hexagon.
     *
     * @param hexGroup the hexagon group
     * @param row      the row
     * @param col      the column
     */
    private void handleHexagonClick(Group hexGroup, int row, int col) {
        if (controller != null) {
            controller.handleHexagonClick(hexGroup, row, col);
        }
    }

    /**
     * Handles the mouse entered event on a hexagon.
     *
     * @param event the mouse event
     * @param row   the row
     * @param col   the column
     */
    private void handleHexagonMouseEntered(MouseEvent event, int row, int col) {
        Polygon hex = (Polygon) event.getSource();
        Color originalColor = getOriginalColor(row, col);
        hex.setFill(originalColor.darker());
    }

    /**
     * Handles the mouse exited event on a hexagon.
     *
     * @param event the mouse event
     * @param row   the row
     * @param col   the column
     */
    private void handleHexagonMouseExited(MouseEvent event, int row, int col) {
        Polygon hex = (Polygon) event.getSource();
        Color originalColor = getOriginalColor(row, col);
        hex.setFill(originalColor);
    }

    /**
     * Adds a house to the hexagon at the given row and column.
     *
     * @param hexGroup the hexagon group
     * @param color    the color of the house
     */
    public void addHouseToHexagon(Group hexGroup, Color color) {
        String colorName = ColorMapping.getStringFromColor(color).toLowerCase();
        ImageView houseImageView = loadHouseImage(colorName);
        if (houseImageView != null) {
            updateHouseImageView(houseImageView, getHexSize(), getHexWidth(), getHexHeight());

            if (hexGroup.getChildren().size() > 1) {
                hexGroup.getChildren().remove(1);
            }

            hexGroup.getChildren().add(houseImageView);
        }
    }

    public ImageView loadHouseImage(String colorName) {
        try {
            return new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/settlements/haus_" + colorName + ".png"))));
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Image for color " + colorName + " not found.", e);
            return null;
        }
    }

    private double getHexSize() {
        return Math.min(getWidth() / (BOARD_WIDTH * Math.sqrt(3)), getHeight() / (BOARD_HEIGHT * 1.5));
    }

    private double getHexWidth() {
        return Math.sqrt(3) * getHexSize();
    }

    private double getHexHeight() {
        return 2 * getHexSize();
    }
}
