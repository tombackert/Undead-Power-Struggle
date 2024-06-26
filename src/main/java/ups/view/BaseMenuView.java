package ups.view;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import ups.model.MenuItem;
import ups.model.Title;

import java.io.InputStream;
import java.util.List;

public abstract class BaseMenuView extends Application {

    private static final int WIDTH = 1488;
    private static final int HEIGHT = 850;
    protected Stage primaryStage;

    protected Pane root = new Pane();
    protected VBox menuBox = new VBox(-5);
    private Line line;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Scene scene = new Scene(createContent());
        primaryStage.setTitle(getTitle());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Parent createContent() {
        addBackground();
        addTitle();

        double lineX = WIDTH / 2 - 100;
        double lineY = HEIGHT / 3 + 50;

        addLine(lineX, lineY);
        addMenu(lineX + 5, lineY + 5);

        startAnimation();

        return root;
    }

    private void addBackground() {
        InputStream inputStream = getClass().getResourceAsStream(getBackgroundImage());
        if (inputStream != null) {
            Image image = new Image(inputStream);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(WIDTH);
            imageView.setFitHeight(HEIGHT);
            root.getChildren().add(imageView);
        } else {
            System.err.println("Die Bilddatei wurde nicht gefunden!");
        }
    }

    private void addTitle() {
        Title title = new Title(getTitleText());
        title.setTranslateX(WIDTH / 2 - title.getTitleWidth() / 2);
        title.setTranslateY(HEIGHT / 3);

        root.getChildren().add(title);
    }

    private void addLine(double x, double y) {
        line = new Line(x, y, x, y + 300);
        line.setStrokeWidth(3);
        line.setStroke(Color.color(1, 1, 1, 0.75));
        line.setEffect(new DropShadow(5, Color.BLACK));
        line.setScaleY(0);

        root.getChildren().add(line);
    }

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

    protected void addMenu(double x, double y) {
        menuBox.setTranslateX(x);
        menuBox.setTranslateY(y);

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

    protected abstract String getTitle();

    protected abstract String getTitleText();

    protected abstract String getBackgroundImage();

    protected abstract List<Pair<String, Runnable>> getMenuData();
}
