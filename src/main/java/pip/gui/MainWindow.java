package pip.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    private static final String USER_IMG_PATH = "/images/user.jpeg";
    private static final String PIP_IMG_PATH = "/images/pip.jpg";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Pip pip;
    private Image userImage = new Image(this.getClass().getResourceAsStream(USER_IMG_PATH));
    private Image pipImage = new Image(this.getClass().getResourceAsStream(PIP_IMG_PATH));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
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
}
