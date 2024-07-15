package ups.view;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import ups.model.MenuItem;
import ups.model.Title;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import ups.controller.GameMenuController;
import ups.controller.KingdomBuilderCardsController;
import ups.view.GameMenuView;

/**
 * Base class for menu views.
 */
public abstract class BaseMenuView extends Application {

    // Attributes
    protected static final int WIDTH = 1488;
    protected static final int HEIGHT = 850;
    protected Stage primaryStage;
    public Pane root = new Pane();
    protected VBox menuBox = new VBox(-5);
    protected static Line line;
    private ImageView imageView;
    protected VBox cardBox = new VBox(-5);
    

    /**
     * Start method for menu view. 
     * @param primaryStage stage of the menu view
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Scene scene = new Scene(createContent(), WIDTH, HEIGHT);
        primaryStage.setTitle(getTitle());
        primaryStage.setScene(scene);
        updateBackgroundSize();
        primaryStage.show();

        // Add listeners to update background image on window resize
        scene.widthProperty().addListener((obs, oldVal, newVal) -> updateBackgroundSize());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updateBackgroundSize());
    }
    
    /**
     * Create content for menu view.
     * @return parent object
     */
    private Parent createContent() {
        addBackground();
        addTitle();
        addLine();
        addMenu();

        startAnimation();

        return root;
    }

    /**
     * Add background image to menu view.
     */
    private void addBackground() {
        InputStream inputStream = getClass().getResourceAsStream(getBackgroundImage());
        if (inputStream != null) {
            Image image = new Image(inputStream);
            imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            root.widthProperty().addListener((obs, oldVal, newVal) -> updateBackgroundSize());
            root.heightProperty().addListener((obs, oldVal, newVal) -> updateBackgroundSize());

            root.getChildren().add(imageView);
            updateBackgroundSize();
        } else {
            System.err.println("Die Bilddatei wurde nicht gefunden!");
        }
    }

    /**
     * Update background size based on window size while preserving aspect ratio.
     */
    private void updateBackgroundSize() {
        double windowWidth = root.getWidth();
        double windowHeight = root.getHeight();

        double imageWidth = imageView.getImage().getWidth();
        double imageHeight = imageView.getImage().getHeight();

        double widthRatio = windowWidth / imageWidth;
        double heightRatio = windowHeight / imageHeight;

        double scale = Math.max(widthRatio, heightRatio);

        imageView.setFitWidth(imageWidth * scale);
        imageView.setFitHeight(imageHeight * scale);

        // Center the image if it exceeds window size
        if (imageView.getFitWidth() > windowWidth) {
            imageView.setTranslateX((windowWidth - imageView.getFitWidth()) / 2);
        } else {
            imageView.setTranslateX(0);
        }

        if (imageView.getFitHeight() > windowHeight) {
            imageView.setTranslateY((windowHeight - imageView.getFitHeight()) / 2);
        } else {
            imageView.setTranslateY(0);
        }
    }

    /**
     * Add title to menu view.
     */
    private void addTitle() {
        Title title = new Title(getTitleText());
        title.translateXProperty().bind(root.widthProperty().subtract(title.widthProperty()).divide(2));
        title.setTranslateY(HEIGHT / 3);

        root.getChildren().add(title);
    }

    /**
     * Add line to menu view.
     */
    protected void addLine() {
        line = new Line(WIDTH / 2, HEIGHT / 3 + 50, WIDTH / 2, HEIGHT / 3 + 350);
        line.setStrokeWidth(3);
        line.setStroke(Color.color(1, 1, 1, 0.75));
        line.setEffect(new DropShadow(5, Color.BLACK));
        line.setScaleY(0);

        // Bind the line's X position to be slightly left of the menuBox
        line.startXProperty().bind(menuBox.translateXProperty().subtract(10));
        line.endXProperty().bind(menuBox.translateXProperty().subtract(10));

        root.getChildren().add(line);
    }

    /**
     * Start animation for menu view.
     */
    private void startAnimation() {
        ScaleTransition st = new ScaleTransition(Duration.seconds(1), line);
        st.setToY(1);
        st.setOnFinished(e -> {
            for (int i = 0; i < menuBox.getChildren().size(); i++) {
                Node n = menuBox.getChildren().get(i);

                TranslateTransition tt = new TranslateTransition(Duration.seconds(1 + i * 0.15), n);
                tt.setToX(0);
                tt.setOnFinished(e2 -> n.setClip(null));
                tt.play();
            }
        });
        st.play();
    }

    /**
     * Add menu to menu view.
     */
    protected void addMenu() {
        // Bind menu box position to be centered
        menuBox.translateXProperty().bind(root.widthProperty().subtract(menuBox.widthProperty()).divide(2));
        menuBox.setTranslateY(HEIGHT / 3 + 55);

        // Add menu items
        List<Pair<String, Runnable>> menuData = getMenuData();
        menuData.forEach(data -> {
            MenuItem item = new MenuItem(data.getKey());
            item.setOnAction(data.getValue());
            item.setTranslateX(-300);

            Rectangle clip = new Rectangle(300, 30);
            clip.translateXProperty().bind(item.translateXProperty().negate());

            item.setClip(clip);

            menuBox.getChildren().addAll(item);
        });

        root.getChildren().add(menuBox);
    }

    /**
     * Show a card image on the screen.
     * @param pathToCard Path to the card image.
     */
    public void showCard(String pathToCard) {
        try {
            // Log the path to the card
            //System.out.println("Showing card: " + pathToCard);

            // Load the image
            FileInputStream input = new FileInputStream(pathToCard);
            Image image = new Image(input);
            ImageView imageView = new ImageView(image);

            // Check if the image is loaded
            if (image.isError()) {
                System.err.println("Error loading image: " + image.getException());
                return;
            }

            // Bind image view position to be on the right side of the menu
            cardBox.translateXProperty()
                    .bind(root.widthProperty().subtract(cardBox.widthProperty()).divide(2).add(300));
            cardBox.setTranslateY(HEIGHT / 3 + 55);

            // Set image view properties
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(230); // (230)
            imageView.setTranslateX(0); // (980)
            imageView.setTranslateY(0); // (330)

            // Clear previous images if any
            cardBox.getChildren().clear();

            // Add the new image view to the card box
            cardBox.getChildren().add(imageView);

            // Ensure cardBox is added to the root if not already present
            if (!root.getChildren().contains(cardBox)) {
                root.getChildren().add(cardBox);
            }

            // Log successful addition
            //System.out.println("Card image added to the scene.");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Show a card image on the screen.
     * @param pathToCard Path to the card image.
     */
    public void showTutorial(String pathToCard) {
        try {
            // Log the path to the card
            //System.out.println("Showing card: " + pathToCard);
            
            // Load the image
            FileInputStream input = new FileInputStream(pathToCard);
            Image image = new Image(input);
            ImageView imageView = new ImageView(image);

            // Check if the image is loaded
            if (image.isError()) {
                System.err.println("Error loading image: " + image.getException());
                return;
            }
            
            // Bind image view position to be on the right side of the menu
            cardBox.translateXProperty().bind(root.widthProperty().subtract(cardBox.widthProperty()).divide(2).add(370));
            cardBox.setTranslateY(HEIGHT / 3 + 50);

            // Set image view properties
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(490); // (230)
            imageView.setTranslateX(0); // (980)
            imageView.setTranslateY(0); // (330)

            // Clear previous images if any
            cardBox.getChildren().clear();

            // Add the new image view to the card box
            cardBox.getChildren().add(imageView);

            // Ensure cardBox is added to the root if not already present
            if (!root.getChildren().contains(cardBox)) {
                root.getChildren().add(cardBox);
            }
                
            // Log successful addition
            //System.out.println("Card image added to the scene.");
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get title of menu view.
     * @return title of menu view
     */
    protected abstract String getTitle();

    /**
     * Get title text of menu view.
     * @return title text of menu view
     */
    protected abstract String getTitleText();

    /**
     * Get background image of menu view.
     * @return background image of menu view
     */
    protected abstract String getBackgroundImage();

    /**
     * Get menu data of menu view.
     * @return menu data of menu view
     */
    protected abstract List<Pair<String, Runnable>> getMenuData();
}
