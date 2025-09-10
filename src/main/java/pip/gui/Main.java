package pip.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * JavaFX entry point for the Pip application.
 * </p>
 */
public class Main extends Application {
    private static final String MAIN_WINDOW_FXML = "/view/MainWindow.fxml";
    private static final String APP_TITLE = "Pip";
    private final Pip pip = new Pip("data/pip.txt");

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(MAIN_WINDOW_FXML));
        AnchorPane ap = fxmlLoader.load();

        MainWindow controller = fxmlLoader.getController();
        controller.setPip(pip);

        Scene scene = new Scene(ap);
        stage.setScene(scene);
        stage.setMinHeight(220);
        stage.setMinWidth(417);
        stage.setTitle(APP_TITLE);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
