package com.dt.style;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;

public class TextFieldWithButtonSkin extends StackPane {

    private final TextField textField;
    private final StackPane rightButton;
    private final Region rightButtonGraphic;

    public TextFieldWithButtonSkin() {
        this.textField = new TextField();

        rightButton = new StackPane();
        rightButton.getStyleClass().add("right-button");
        rightButton.setFocusTraversable(false);

        rightButtonGraphic = new Region();
        rightButtonGraphic.getStyleClass().add("right-button-graphic");

        rightButtonGraphic.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        rightButton.getChildren().add(rightButtonGraphic);

        // initially hidden
        rightButton.setVisible(false);

        getChildren().addAll(textField, rightButton);
        StackPane.setAlignment(rightButton, Pos.CENTER_RIGHT);

        setupListeners();
    }

    private void setupListeners() {

        ChangeListener<Object> updater = (obs, oldVal, newVal) -> updateButtonVisibility();

        textField.textProperty().addListener(updater);
        textField.focusedProperty().addListener(updater);

        rightButton.setOnMousePressed(e -> rightButtonPressed());
        rightButton.setOnMouseReleased(e -> rightButtonReleased());
    }

    private void updateButtonVisibility() {
        boolean visible = textField.isFocused() &&
                textField.getText() != null &&
                !textField.getText().isEmpty();

        rightButton.setVisible(visible);
    }

    protected void rightButtonPressed() {
    }

    protected void rightButtonReleased() {
    }

    public TextField getTextField() {
        return textField;
    }
}