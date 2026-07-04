package com.dt.style;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class MetroPasswordFieldSkin extends StackPane {

    private final PasswordField passwordField = new PasswordField();
    private final TextField textField = new TextField();
    private boolean showing = false;

    public MetroPasswordFieldSkin() {
        textField.setManaged(false);
        textField.setVisible(false);

        getChildren().addAll(passwordField, textField);

        // sync text both ways
        textField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    public void toggleVisibility() {
        showing = !showing;

        textField.setVisible(showing);
        textField.setManaged(showing);

        passwordField.setVisible(!showing);
        passwordField.setManaged(!showing);
    }

    public String getText() {
        return passwordField.getText();
    }
}