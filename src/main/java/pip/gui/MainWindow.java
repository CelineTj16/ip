package pip.gui;

import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    private static final String USER_IMG_PATH = "/images/user.jpeg";
    private static final String PIP_IMG_PATH = "/images/pip.jpg";

    private static final double AVATAR_SIZE = 300;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Pip pip;
    private Image rawUserImage = new Image(this.getClass().getResourceAsStream(USER_IMG_PATH));
    private Image rawPipImage = new Image(this.getClass().getResourceAsStream(PIP_IMG_PATH));

    private Image userImage;
    private Image pipImage;

    /**
     * Initializes the main window after its FXML elements have been loaded.
     * */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        userImage = makeCircularSnapshot(rawUserImage, AVATAR_SIZE);
        pipImage = makeCircularSnapshot(rawPipImage, AVATAR_SIZE);
    }

    /** Injects the Pip instance */
    public void setPip(Pip p) {
        pip = p;
        String greet = pip.getStartupGreeting();
        if (greet != null && !greet.isBlank()) {
            dialogContainer.getChildren().add(
                    DialogBox.getPipDialog(greet, pipImage)
            );
        }
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Pip's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();

        if (input == null || input.isBlank()) {
            return;
        }
        String response = pip.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getPipDialog(response, pipImage)
        );
        userInput.clear();
    }

    private static Image makeCircularSnapshot(Image src, double size) {
        ImageView iv = new ImageView(src);
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        iv.setPreserveRatio(false);

        Circle clip = new Circle(size / 2, size / 2, size / 2);
        iv.setClip(clip);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        WritableImage wi = new WritableImage((int) Math.ceil(size), (int) Math.ceil(size));
        WritableImage snapshot = iv.snapshot(params, wi);

        iv.setClip(null);
        return snapshot;
    }
}
